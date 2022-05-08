<template>
  <v-sheet>
    <v-list color="foa_nav_bg">
      <v-list-item-group>
        <v-list-item to="/polls/view">
          <v-list-item-icon>
            <v-icon color="foa_nav_link">mdi-poll</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title class="foa_nav_link--text">View Polls</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item to="/polls/manage">
          <v-list-item-icon>
            <v-icon color="foa_nav_link">mdi-human-male-board-poll</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title class="foa_nav_link--text">Manage My Polls</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item to="/polls/results">
          <v-list-item-icon>
            <v-icon color="foa_nav_link">mdi-ballot-recount-outline</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title class="foa_nav_link--text">View Results</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
    </v-list>
  </v-sheet>
</template>

<script>
export default {
  name: 'PollNav',
};
</script>
