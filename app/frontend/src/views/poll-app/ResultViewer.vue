<template>
  <div class="resultViewer">
    <v-dialog v-model="showChart" persistent max-width="600">
      <v-card>
        <v-card-title>
          <span class="pt-0 justify-center foa_text_header--text">Results</span>
          <v-spacer></v-spacer>
          <v-btn class="mr-0" color="error" icon @click="hideResultsDialog"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <h5 class="text-h6">{{ poll.description }}</h5>
          <div v-if="poll.votes && poll.totalVotes > 0">
            <div class="text-caption">Total Votes Received: {{ poll.totalVotes }}</div>
            <div class="text-caption">Most Votes: {{ poll.options[poll.winner] }}</div>
          </div>
          <span v-else class="text-caption"> No votes received </span>
          <ResultChart
            v-if="showChart"
            class="mt-5"
            :labels="poll.options"
            :votes="poll.votes"
            :poll-description="poll.description"
          />
        </v-card-text>
      </v-card>
    </v-dialog>
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
          no-data-text="There are no polls with results to view yet. Check back in a little while."
          hide-default-footer
          :search="search"
        >
          <template #header>
            <div class="foa_text_header--text text-h4 mb-3 d-inline-flex align-center">
              <span>Poll Results</span>
              <PollDialog class="mr-2" :success-callback="fetchPolls" type="create"></PollDialog>
            </div>
            <v-toolbar class="mb-4" color="foa_nav_bg">
              <v-text-field
                v-model="search"
                clearable
                flat
                solo
                color="foa_button"
                append-icon="mdi-magnify"
                label="Search"
                hide-details
              ></v-text-field>
              <v-btn icon color="foa_button_dark" :disabled="loading" :loading="loading" @click="fetchPolls"
                ><v-icon>mdi-cached</v-icon></v-btn
              >
            </v-toolbar>
          </template>
          <template #default="props">
            <v-list>
              <v-list-item v-for="p in props.items" :key="p.id" three-line>
                <v-list-item-content>
                  <v-list-item-title v-text="p.description"> </v-list-item-title>
                  <v-list-item-subtitle v-if="p.createdBy" class="text-caption"
                    >Created by: {{ `${p.createdBy.firstName} ${p.createdBy.lastName}` }}</v-list-item-subtitle
                  >
                  <v-list-item-subtitle v-if="p.familyId" class="text-caption">
                    Family: {{ getFamily(p.familyId).name }}
                  </v-list-item-subtitle>
                  <v-list-item-subtitle v-if="p.closedDateTime" class="text-caption">
                    Closed: {{ p.closedDateTime }}
                  </v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <v-btn small @click="showResultsDialog(p)"
                    >Results <v-icon class="ml-2">mdi-ballot-recount-outline</v-icon></v-btn
                  >
                </v-list-item-action>
              </v-list-item>
            </v-list>
          </template>
          <template #footer>
            <v-row class="mt-4 mx-1" align="center" justify="center">
              <span class="grey--text text-caption">Items per page</span>
              <v-menu offset-y>
                <template #activator="{ on, attrs }">
                  <v-btn dark text color="primary" class="ml-2" v-bind="attrs" v-on="on">
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
              <v-btn fab dark x-small :color="btnColor" class="mr-1" @click="formerPage"
                ><v-icon>mdi-chevron-left</v-icon></v-btn
              >
              <v-btn fab dark x-small :color="btnColor" class="ml-1" @click="nextPage"
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
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import PollNav from '../../components/poll-app/PollNav.vue';
import ResultChart from '../../components/poll-app/ResultChart.vue';
import PollDialog from '../../components/poll-app/PollDialog.vue';

export default {
  name: 'ResultViewer',
  components: {
    PollNav,
    ResultChart,
    PollDialog,
  },
  data: () => ({
    polls: [{ id: 1, description: 'Test description', family: 'Lippelman', completedDateTime: '2022-04-09' }],
    poll: {},
    headers: [
      {
        text: 'Family',
        value: 'familyName',
      },
      {
        text: 'Description',
        align: 'sart',
        value: 'description',
      },
      {
        text: 'Date Completed',
        value: 'completedDateTime',
      },
    ],
    loading: true,
    search: '',
    showChart: false,
    error: false,
    errorMsg: '',
    itemsPerPage: 4,
    itemsPerPageArray: [4, 8, 12],
    page: 1,
  }),
  computed: {
    ...mapGetters({ getFamily: 'getFamily' }),
    numberOfPages() {
      return Math.ceil(this.polls.length / this.itemsPerPage);
    },
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  created() {
    this.fetchPolls();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
    async fetchPolls() {
      try {
        this.loading = true;
        const pollReq = {
          closed: true,
          unVoted: false,
          start: null,
          end: null,
          filters: {},
        };
        const res = await api.searchPolls(pollReq);
        if (res.status === 200) {
          this.polls = res.data.polls;
          this.polls.sort((a, b) => new Date(b.closedDateTime) - new Date(a.closedDateTime));
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
    async showResultsDialog(selected) {
      try {
        this.loading = true;
        const options = [];
        const votes = [];
        const res = await api.getPollResults(selected.id);
        if (res.status === 200) {
          res.data.options.forEach((option) => {
            options.push(option.value);
            votes.push(option.votes);
          });

          this.poll = {
            description: res.data.description,
            options,
            votes,
            totalVotes: votes.reduce((a, b) => a + b),
            winner: votes.reduce((iMax, x, i, arr) => (x > arr[iMax] ? i : iMax), 0),
          };
          this.showChart = true;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue fetching the results. Please try again in a few minutes.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue fetching the results. Please try again in a few minutes.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    hideResultsDialog() {
      this.poll = {};
      this.showChart = false;
    },
  },
};
</script>
