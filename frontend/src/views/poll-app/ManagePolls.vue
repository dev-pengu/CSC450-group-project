<template>
  <div class="managePolls">
    <v-row>
      <v-col cols="12" sm="4" md="3">
        <PollNav />
      </v-col>
      <v-col cols="12" sm="8" md="9">
        <v-dialog v-model="updateDialog" max-width="500px">
          <v-card>
            <v-card-title>
              <span class="pt-0 justify-center foa_text_header--text">Update Poll Information</span>
              <v-spacer></v-spacer>
              <v-btn class="mr-0" color="red" icon @click="updateDialog = false"><v-icon>mdi-close</v-icon></v-btn>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-form ref="form" v-model="valid" lazy-validation :disabled="loading">
                  <v-text-field
                    v-model="editedItem.description"
                    outlined
                    color="foa_button"
                    :rules="descriptionRules"
                    label="Description"
                    counter="256"
                    max-length="256"
                  ></v-text-field>
                  <v-textarea
                    v-model="editedItem.notes"
                    outlined
                    :rules="notesRules"
                    label="Notes"
                    color="foa_button"
                  ></v-textarea>
                  <v-dialog
                    ref="dialog"
                    v-model="dateModal"
                    :return-value.sync="editedItem.closedDateTime"
                    persistent
                    width="290px"
                  >
                    <template #activator="{ on, attrs }">
                      <v-text-field
                        v-model="editedItem.closedDateTime"
                        label="Date to close the poll"
                        prepend-icon="mdi-calendar"
                        readonly
                        hint="Poll will close at 11:59 on the date specified"
                        persistent-hint
                        :rules="closedDateRules"
                        v-bind="attrs"
                        color="foa_button"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="editedItem.closedDateTime" scrollable color="foa_button">
                      <v-spacer></v-spacer>
                      <v-btn text color="foa_button" @click="dateModal = false"> Cancel </v-btn>
                      <v-btn text color="foa_button" @click="$refs.dialog.save(editedItem.closedDateTime)"> OK </v-btn>
                    </v-date-picker>
                  </v-dialog>
                  <v-select
                    v-model="editedItem.respondents"
                    :items="members"
                    label="Poll Respondents"
                    multiple
                    chips
                    hint="Add one or more respondents for your poll"
                    item-text="name"
                    item-value="id"
                    :rules="respondentRules"
                    persistent-hint
                    color="foa_button"
                    item-color="foa_button"
                  ></v-select>
                  <v-combobox
                    v-model="editedItem.options"
                    hide-selected
                    hint="Maximum of 4 options"
                    label="Response Options"
                    item-text="value"
                    item-value="id"
                    multiple
                    persistent-hint
                    small-chips
                    :rules="optionRules"
                    class="mb-5"
                    color="foa_button"
                  >
                  </v-combobox>
                </v-form>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                class="foa_button_text--text mr-4"
                color="red"
                elevation="2"
                max-width="150px"
                :disabled="loading"
                @click="close"
                >Cancel</v-btn
              >
              <v-btn
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                max-width="150px"
                :loading="loading"
                :disabled="!valid || loading"
                @click="save"
                >Save</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="deleteDialog" max-width="500px">
          <v-card>
            <v-card-title class="text-h5">Are you sure you want to delete this poll?</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                class="foa_button_text--text mr-4"
                color="red"
                elevation="2"
                max-width="150px"
                :disabled="loading"
                @click="closeDelete"
                >Cancel</v-btn
              >
              <v-btn
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                max-width="150px"
                :loading="loading"
                :disabled="loading"
                @click="deleteItemConfirm"
                >OK</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-data-iterator
          :items="polls"
          item-key="id"
          :items-per-page.sync="itemsPerPage"
          :page.sync="page"
          :loading="loading"
          no-data-text="You don't have any polls. Create one for your family!"
          hide-default-footer
          :search="search"
        >
          <template #header>
            <div class="foa_text_header--text text-h4 mb-3 d-inline-flex align-center">
              <span>Manage Polls</span>
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
              <v-list-item v-for="poll in props.items" :key="poll.id">
                <v-list-item-content>
                  <v-list-item-title v-text="poll.description"> </v-list-item-title>
                  <v-list-item-subtitle class="text-caption"
                    >Created by: {{ `${poll.createdBy.firstName} ${poll.createdBy.lastName}` }}</v-list-item-subtitle
                  >
                  <v-list-item-subtitle class="text-caption">
                    Family: {{ getFamily(poll.familyId).name }}
                  </v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <div class="d-flex">
                    <v-btn small :color="btnColor" icon class="mr-2" @click="editItem(poll)">
                      <v-icon>mdi-pencil</v-icon>
                    </v-btn>
                    <v-btn small color="red" icon @click="deleteItem(poll)">
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                  </div>
                </v-list-item-action>
              </v-list-item>
            </v-list>
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
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';
import PollNav from '../../components/poll-app/PollNav.vue';
import PollDialog from '../../components/poll-app/PollDialog.vue';

