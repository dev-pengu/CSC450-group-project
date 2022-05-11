<template>
  <v-app>
    <NavDrawer v-if="isLoggedIn" />
    <AppBar v-if="isLoggedIn" />
    <v-main>
      <v-container>
        <router-view />
      </v-container>
    </v-main>
    <Snackbar />
  </v-app>
</template>

<script>
import NavDrawer from '@/components/NavDrawer.vue';
import AppBar from '@/components/AppBar.vue';
import Snackbar from '@/components/Snackbar.vue';
import { mapGetters } from 'vuex';
import api from './api';

export default {
  components: {
    NavDrawer,
    AppBar,
    Snackbar,
  },
  computed: {
    ...mapGetters(['isLoggedIn']),
  },
  mounted() {
    window.onunload = () => {
      api.logout();
    };

    const theme = localStorage.getItem('darkMode');
    if (theme) {
      this.$vuetify.theme.dark = theme !== 'false';
    } else {
      this.$vuetify.theme.dark = false;
      localStorage.setItem('darkMode', this.$vuetify.theme.dark.toString());
    }
  },
};
</script>
<style>
#app {
  font-family: Roboto, Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
