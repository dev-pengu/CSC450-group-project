<template>
  <v-card>
    <v-card-title class="subheading font-weight-bold"
      ><v-icon>{{ 'mdi-chart-box' }}</v-icon> {{ poll.description }}</v-card-title
    >
    <v-divider />
    <div class="mx-2">
      <div>Created by: {{ poll.createdBy.firstName }}</div>
      <div class="text-caption">Additional info: {{ poll.notes }}</div>
      <v-radio-group
        v-model="voteOption"
        :readonly="poll.closed || !poll.respondents.some((r) => r.username === user.username)"
        label="Options:"
      >
        <v-radio v-for="option in poll.options" :key="option.id" :label="option.value" :value="option.id"> </v-radio>
      </v-radio-group>
    </div>
    <v-divider />
    <v-card-actions>
      <div v-if="!poll.respondents.some((r) => r.username === user.username)">You were not added as a respondent.</div>
      <div v-else-if="!poll.closed">
        <div class="text-caption mb-2">Poll closes: {{ poll.closedDateTime }}</div>
        <v-btn @click="vote"><v-icon>mdi-vote-outline</v-icon>Vote</v-btn>
      </div>
      <div v-else>This poll is closed</div>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex';
import api from '../../api';

export default {
  name: 'PollCard',
  props: {
    poll: {
      default: null,
      type: Object,
    },
  },
  data: (instance) => ({
    voteOption: instance.poll.vote,
  }),
  computed: {
    ...mapGetters({ user: 'getUser' }),
  },
  methods: {
    async vote() {
      const formData = {
        pollId: this.poll.id,
        choice: {
          id: this.voteOption,
        },
      };

      const res = await api.votePoll(formData);
      if (res.status === 200) {
        // TODO: display message saying vote was received
      } else {
        // TODO: handle errors and display message
      }
    },
  },
};
</script>
