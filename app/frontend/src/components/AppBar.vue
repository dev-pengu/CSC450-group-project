<template>
  <v-app-bar app clipped-left>
    <v-app-bar-nav-icon @click="toggleDrawerState"></v-app-bar-nav-icon>
    <v-img max-height="40" max-width="40" contain src="../assets/logo.png"></v-img>
    <v-toolbar-title v-if="$vuetify.breakpoint.smAndUp">Family Command Center</v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn icon :color="darkModeBtnColor" @click="toggleDarkMode">
      <v-icon>mdi-weather-night</v-icon>
    </v-btn>
    <v-menu offset-y>
      <template #activator="{ on }">
        <v-btn icon v-on="on"><v-icon>mdi-account-circle</v-icon></v-btn>
      </template>
      <v-list nav>
        <v-list-item to="/test">
          <v-list-item-title>My Profile</v-list-item-title>
        </v-list-item>
        <v-list-item link @click="logout">
          <v-list-item-title>Logout</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>
</template>

<script>
export default {
  name: 'AppBar',
  data: () => ({}),
  computed: {
    drawerState() {
      return this.$store.getters.drawerState;
    },
    darkModeBtnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : '';
    },
  },
  methods: {
    toggleDrawerState() {
      if (this.$vuetify.breakpoint.mdAndDown) {
        this.$store.commit('toggleDrawerState', !this.drawerState);
      } else {
        this.$store.commit('toggleDrawerMiniState');
      }
    },
    toggleDarkMode() {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
      localStorage.setItem('darkMode', this.$vuetify.theme.dark.toString());
    },
    async logout() {
      await this.$store.dispatch('logout');
      this.$router.push('/login');
    },
  },
};
</script>
