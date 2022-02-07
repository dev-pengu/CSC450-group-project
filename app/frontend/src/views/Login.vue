<template>
    <div class="login">
        <img alt="Logo" src="../assets/logo.png">
        <form @submit.prevent="submit()">
            <p>Dont have an account? <router-link to="/signup">Sign up!</router-link></p>
            <input v-model="formData.username" placeholder="Username"/>
            <input v-model="formData.password" type="password" placeholder="Password"/>
            <button type="submit">Login</button>
            <p v-if="error" class="error" v-text="errors.join('<br/>')"></p>
        </form>
    </div>
</template>

<script>
import { mapActions } from 'vuex';
import { setCookie } from '@/util/CookieUtil';

export default {
  name: 'Login',
  components: {},
  data: () => ({
    formData: {
      username: '',
      password: '',
    },
    error: false,
    errors: [],
  }),
  methods: {
    ...mapActions(['login']),
    async submit() {
      this.error = false;
      this.errors = [];
      if (this.formData.username === '' || this.formData.password === '') {
        return;
      }
      try {
        const res = await this.login(this.formData);
        if (res.status === 200) {
          const { data } = res;
          setCookie('Authorization', data.token, data.expires);
          this.$router.push('/');
        } else {
          this.errors.push('Error with credentials');
          this.error = true;
        }
      } catch (err) {
        const error = { err };
        if (error.err.isAxiosError) {
          this.errors.push(error.err.response.data);
        } else {
          this.errors.push(err);
        }
        this.error = true;
      }
    },
  },
};
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
