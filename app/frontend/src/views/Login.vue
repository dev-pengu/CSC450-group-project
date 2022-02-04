<template>
    <div class="login">
        <img alt="Logo" src="../assets/logo.png">
        <form @submit.prevent="login()">
            <p>Dont have an account? <router-link to="/signup">Sign up!</router-link></p>
            <input v-model="username" placeholder="Username"/>
            <input v-model="password" type="password" placeholder="Password"/>
            <button type="submit">Login</button>
            <p v-if="error" class="error" v-text="errors.join('<br/>')"></p>
        </form>
    </div>
</template>

<script>
import { defineComponent } from 'vue';
import { setCookie } from '@/util/CookieUtil';

export default defineComponent({
  name: 'Login',
  data: () => ({
    loginError: false,
    username: '',
    password: '',
    error: false,
    errors: [],
  }),
  methods: {
    async login() {
      this.errors = [];
      try {
        const res = await this.$store.dispatch('login', { username: this.username, password: this.password });
        if (res.status === 200) {
          const { data } = res;
          setCookie('Authorization', data.token, data.expires);
          this.$router.push('/');
        } else {
          this.loginError = true;
          this.errors.push('Error with credentials');
          this.error = true;
        }
      } catch (err) {
        this.loginError = true;
        this.errors.push(err);
        this.error = true;
      }
    },
  },
});
</script>

<style>
    .login form input{
        display: block;
        width: 100%;
        margin-left: auto;
        margin-right: auto;
        width: min(50%, 250px);
        margin-bottom: 1rem;
        font-size: 16px;
        padding: 5px;
    }
</style>
