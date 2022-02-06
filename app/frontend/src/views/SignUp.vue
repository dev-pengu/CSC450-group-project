<template>
    <div class="signup">
        <img alt="Logo" src="../assets/logo.png">
        <form @submit.prevent="signUp()">
            <input v-model="formData.username" placeholder="Username"/>
            <input v-model="formData.email" placeholder="Email" type="email"/>
            <input v-model="formData.firstName" placeholder="First Name" />
            <input v-model="formData.lastName" placeholder="Last Name" />
            <input v-model="formData.password" type="password" placeholder="Password"/>
            <input v-model="formData.confirmPassword" type="password"
              placeholder="Confirm Password"/>
            <button type="submit">Sign Up</button>
            <p v-if="error" class="error" v-text="errors.join('<br/>')"></p>
        </form>
    </div>
</template>

<script>
import { mapActions } from 'vuex';
import { setCookie } from '@/util/CookieUtil';
import api from '../api';

export default {
  name: 'SignUp',
  data: () => ({
    formData: {
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      firstName: '',
      lastName: '',
    },
    error: false,
    errors: [],
  }),
  methods: {
    ...mapActions(['login']),
    async signUp() {
      this.errors = [];
      this.error = false;
      if (this.password !== this.confirmPassword) {
        this.errors.push('Passwords do not match');
        this.error = true;
        return;
      }
      try {
        const res = await api.createUser(this.formData);
        if (res.status === 201) {
          const loginRes = await this.login(this.formData);
          if (loginRes.status === 200) {
            const { data } = res;
            setCookie('Authorization', data.token, data.expires);
            this.$router.push('/');
          } else {
            this.$router.push('/login');
          }
        } else {
          this.errors.push('Error creating user.');
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
    .signup form input{
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
