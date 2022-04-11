<template>
  <div class="login">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col cols="10" sm="7" md="5">
        <v-card elevation="4">
          <v-card-text class="pb-0">
            <v-form>
              <v-text-field
                v-model="formData.identifier"
                color="foa_button"
                prepend-icon="mdi-account"
                label="Username/Email"
                :rules="[rules.required]"
                clearable
              />
              <v-text-field
                ref="password"
                v-model="formData.newPassword"
                color="foa_button"
                prepend-icon="mdi-lock"
                :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show1 ? 'text' : 'password'"
                label="New Password"
                counter="32"
                max-length="32"
                required
                :rules="[checkPassword]"
                @click:append="show1 = !show1"
              />
              <v-row justify="center">
                <v-col cols="12">
                  <PasswordRequirement
                    v-if="Object.values(passwordReqs).some((req) => !req)"
                    class="mt-2"
                    :pw-length="passwordReqs.length"
                    :contains-lower-case="passwordReqs.lowerCase"
                    :contains-upper-case="passwordReqs.upperCase"
                    :contains-number="passwordReqs.number"
                    :contains-special="passwordReqs.special"
                  />
                </v-col>
              </v-row>
              <v-text-field
                ref="confirmPassword"
                v-model="formData.confirmPassword"
                color="foa_button"
                prepend-icon="mdi-lock"
                :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show2 ? 'text' : 'password'"
                label="Confirm Password"
                required
                :rules="[comparePasswords]"
                :error-messages="confirmPwdError"
                @click:append="show2 = !show2"
                @keyup.enter="submit"
              />
            </v-form>
            <v-alert v-if="error" class="mb-2" text type="error">{{ errorMsg }}</v-alert>
          </v-card-text>
          <v-card-actions class="justify-center">
            <v-btn
              class="foa_button_text--text"
              color="foa_button"
              elevation="2"
              width="70%"
              :loading="loading"
              :disabled="loading"
              @click="submit"
              >Reset Password</v-btn
            >
          </v-card-actions>
          <v-card-text>
            <div class="text-center">
              Go back to the <router-link to="/login" class="foa_link--text">login</router-link> page
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import api from '../api';
import PasswordRequirement from '../components/PasswordRequirement.vue';
import passwordChecker from '../util/PasswordRequirementChecker';

export default {
  name: 'PasswordReset',
  components: {
    PasswordRequirement,
  },
  data: () => ({
    identifier: '',
    formData: {
      username: '',
      email: '',
      newPassword: '',
      confirmPassword: '',
      resetCode: null,
    },
    error: false,
    errorMsg: '',
    confirmPwdError: '',
    loading: false,
    show1: false,
    show2: false,
    passwordReqs: {
      length: false,
      lowerCase: false,
      upperCase: false,
      number: false,
      special: false,
    },
  }),
  methods: {
    async submit() {
      this.formData.resetCode = this.$route.query.code;
      if (this.identifier.includes('@')) {
        this.formData.email = this.identifier;
      } else {
        this.formData.username = this.identifier;
      }

      this.loading = true;
      this.error = false;
      this.errorMsg = '';
      if (this.formData.username === '' && this.formData.email === '') {
        this.loading = false;
        this.error = true;
        this.errorMsg = 'Please supply your username or email to reset your password.';
        return;
      }

      try {
        const res = await api.changePassword(this.formData);
        if (res.status === 200) {
          this.$router.push('/');
          // TODO: add snackbar notification indicating password has been changed successfully
        } else if (res.data.errorCode === 1001) {
          this.error = true;
          this.errorMsg = 'User profile not found. Check your username/email.';
        } else if (res.data.errorCode === 2003) {
          this.error = true;
          this.errorMsg = 'You are not permitted to use this code.';
        } else {
          this.error = true;
          this.errorMsg = 'We ran into an error resetting your password.';
        }
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
    comparePasswords() {
      if (this.formData.confirmPassword.length === 0) {
        return true;
      }
      if (this.formData.newPassword === this.formData.confirmPassword) {
        this.confirmPwdError = '';
        return true;
      }
      this.confirmPwdError = 'Passwords must match!';
      return false;
    },
    checkPassword() {
      this.passwordReqs = passwordChecker(this.formData.newPassword);
      if (Object.values(this.passwordReqs).every((req) => req)) {
        return true;
      }
      return false;
    },
  },
};
</script>

<style scoped></style>
