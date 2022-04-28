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
    PollForm,
  },
};
</script>
