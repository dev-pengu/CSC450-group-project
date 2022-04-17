<template>
  <div class="snackbar">
    <v-snackbar v-model="snackbarState" text :color="snackbarColor" :timeout="timeout" top app>
      <v-row>
        <v-col cols="2" sm="1" align-self="center">
          <v-icon :color="snackbarColor">{{ snackbarIcon }}</v-icon>
        </v-col>
        <v-col cols="10" sm="11">
          {{ snackbarMessage }}
        </v-col>
      </v-row>
      <template #action="{ attrs }">
        <v-btn text v-bind="attrs" @click="closeSnackbar">Close</v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';

export default {
  name: 'Snackbar',
  computed: {
    ...mapGetters({
      snackbarMessage: 'getSnackbarMessage',
      snackbarColor: 'getSnackbarColor',
      snackbarIcon: 'getSnackbarIcon',
      timeout: 'getTimeout',
    }),
    snackbarState: {
      get() {
        return this.$store.getters.getSnackbarState;
      },
      set(newValue) {
        this.setSnackbarState({ state: newValue });
      },
    },
  },
  methods: {
    ...mapMutations({ setSnackbarState: 'SET_SNACKBAR_STATE' }),
    closeSnackbar() {
      this.setSnackbarState({ state: false });
    },
  },
};
</script>
