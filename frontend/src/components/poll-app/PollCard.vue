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
        <v-btn :loading="loading" :disabled="loading || voteOption === null" @click="vote"
          ><v-icon>mdi-vote-outline</v-icon>Vote</v-btn
        >
      </div>
      <div v-else>This poll is closed</div>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';

export default {
  name: 'PollCard',
  props: {
    poll: {
      default: null,
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
    voteOption: instance.poll.vote,
    loading: false,
  }),
  computed: {
    ...mapGetters({ user: 'getUser' }),
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async vote() {
      try {
        this.loading = true;
        const formData = {
          pollId: this.poll.id,
          choice: {
            id: this.voteOption,
          },
        };

        const res = await api.votePoll(formData);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: 'Vote processed successfully. You may update your vote until the poll closes.',
            timeout: 3000,
          });
          this.successCallback();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an issue processing your vote. Please try again in a few minutes.',
            timeout: 3000,
          });
          this.failureCallback();
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue processing your vote. Please try again in a few minutes.',
          timeout: 3000,
        });
        this.failureCallback();
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
