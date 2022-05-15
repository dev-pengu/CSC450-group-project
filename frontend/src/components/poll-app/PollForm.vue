<template>
  <div class="pollForm">
    <v-form ref="form" v-model="valid" :disabled="loading">
      <v-select
        v-model="formData.familyId"
        :items="families"
        outlined
        color="foa_button"
        item-color="foa_button"
        :rules="familyRules"
        label="Family"
        item-text="name"
        item-value="id"
        @change="getMembers"
      ></v-select>
      <v-text-field
        v-model="formData.description"
        outlined
        color="foa_button"
        :rules="descriptionRules"
        label="Description"
        counter="256"
        max-length="256"
        required
      ></v-text-field>
      <v-textarea v-model="formData.notes" outlined :rules="notesRules" label="Notes" color="foa_button"></v-textarea>
      <v-dialog ref="dialog" v-model="dateModal" :return-value.sync="formData.closedDate" persistent width="290px">
        <template #activator="{ on, attrs }">
          <v-text-field
            v-model="formData.closedDate"
            label="Date to close the poll"
            prepend-icon="mdi-calendar"
            readonly
            hint="Poll will close at 11:59 PM on the date specified"
            persistent-hint
            :rules="closedDateRules"
            v-bind="attrs"
            color="foa_button"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="formData.closedDate" scrollable color="foa_button">
          <v-spacer></v-spacer>
          <v-btn text color="foa_button" @click="dateModal = false"> Cancel </v-btn>
          <v-btn text color="foa_button" @click="$refs.dialog.save(formData.closedDate)"> OK </v-btn>
        </v-date-picker>
      </v-dialog>
      <v-select
        v-model="formData.respondents"
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
        v-model="formData.responseOptions"
        hide-selected
        hint="Maximum of 4 options"
        label="Response Options"
        multiple
        persistent-hint
        small-chips
        :items="defaultOptions"
        :rules="optionRules"
        class="mb-5"
        color="foa_button"
      >
      </v-combobox>
      <div class="d-flex justify-end mt-4">
        <v-btn class="mr-4" color="red" text :disabled="loading" @click="reset">Reset</v-btn>
        <v-btn color="foa_button" text :loading="loading" :disabled="!valid || loading" @click="submit">Submit</v-btn>
      </div>
    </v-form>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../../api';

export default {
  name: 'PollForm',
  props: {
    poll: {
      default: () => ({}),
      type: Object,
    },
    successCallback: {
      default: () => {},
      type: Function,
    },
    failureCallback: {
      default: () => {},
      type: Function,
    },
  },
  data: (instance) => ({
    valid: false,
    families: [],
    members: [],
    descriptionRules: [
      (v) => !!v || 'Description is required',
      (v) => (v && v.length <= 256) || 'Description must be less than 256 characters',
    ],
    familyRules: [(v) => !!v || 'Family is required'],
    notesRules: [],
    closedDateRules: [(v) => !!v || 'Date for poll to close is required'],
    respondentRules: [(v) => (v && v.length > 0) || 'At least one respondent is required'],
    optionRules: [
      (v) => (v && v.length > 1) || 'At least two options are required',
      (v) => (v && v.length < 5) || 'At most 4 options are allowed for a poll',
    ],
    loading: false,
    formData: {
      familyId: instance.poll.familyId,
      description: instance.poll.description,
      notes: instance.poll.notes,
      closedDate: instance.poll.closedDate,
      respondents: instance.poll.repsondents,
      responseOptions: instance.poll.responseOptions,
    },
    defaultOptions: ['Yes', 'No'],
    dateModal: false,
  }),
  watch: {
    // eslint-disable-next-line func-names
    'formData.responseOptions': function (val) {
      if (val.length > 4) {
        this.$$nextTick(() => this.responseOptions.pop());
      }
    },
  },
  async created() {
    const res = await api.getFamiliesForSelect();
    if (res.status === 200) {
      this.families = res.data;
    } else {
      this.families = [];
    }
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async getMembers() {
      try {
        this.loading = true;
        if (this.formData.familyId !== null) {
          const res = await api.getMembersForSelect(this.formData.familyId);
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
            this.formData.respondents = [];
          }
        } else {
          this.members = [];
          this.formData.respondents = [];
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
    async submit() {
      try {
        this.loading = true;
        if (!this.$refs.form.validate()) {
          this.loading = false;
          return;
        }

        const req = {
          familyId: this.formData.familyId,
          description: this.formData.description,
          notes: this.formData.notes,
          closedDateTime: `${this.formData.closedDate} 23:59`,
          options: this.formData.responseOptions.map((option) => ({ value: option })),
          respondents: this.formData.respondents.map((respondent) => ({ id: respondent })),
        };

        const res = await api.createPoll(req);

        if (res.status === 201) {
          this.showSnackbar({
            type: 'success',
            message: 'Poll created successfully',
            timeout: 3000,
          });
          this.successCallback();
          this.reset();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue creating your poll. Please try again in a few minutes.',
            timeout: 3000,
          });
          this.failureCallback();
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue creating your poll. Please try again in a few minutes.',
          timeout: 3000,
        });
        this.failureCallback();
      } finally {
        this.loading = false;
      }
    },
    reset() {
      this.$refs.form.reset();
    },
  },
};
</script>
