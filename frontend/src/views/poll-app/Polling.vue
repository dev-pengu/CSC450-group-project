<template>
  <div class="polling">
    <v-navigation-drawer v-model="filterBarOpen" fixed temporary right width="325px">
      <v-sheet class="pa-5">
        <v-row>
          <v-col cols="12">
            <div class="d-flex justify-space-between align-center">
              <h3 class="text-uppercase foa_text_header--text">Active Filters</h3>
              <v-btn :loading="loading" class="ma-1" color="red" plain @click="resetFilters">Clear</v-btn>
              <v-btn color="red" icon small @click="filterBarOpen = !filterBarOpen"><v-icon>mdi-close</v-icon></v-btn>
            </div>
            <v-divider class="mb-2" />
            <v-row v-for="filterSet in Object.keys(activeFilters)" :key="filterSet" align="center" justify="start">
              <v-col v-for="(filter, i) in activeFilters[filterSet]" :key="filter.id" class="shrink">
                <v-chip :disabled="loading" close @click:close="removeFilter(filterSet, i)">
                  <v-icon v-if="filterSet == 'FAMILY'" left>{{ 'mdi-human-male-female-child' }}</v-icon>
                  <v-icon v-else left>{{ 'mdi-chart-box' }}</v-icon>
                  {{ filter.display }}
                </v-chip>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12">
            <h3 class="text-uppercase foa_text_header--text">Filters</h3>
            <v-divider class="mb-2" />
            <h4 class="foa_text_header--text">Families</h4>
            <div>
              <v-list>
                <template v-for="filter in searchFilters.FAMILY">
                  <v-list-item
                    v-if="!activeFilters.FAMILY.map((f) => f.id).includes(filter.id)"
                    :key="filter.id"
                    :disabled="loading"
                    @click="addFilter('FAMILY', filter)"
                  >
                    <v-list-item-avatar>
                      <v-icon :disabled="loading">{{ 'mdi-human-male-female-child' }}</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-title v-text="filter.display"></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </div>
            <v-divider />
            <h4 class="foa_text_header--text">Polls</h4>
            <div>
              <v-list>
                <template v-for="filter in searchFilters.POLL">
                  <v-list-item
                    v-if="!activeFilters.POLL.map((f) => f.id).includes(filter.id)"
                    :key="filter.id"
                    :disabled="loading"
                    @click="addFilter('POLL', filter)"
                  >
                    <v-list-item-avatar>
                      <v-icon :disabled="loading">{{ 'mdi-chart-box' }}</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-title v-text="filter.display"></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </div>
            <v-divider />
            <h4 class="foa_text_header--text">Poll Status</h4>
            <div>
              <v-switch
                v-model="closed"
                class="my-0"
                inset
                color="foa_button"
                label="Closed Polls"
                @change="search"
              ></v-switch>
              <v-switch
                v-model="unvoted"
                class="my-0"
                inset
                color="foa_button"
                label="Unvoted By Me"
                @change="search"
              ></v-switch>
              <v-menu
                ref="beginMenu"
                :close-on-content-click="false"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template #activator="{ on, attrs }">
                  <v-text-field
                    v-model="startDate"
                    label="Closing Date Begin"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    color="foa_button"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="startDate"
                  :active-picker.sync="activePicker"
                  color="foa_button"
                  @change="saveBegin"
                ></v-date-picker>
              </v-menu>
              <v-menu
                ref="endMenu"
                :close-on-content-click="false"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template #activator="{ on, attrs }">
                  <v-text-field
                    v-model="endDate"
                    label="Closing Date End"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    color="foa_button"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="endDate"
                  :active-picker.sync="activePicker"
                  color="foa_button"
                  @change="saveEnd"
                ></v-date-picker>
              </v-menu>
            </div>
            <v-switch v-model="autoSearch" label="Auto search on change?" inset color="foa_button"></v-switch>
            <v-btn v-if="!autoSearch" @click="search">Search</v-btn>
          </v-col>
        </v-row>
      </v-sheet>
    </v-navigation-drawer>

    <v-row>
      <v-col cols="12" sm="4" md="3">
        <PollNav />
      </v-col>
      <v-col cols="12" sm="8" md="9">
        <v-data-iterator
          :items="polls"
          item-key="id"
          :items-per-page.sync="itemsPerPage"
          :page.sync="page"
          :loading="loading"
          :no-data-text="`You don't have any polls. Create one for your family!`"
          hide-default-footer
          :search="searchStr"
        >
          <template #header>
            <div class="foa_text_header--text text-h4 mb-3 d-inline-flex align-center">
              <span>Polls</span>
              <PollDialog class="mr-2" :success-callback="search" type="create"></PollDialog>
            </div>
            <v-toolbar class="mb-4" color="foa_nav_bg">
              <v-text-field
                v-model="searchStr"
                clearable
                flat
                solo
                color="foa_button"
                append-icon="mdi-magnify"
                label="Search"
                hide-details
              ></v-text-field>
              <v-btn icon color="foa_button_dark" :disabled="loading" :loading="loading" @click="search"
                ><v-icon>mdi-cached</v-icon>
              </v-btn>
              <v-btn
                icon
                :color="filterBtnColor"
                :disabled="loading"
                :loading="loading"
                @click.stop="filterBarOpen = !filterBarOpen"
              >
                <v-icon>mdi-filter</v-icon>
              </v-btn>
            </v-toolbar>
          </template>
          <template #default="props">
            <v-row>
              <v-col v-for="poll in props.items" :key="poll.id" cols="12" md="6" lg="4">
                <PollCard :poll="poll"></PollCard>
              </v-col>
            </v-row>
          </template>
          <template #footer>
            <v-row class="mt-4 mx-1" align="center" justify="center">
              <span class="grey--text text-caption">Items per page</span>
              <v-menu offset-y>
                <template #activator="{ on, attrs }">
                  <v-btn dark text color="foa_button" class="ml-2" v-bind="attrs" v-on="on">
                    {{ itemsPerPage }}
                    <v-icon>mdi-chevron-down</v-icon>
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item
                    v-for="(number, index) in itemsPerPageArray"
                    :key="index"
                    @click="updateItemsPerPage(number)"
                  >
                    <v-list-item-title>{{ number }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
              <v-spacer></v-spacer>
              <span class="mr-4 grey--text text-caption">Page {{ page }} of {{ numberOfPages }}</span>
              <v-btn fab dark x-small color="foa_button" class="mr-1" @click="formerPage"
                ><v-icon>mdi-chevron-left</v-icon></v-btn
              >
              <v-btn fab dark x-small color="foa_button" class="ml-1" @click="nextPage"
                ><v-icon>mdi-chevron-right</v-icon></v-btn
              >
            </v-row>
          </template>
        </v-data-iterator>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../../api';
import PollCard from '../../components/poll-app/PollCard.vue';
import PollNav from '../../components/poll-app/PollNav.vue';
import PollDialog from '../../components/poll-app/PollDialog.vue';

export default {
  name: 'Polling',
  components: {
    PollCard,
    PollNav,
    PollDialog,
  },
  data: () => ({
    searchFilters: {
      FAMILY: [
        { id: 1, display: 'Lippelman' },
        { id: 2, display: 'Test' },
      ],
      POLL: [{ id: 1, display: 'Dinner on Saturday' }],
    },
    activeFilters: { FAMILY: [], POLL: [] },
    unvoted: false,
    error: false,
    errorMsg: '',
    closed: false,
    startDate: null,
    endDate: null,
    activePicker: null,
    menu: false,
    loading: false,
    filterBarOpen: false,
    polls: [],
    itemsPerPage: 4,
    page: 1,
    autoSearch: true,
    itemsPerPageArray: [4, 8, 12],
    searchStr: '',
    filtersActive: false,
  }),
  computed: {
    numberOfPages() {
      return Math.ceil(this.polls.length / this.itemsPerPage);
    },
    filterBtnColor() {
      return this.filtersActive ? 'foa_button_dark' : '';
    },
  },
  watch: {
    menu(val) {
      if (val) {
        this.activePicker = 'YEAR';
      }
    },
  },
  mounted() {
    if (this.$route.query.familyId !== undefined) {
      if (this.activeFilters.FAMILY !== undefined) {
        this.activeFilters.FAMILY.push({ id: +this.$route.query.familyId, display: this.$route.query.name });
        this.filtersActive = true;
      }
    }
    this.search();
  },

  methods: {
    ...mapActions(['showSnackbar']),
    saveBegin(date) {
      this.$refs.beginMenu.save(date);
      if (this.autoSearch) {
        this.search();
      }
    },
    saveEnd(date) {
      this.$refs.endMenu.save(date);
      if (this.autoSearch) {
        this.search();
      }
    },
    removeFilter(filterSet, i) {
      this.activeFilters[filterSet].splice(i, 1);
      if (this.activeFilters.POLL.length === 0 && this.activeFilters.FAMILY.length === 0) {
        this.filtersActive = false;
      }
      if (this.autoSearch) {
        this.search();
      }
    },
    addFilter(filterSet, filter) {
      this.activeFilters[filterSet].push(filter);
      if (!this.filtersActive) {
        this.filtersActive = true;
      }
      if (this.autoSearch) {
        this.search();
      }
    },
    resetFilters() {
      this.activeFilters.FAMILY = [];
      this.activeFilters.POLL = [];
      this.closed = false;
      this.unvoted = false;
      this.startDate = null;
      this.endDate = null;
      this.filtersActive = false;
      this.search();
    },
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
    async search() {
      try {
        this.loading = true;
        const request = {
          closed: this.closed,
          unVoted: this.unvoted,
          start: this.startDate,
          end: this.endDate,
          filters: this.activeFilters,
        };
        const res = await api.searchPolls(request);
        if (res.status === 200) {
          this.polls = res.data.polls;
          this.searchFilters = res.data.searchFilters;
          this.activeFilters = res.data.activeSearchFilters;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue fetching polls. Please try again in a few minutes.',
            timeout: 3000,
          });
          this.polls = [];
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue fetching polls. Please try again in a few minutes.',
          timeout: 3000,
        });
        this.polls = [];
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
