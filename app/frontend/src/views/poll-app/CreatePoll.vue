<template>
  <div class="createPoll">
    <v-row>
      <v-col cols="12" sm="4" md="3">
        <PollNav />
      </v-col>
      <v-col cols="12" sm="8" md="9">
        <v-toolbar class="mb-2 mt-xs-4" color="foa_content_bg" dark flat>
          <v-toolbar-title class="foa_nav_link--text">Create a New Poll</v-toolbar-title>
        </v-toolbar>
        <PollForm />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import PollNav from '../../components/poll-app/PollNav.vue';
import PollForm from '../../components/poll-app/PollForm.vue';

export default {
  name: 'CreatePoll',
  components: {
    PollNav,

  },
  data: () => ({
    valid: true,
    families: [],
    members: [],
    descriptionRules: [
      (v) => !!v || 'Description is required',
      (v) => (v && v.length <= 70) || 'Description must be less than 70 characters',
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
      familyId: null,
      description: '',
      notes: '',
      closedDate: null,
      respondents: [],
    },
    responseOptions: [],
    defaultOptions: ['Yes', 'No'],
    dateModal: false,
    error: false,
    errorMsg: '',
  }),
  watch: {
    responseOptions(val) {
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
    async getMembers() {
      if (this.formData.familyId !== null) {
        const res = await api.getMembersForSelect(this.formData.familyId);
        if (res.status === 200) {
          this.members = res.data.map((member) => ({ id: member.id, name: `${member.firstName} ${member.lastName}` }));
        } else {
          this.members = [];
          this.formData.respondents = [];
        }
      } else {
        this.members = [];
        this.formData.respondents = [];
      }
    },
    async submit() {
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
        options: this.responseOptions.map((option) => ({ value: option })),
        respondents: this.formData.respondents.map((respondent) => ({ id: respondent })),
      };

      const res = await api.createPoll(req);

      if (res.status === 201) {
        this.showSnackbar({ type: 'success', message: 'Your poll was created successfully!', timeout: 3000 });
        this.reset();
      } else {
        this.error = true;
        this.errorMsg = 'There was an error creating your poll. Please try again in a few minutes.';
      }
      this.loading = false;
    },
    reset() {
      this.$refs.form.reset();
    },

  },
};
</script>