export default {
  name: 'PollManager',
  components: {
    PollNav,
    PollDialog,
  },
  data: () => ({
    valid: true,
    updateDialog: false,
    deleteDialog: false,
    tableHeaders: [
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
        text: 'Created By',
        value: 'createdBy.firstName',
      },
      {
        text: 'Closing',
        value: 'closedDateTime',
      },
      { text: 'Actions', value: 'actions', sortable: false },
    ],
    polls: [],
    editedIndex: -1,
    editedItem: {
      id: null,
      responseOptions: [],
      familyId: null,
      description: '',
      notes: '',
      closedDateTime: null,
      options: [],
      respondents: [],
      omitCreator: false,
    },
    defaultItem: {
      id: null,
      responseOptions: [],
      familyId: null,
      description: '',
      notes: '',
      closedDateTime: null,
      options: [],
      respondents: [],
      omitCreator: false,
    },
    families: [],
    members: [],
    descriptionRules: [
      (v) => !!v || 'Description is required',
      (v) => (v && v.length <= 256) || 'Description must be less than 256 characters',
    ],
    notesRules: [],
    closedDateRules: [(v) => !!v || 'Date for poll to close is required'],
    respondentRules: [(v) => (v && v.length > 0) || 'At least one respondent is required'],
    optionRules: [
      (v) => (v && v.length > 1) || 'At least two options are required',
      (v) => (v && v.length < 5) || 'At most 4 options are allowed for a poll',
    ],
    loading: false,
    dateModal: false,
    error: false,
    errorMsg: '',
    expanded: [],
    search: '',
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
  watch: {
    updateDialog(val) {
      if (!val) this.close();
    },
    deleteDialog(val) {
      if (!val) this.closeDelete();
    },
    editedItem(val) {
      if (val.responseOptions && val.responseOptions.length > 4) {
        this.$$nextTick(() => this.editedItem.responseOptions.pop());
      }
    },
  },
  async created() {
    this.fetchPolls();

    api
      .getFamiliesForSelect()
      .then((res) => {
        if (res.status === 200) {
          this.families = res.data;
        } else {
          this.families = [];
        }
      })
      .catch((err) => {
        console.log(err);
        this.error = true;
        this.errorMsg = 'We ran into an error with the Poll. Please try again in a few minutes.';
        this.families = [];
      });
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async editItem(item) {
      this.editedIndex = this.polls.indexOf(item);
      this.editedItem = { ...item };
      const [closedDate] = this.editedItem.closedDateTime.split(' ');
      this.editedItem.closedDateTime = closedDate;
      this.editedItem.respondents = this.editedItem.respondents.map((respondent) => respondent.id);
      this.getMembers();
      this.updateDialog = true;
    },
    deleteItem(item) {
      this.editedIndex = this.polls.indexOf(item);
      this.editedItem = { ...item };
      this.deleteDialog = true;
    },
    async deleteItemConfirm() {
      try {
        this.loading = true;
        const res = await api.deletePoll(this.editedItem.id);
        if (res.status === 200) {
          this.fetchPolls();
          this.closeDelete();
          this.showSnackbar({
            type: 'success',
            message: 'The poll has been deleted successfully.',
            timeout: 3000,
          });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue deleting the poll. Please try again in a few minutes.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue deleting the poll. Please try again in a few minutes.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    close() {
      this.updateDialog = false;
      this.$nextTick(() => {
        this.editedIndex = -1;
        this.editedItem = { ...this.defaultItem };
      });
    },
    closeDelete() {
      this.deleteDialog = false;
      this.$nextTick(() => {
        this.editedIndex = -1;
        this.editedItem = { ...this.defaultItem };
      });
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
    async save() {
      try {
        this.loading = true;
        const req = {
          id: this.editedItem.id,
          familyId: this.editedItem.familyId,
          description: this.editedItem.description,
          notes: this.editedItem.notes,
          closedDateTime: `${this.editedItem.closedDateTime} 23:59`,
          options: this.editedItem.options.map((option) => ({ id: option.id || null, value: option.value || option })),
          respondents: this.editedItem.respondents.map((respondent) => ({ id: respondent })),
        };
        const res = await api.updatePoll(req);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: res.data,
            timeout: 3000,
          });
          this.fetchPolls();
          this.close();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue updating the poll. Please try again in a few minutes.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue updating the poll. Please try again in a few minutes.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    async fetchPolls() {
      try {
        this.loading = true;
        const pollReq = {
          closed: false,
          unVoted: false,
          start: null,
          end: null,
          filters: {},
          limitToCreated: true,
        };
        const res = await api.searchPolls(pollReq);
        if (res.status === 200) {
          this.polls = res.data.polls;
        } else {
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
    async getMembers() {
      try {
        this.loading = true;
        if (this.editedItem.familyId !== null) {
          const res = await api.getMembersForSelect(this.editedItem.familyId);
          if (res.status === 200) {
            this.members = res.data.map((member) => ({
              id: member.id,
              name: `${member.firstName} ${member.lastName}`,
            }));
          } else {
            this.showSnackbar({
              type: 'error',
              message: 'We ran into an issue fetching family members. Please try again in a few minutes.',
              timeout: 3000,
            });
            this.members = [];
          }
        } else {
          this.members = [];
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue fetching family members. Please try again in a few minutes.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
