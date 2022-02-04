<template>
    <div class="signup">
        <img alt="Logo" src="../assets/logo.png">
        <form @submit.prevent="signUp()">
            <input v-model="username" placeholder="Username"/>
            <input v-model="email" placeholder="Email" type="email"/>
            <input v-model="firstName" placeholder="First Name" />
            <input v-model="lastName" placeholder="Last Name" />
            <input v-model="password" type="password" placeholder="Password"/>
            <input v-model="confirmPassword" type="password" placeholder="Confirm Password"/>
            <button type="submit">Sign Up</button>
            <p v-if="error" class="error" v-text="errors.join('<br/>')"></p>
        </form>
    </div>
</template>

<script>
import { defineComponent } from 'vue';
import { setCookie } from '@/util/CookieUtil';

export default defineComponent({
  name: 'Sign Up',
  data: () => ({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    firstName: '',
    lastName: '',
    error: false,
    errors: [],
  }),
  methods: {
    async signUp() {
      this.errors = [];
      if (this.password !== this.confirmPassword) {
        this.errors.push('Passwords do not match');
        this.error = true;
        return;
      }
      try {
        const res = await this.$store.dispatch('signUp', {
          username: this.username,
          password: this.password,
          email: this.email,
          firstName: this.firstName,
          lastName: this.lastName,
        });
        if (res.status === 201) {
          const loginRes = await this.$store.dispatch('login', {
            username: this.username,
            password: this.password,
          });
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
        this.errors.push(err);
        this.error = true;
      }
    },
  },
});
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
