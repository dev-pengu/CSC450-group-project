<template>
  <div class="login">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col cols="10" sm="5" md="4">
        <v-card elevation="4">
          <v-card-text class="pb-0">
            <v-form v-model="valid">
              <v-text-field
                v-model="formData.usernameEmail"
                color="foa_button"
                prepend-icon="mdi-account"
                :rules="required"
                label="Username or Email"
                :disabled="loading"
              />
              <v-text-field
                v-model="formData.password"
                color="foa_button"
                :rules="required"
                prepend-icon="mdi-lock"
                :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPass ? 'text' : 'password'"
                label="Password"
                :disabled="loading"
                @keyup.enter="submit"
                @click:append="showPass = !showPass"
              />
            </v-form>
            <v-alert v-if="error" class="my-2" text type="error">{{ errorMsg }}</v-alert>
          </v-card-text>
          <v-card-actions class="justify-center">
            <v-btn
              class="foa_button_text--text"
              color="foa_button"
              elevation="2"
              width="50%"
              :loading="loading"
              :disabled="loading || !valid"
              @click="submit"
              >Login</v-btn
            >
          </v-card-actions>
          <v-card-text class="pt-2">
            <div class="text-center">
              <span class="foa_link--text text-decoration-underline" @click="sendReset">Forgot your password?</span>
            </div>
            <div class="text-center">
              Don't have an account?
              <router-link :to="{ path: '/signup', query: { code: $route.query.code } }" class="foa_link--text"
                >Sign up now!</router-link
              >
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../api';

export default {
  name: 'Login',
  components: {},
  data: () => ({
    valid: false,
    required: [(v) => !!v || 'This field is required'],
    formData: {
      usernameEmail: '',
      password: '',
    },
    error: false,
    errorMsg: '',
    loading: false,
    showPass: false,
  }),
  methods: {
    ...mapActions(['loginUser', 'showSnackbar']),
    isEmail(usernameEmail) {
      return /.+@.+\..+/.test(usernameEmail);
    },
    async submit() {
      if (!this.valid) {
        return;
      }
      try {
        this.loading = true;
        this.error = false;
        this.errorMsg = '';
        await api.getCsrf();
        const userCredentials = {};
        userCredentials.password = this.formData.password.trim();
        if (this.isEmail(this.formData.usernameEmail)) {
          userCredentials.email = this.formData.usernameEmail;
        } else {
          userCredentials.username = this.formData.usernameEmail;
        }
        const res = await this.loginUser(userCredentials);
        if (res.status === 200) {
          this.$vuetify.theme.dark = res.data.useDarkMode;
          localStorage.setItem('darkMode', this.$vuetify.theme.dark.toString());
          if (this.$route.query.code !== undefined && this.$route.query.code !== '') {
            this.$router.push(`/profile/families?code=${this.$route.query.code.trim()}`);
          } else {
            this.$router.push('/');
          }
        } else if (res.status === 401 && res.data.errorCode === 1010) {
          this.error = true;
          this.errorMsg =
            'You have been locked out of your account after too many failed attempts. Please reset your password to regain access.';
        } else if (res.status === 403) {
          this.error = true;
          this.errorMsg = 'Your username or password was incorrect.';
        } else {
          this.error = true;
          this.errorMsg = 'We ran into an error while validating your credentials. Please try again in a few minutes.';
        }
      } catch (err) {
        this.error = true;
        this.errorMsg = 'Login failed, please try again.';
      } finally {
        this.loading = false;
      }
    },
    async sendReset() {
      this.loading = true;
      this.error = false;
      this.errorMsg = '';
      if (this.formData.usernameEmail === '' || this.isEmail(this.formData.usernameEmail)) {
        this.loading = false;
        this.error = true;
        this.errorMsg = 'Please supply a username to reset your password.';
        return;
      }
      try {
        api.sendResetCode(this.formData.usernameEmail.trim());
        this.showSnackbar({ type: 'info', message: 'An email has been sent to your email on file.', timeout: 3000 });
      } catch (err) {
        this.error = true;
        const error = { err };
        if (error.err.isAxiosError) {
          this.errorMsg = error.err.response.data;
        } else {
          this.errorMsg = err;
        }
      }
      this.loading = false;
    },
  },
};
</script>

<style scoped></style>
