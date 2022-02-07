<template>
  <div id="nav">
    <router-link to="/">Home</router-link> |
    <a @click="logout">Logout</a>
  </div>
</template>

<script>
export default {
  name: 'NavBar',
  computed: {
    isLoggedIn: () => this.$store.getters.isLoggedIn,
  },
  methods: {
    async logout() {
      await this.$store.dispatch('logout');
      this.$router.push('/login');
    },
  },
};
</script>
