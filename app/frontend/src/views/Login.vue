<template>
  <div class="login">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col cols="10" sm="5" md="4">
        <v-card elevation="4">
          <v-card-text>
            <v-form v-model="valid">
              <v-text-field
                v-model="formData.username"
                color="foa_button"
                prepend-icon="mdi-account"
                :rules="usernameRules"
                label="Username"
                required
              />
              <v-text-field
                v-model="formData.password"
                color="foa_button"
                prepend-icon="mdi-lock"
                :rules="passwordRules"
                type="password"
                label="Password"
                required
                @keyup.enter="submit"
              />
              <div class="text-center">
                <router-link class="foa_link--text" to="#" @click="sendReset">Forgot your password?</router-link>
              </div>
            </v-form>
            <v-alert v-if="error" class="mb-0" text type="error">{{ errorMsg }}</v-alert>
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
          <v-card-text>
            <div class="text-center">
              Don't have an account? <router-link to="/signup" class="foa_link--text">Sign up now!</router-link>
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
    usernameRules: [(v) => !!v || 'Username is required'],
    passwordRules: [(v) => !!v || 'Password is required'],
    formData: {
      username: '',
      password: '',
    },
    error: false,
    errorMsg: '',
    loading: false,
  }),
  methods: {
    ...mapActions(['loginUser']),
    async submit() {
      try {
        this.loading = true;
        this.error = false;
        this.errorMsg = '';

        const res = await this.loginUser(this.formData);
        if (res.status === 200) {
          this.$router.push('/');
        } else {
          this.error = true;
          this.errorMsg = 'Your username or password was incorrect.';
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
      if (this.formData.username === '') {
        this.loading = false;
        this.error = true;
        this.errorMsg = 'Please supply a username to reset your password';
        return;
      }
      try {
        api.sendResetCode(this.formData);
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
