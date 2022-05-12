<template>
  <div class="snackbar">
    <v-snackbar v-model="snackbarState" text :color="snackbarColor" :timeout="timeout" top app>
      <v-icon :color="snackbarColor">{{ snackbarIcon }}</v-icon>
      <span class="ml-2">{{ snackbarMessage }}</span>
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
