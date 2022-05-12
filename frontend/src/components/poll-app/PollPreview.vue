<template>
  <div class="pollPreview">
    <v-toolbar class="mb-3">
      <span class="text-h5 foa_text_header--text">My Polls</span>
      <PollDialog type="create" :success-callback="getPolls"></PollDialog>
      <v-spacer></v-spacer>
      <v-btn icon :disabled="loading" :loading="loading" @click="getPolls"><v-icon>mdi-cached</v-icon> </v-btn>
      <v-btn icon :disabled="loading" to="/polls/view"><v-icon>mdi-share</v-icon></v-btn>
    </v-toolbar>
    <div v-if="!polls || polls.length === 0" class="text-caption d-flex align-center justify-center">
      No more polls waiting for your response!
    </div>
    <v-virtual-scroll :items="polls" height="330" :item-height="95">
      <template #default="{ item }">
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title>{{ item.description }}</v-list-item-title>
            <v-list-item-subtitle class="text-caption"
              >Created by: {{ `${item.createdBy.firstName} ${item.createdBy.lastName}` }}</v-list-item-subtitle
            >
            <v-list-item-subtitle class="text-caption"
              >Family: {{ getFamily(item.familyId).name }}</v-list-item-subtitle
            >
            <v-list-item-subtitle class="text-caption">Closes: {{ item.closedDateTime }}</v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-action>
            <PollDialog class="mr-2" :poll="item" :success-callback="getPolls" type="vote"></PollDialog>
          </v-list-item-action>
        </v-list-item>
        <v-divider></v-divider>
      </template>
    </v-virtual-scroll>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import PollDialog from './PollDialog.vue';

export default {
  name: 'PollPreview',
  components: {
    PollDialog,
  },
  props: {},
  data: () => ({
    loading: false,
    polls: [],
    votingPoll: {
      id: -1,
      vote: -1,
    },
  }),
  computed: {
    ...mapGetters({ getFamily: 'getFamily' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  async created() {
    await this.getPolls();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async getPolls() {
      try {
        this.loading = true;
        const request = {
          closed: false,
          unVoted: true,
          start: null,
          end: null,
          filters: {},
        };
        const res = await api.searchPolls(request);
        if (res.status === 200) {
          this.polls = res.data.polls;
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your outstanding polls, if the issue persists please contact support.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    async vote() {
      try {
        this.loading = true;
        const formData = {
          pollId: this.votingPoll.id,
          choice: {
            id: this.votingPoll.vote,
          },
        };

        const res = await api.votePoll(formData);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: 'Your vote has been received.',
            timeout: 3000,
          });
          this.getPolls();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem processing your vote, please try again in a few minutes.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem processing your vote, please try again in a few minutes.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
