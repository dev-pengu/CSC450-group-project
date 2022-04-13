<template>
  <div class="managePolls">
    <v-row>
      <v-col cols="12" sm="4" md="3">
        <PollNav />
      </v-col>
      <v-col cols="12" sm="8" md="9">
        <v-toolbar class="mb-2 mt-xs-4" color="foa_content_bg" dark flat>
          <v-toolbar-title class="foa_nav_link--text">Poll Management</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon color="foa_button_dark" :disabled="loading" :loading="loading" @click="fetchPolls"
            ><v-icon>mdi-cached</v-icon></v-btn
          >
          <v-text-field
            v-model="search"
            color="foa_button"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
            light
          ></v-text-field>
        </v-toolbar>
        <v-dialog v-model="updateDialog" max-width="500px">
          <v-card>
            <v-card-title>
              <span class="pt-0 justify-center foa_text_header--text">Update Poll Information</span>
              <v-spacer></v-spacer>
              <v-btn class="mr-0" color="error" icon @click="updateDialog = false"><v-icon>mdi-close</v-icon></v-btn>
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
                    counter="70"
                    max-length="70"
                    required
                  ></v-text-field>
                  <v-textarea v-model="editedItem.notes" outlined :rules="notesRules" label="Notes"></v-textarea>
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
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="editedItem.closedDateTime" scrollable>
                      <v-spacer></v-spacer>
                      <v-btn text color="primary" @click="dateModal = false"> Cancel </v-btn>
                      <v-btn text color="primary" @click="$refs.dialog.save(editedItem.closedDateTime)"> OK </v-btn>
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
                  >
                  </v-combobox>
                </v-form>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                class="foa_button_text--text mr-4"
                color="error"
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
                :disabled="loading"
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
                color="error"
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
        <v-data-table
          :headers="tableHeaders"
          :items="polls"
          :items-per-page="8"
          :expanded="expanded"
          item-key="id"
          class="elevation-1"
          :footer-props="{
            showFirstLastPage: true,
            firstIcon: 'mdi-chevron-left',
            lastIcon: 'mdi-chevron-right',
            prevIcon: 'mdi-minus',
            nextIcon: 'mdi-plus',
          }"
          single-expand
          show-expand
          :search="search"
        >
          <template #item.actions="{ item }">
            <v-icon small class="mr-2" @click="editItem(item)">mdi-pencil</v-icon>
            <v-icon small @click="deleteItem(item)">mdi-delete</v-icon>
          </template>
          <template #expanded-item="{ headers, item }">
            <td :colspan="headers.length">Notes: {{ item.notes }}</td>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import api from '../../api';
import PollNav from '../../components/poll-app/PollNav.vue';

export default {
  name: 'PollManager',
  components: {
    PollNav,
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
      (v) => (v && v.length <= 70) || 'Description must be less than 70 characters',
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
    expanded: [],
    search: '',
  }),
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
        // TODO: handle error
        this.families = [];
      });
  },
  methods: {
    async editItem(item) {
      this.editedIndex = this.polls.indexOf(item);
      this.editedItem = { ...item };
      const [closedDate] = this.editedItem.closedDateTime.split(' ');
      this.editedItem.closedDateTime = closedDate;
      this.editedItem.respondents = this.editedItem.respondents.map((respondent) => respondent.id);
      await this.getMembers();
      this.updateDialog = true;
    },
    deleteItem(item) {
      this.editedIndex = this.polls.indexOf(item);
      this.editedItem = { ...item };
      this.deleteDialog = true;
    },
    async deleteItemConfirm() {
      this.loading = true;
      const res = await api.deletePoll(this.editedItem.id);
      if (res.status === 200) {
        this.fetchPolls();
        this.closeDelete();
      } else {
        // TODO: handle error
      }

      this.loading = false;
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
    async save() {
      this.loading = true;
      const req = {
        id: this.editedItem.id,
        familyId: this.editedItem.familyId,
        description: this.editedItem.description,
        notes: this.editedItem.notes,
        closedDateTime: `${this.editedItem.closedDateTime} 11:59`,
        options: this.editedItem.options.map((option) => ({ id: option.id, value: option.value })),
        respondents: this.editedItem.respondents.map((respondent) => ({ id: respondent })),
      };
      const res = await api.updatePoll(req);
      if (res.status === 200) {
        this.fetchPolls();
        this.close();
      } else {
        // TODO: handle errors
      }
      this.loading = false;
    },
    fetchPolls() {
      const pollReq = {
        closed: false,
        unVoted: false,
        start: null,
        end: null,
        filters: {},
        limitToCreated: true,
      };
      api
        .searchPolls(pollReq)
        .then((res) => {
          if (res.status === 200) {
            this.polls = res.data.polls;
          } else {
            this.polls = [];
          }
        })
        .catch((err) => {
          console.log(err);
          // TODO: handle error
          this.polls = [];
        });
    },
    async getMembers() {
      if (this.editedItem.familyId !== null) {
        const res = await api.getMembersForSelect(this.editedItem.familyId);
        if (res.status === 200) {
          this.members = res.data.map((member) => ({ id: member.id, name: `${member.firstName} ${member.lastName}` }));
        } else {
          this.members = [];
        }
      } else {
        this.members = [];
      }
    },
  },
};
</script>
