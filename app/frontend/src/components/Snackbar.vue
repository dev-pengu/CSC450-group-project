<template>
  <div class="snackbar">
    <v-snackbar v-model="snackbarState" timeout="3000" top app>
      {{ snackbarMessage }}
      <template #action="{ attrs }">
        <v-btn color="foa_button" text v-bind="attrs" @click="closeSnackbar">Close</v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';

export default {
  name: 'Snackbar',
  computed: {
    ...mapGetters({ snackbarMessage: 'getSnackbarMessage' }),
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
