<template>
  <div class="resultViewer">
    <v-row>
      <v-col cols="12" sm="4" md="3">
        <PollNav />
      </v-col>
      <v-col cols="12" sm="8" md="9">
        <v-toolbar class="mb-2 mt-xs-4" color="foa_content_bg" dark flat>
          <v-toolbar-title class="foa_nav_link--text">Completed Polls</v-toolbar-title>
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
        <v-data-table
          v-model="selected"
          single-select
          show-select
          :headers="headers"
          :items="polls"
          :items-per-page="8"
          item-key="id"
          class="elevation-1"
          :footer-props="{
            showFirstLastPage: true,
            firstIcon: 'mdi-chevron-left',
            lastIcon: 'mdi-chevron-right',
            prevIcon: 'mdi-minus',
            nextIcon: 'mdi-plus',
          }"
          :search="search"
        >
        </v-data-table>
        <ResultChart
          v-if="showChart"
          class="mt-5"
          :labels="poll.options"
          :votes="poll.votes"
          :poll-description="poll.description"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import api from '../../api';
import PollNav from '../../components/poll-app/PollNav.vue';
import ResultChart from '../../components/poll-app/ResultChart.vue';

export default {
  name: 'ResultViewer',
  components: {
    PollNav,
    ResultChart,
  },
  data: () => ({
    polls: [{ id: 1, description: 'Test description', family: 'Lippelman', completedDateTime: '2022-04-09' }],
    selected: [],
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
  }),
  watch: {
    selected(val) {
      if (val !== null && val.length > 0) {
        const { id } = val[0];
        api
          .getPollResults(id)
          .then((res) => {
            if (res.status === 200) {
              const options = [];
              const votes = [];
              res.data.options.forEach((option) => {
                options.push(option.value);
                votes.push(option.votes);
              });

              this.poll = {
                description: res.data.description,
                options,
                votes,
              };
              this.showChart = true;
            } else {
              this.error = true;
              this.errorMsg = 'We ran into an issue retrieving the result. Please try again in a few minutes.';
              this.poll = {};
              this.showChart = false;
              this.selected = [];
            }
          })
          .catch((err) => {
            this.error = true;
            this.errorMsg = 'We ran into an issue regarding the poll. ';
            console.log(err);
            this.poll = {};
            this.selected = [];
            this.showChart = false;
          });
      } else {
        this.showChart = false;
      }
    },
  },
  created() {
    this.fetchPolls();
  },
  methods: {
    async fetchPolls() {
      this.polls = [];
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
      } else {
        this.error = true;
        this.errorMsg = 'There was an error processing your request. Please try again in a few minutes.';
        this.polls = [];
      }
      this.loading = false;
    },
  },
};
</script>
