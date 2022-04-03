<template>
  <v-app>
    <NavDrawer v-if="isLoggedIn" />
    <AppBar v-if="isLoggedIn" />
    <v-main>
      <v-container>
        <router-view />
      </v-container>
    </v-main>
    <v-footer app></v-footer>
  </v-app>
</template>

<script>
import NavDrawer from '@/components/NavDrawer.vue';
import AppBar from '@/components/AppBar.vue';
import api from './api';

export default {
  components: {
    NavDrawer,
    AppBar,
  },
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn;
    },
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
