<template>
  <v-navigation-drawer
    v-model="drawerState"
    v-resize="onResize"
    :mini-variant="drawerMiniState"
    app
    clipped
    color="foa_nav_bg"
  >
    <v-list-item v-if="$vuetify.breakpoint.mdAndDown">
      <v-list-item-content>
        <v-img height="75%" contain src="@/assets/logo-light.png"></v-img>
        <v-list-item-title class="text-center font-weight-bold">Family Command Center</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list nav>
      <v-list-item-group color="foa_nav_link">
        <v-list-item v-for="(item, i) in navItems" :key="i" :to="item.route">
          <v-list-item-icon>
            <v-icon color="foa_nav_link">{{ item.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title class="foa_nav_link--text">{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
      <v-list-item @click="logout">
        <v-list-item-icon>
          <v-icon color="foa_nav_link">mdi-logout</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title class="foa_nav_link--text">Logout</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'NavDrawer',
  data: () => ({
    navItems: [
      { title: 'Home', icon: 'mdi-home', route: '/dashboard' },
      { title: 'Calendar', icon: 'mdi-calendar', route: '/calendar/view' },
      { title: 'To-Do', icon: 'mdi-check-circle-outline', route: '/todo' },
      { title: 'Shopping Lists', icon: 'mdi-cart', route: '/shopping/view' },
      { title: 'Family Polling', icon: 'mdi-chart-box', route: '/polls/view' },
      { title: 'Family Settings', icon: 'mdi-human-male-female-child', route: '/profile/families' },
      { title: 'User Settings', icon: 'mdi-account-cog', route: '/profile/settings' },
    ],
  }),
  computed: {
    ...mapGetters({ drawerMiniState: 'getDrawerMiniState' }),
    drawerState: {
      get() {
        return this.$store.getters.getDrawerState;
      },
      set(newValue) {
        this.setDrawerState({ state: newValue });
      },
    },
  },
  methods: {
    ...mapMutations({ setDrawerState: 'SET_DRAWER_STATE', setDrawerMiniState: 'SET_DRAWER_MINI_STATE' }),
    ...mapActions(['logoutUser']),
    onResize() {
      if (this.$vuetify.breakpoint.mdAndDown) {
        this.setDrawerMiniState({ state: false });
      }
    },
    async logout() {
      this.logoutUser();
      this.$router.push('/login');
    },
  },
};
</script>
