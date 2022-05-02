<template>
  <v-app-bar app clipped-left>
    <v-app-bar-nav-icon @click="toggleDrawerState"></v-app-bar-nav-icon>
    <v-img v-if="$vuetify.theme.dark" max-height="60" max-width="60" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else max-height="60" max-width="60" contain src="@/assets/logo-light.png"></v-img>
    <v-toolbar-title v-if="$vuetify.breakpoint.smAndUp">Happy Home</v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn icon :color="darkModeBtnColor" @click="toggleDarkMode">
      <v-icon>mdi-weather-night</v-icon>
    </v-btn>
    <v-menu offset-y>
      <template #activator="{ on }">
        <v-btn icon v-on="on"><v-icon>mdi-account-circle</v-icon></v-btn>
      </template>
      <v-list nav>
        <v-list-item to="/profile/settings">
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
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'AppBar',
  data: () => ({}),
  computed: {
    ...mapGetters({ drawerState: 'getDrawerState', drawerMiniState: 'getDrawerMiniState' }),
    darkModeBtnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : '';
    },
  },
  methods: {
    ...mapMutations({ setDrawerState: 'SET_DRAWER_STATE', setDrawerMiniState: 'SET_DRAWER_MINI_STATE' }),
    ...mapActions(['logoutUser', 'showSnackbar']),
    toggleDrawerState() {
      if (this.$vuetify.breakpoint.mdAndDown) {
        this.setDrawerState({ state: !this.drawerState });
      } else {
        this.setDrawerMiniState({ state: !this.drawerMiniState });
      }
    },
    toggleDarkMode() {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
      localStorage.setItem('darkMode', this.$vuetify.theme.dark.toString());
      this.showSnackbar({
        type: 'info',
        message:
          'Updating dark mode with the toggle will only save your preference to this browser. Visit your profile page to update your preferences across all devices.',
      });
    },
    async logout() {
      this.logoutUser();
      this.$router.push('/login');
    },
  },
};
</script>
