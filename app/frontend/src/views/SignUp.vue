<template>
  <div class="signup">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col vols="10" md="7">
        <v-stepper v-model="currentStep" alt-labels>
          <v-stepper-header>
            <v-stepper-step color="foa_button" step="1" :complete="currentStep > 1">Personal Info</v-stepper-step>

            <v-divider></v-divider>

            <v-stepper-step color="foa_button" step="2" :complete="currentStep > 2">Credentials</v-stepper-step>
          </v-stepper-header>
          <v-stepper-items>
            <v-stepper-content step="1">
              <v-alert v-if="error" class="mb-2" text type="error">{{ errorMsg }}</v-alert>
              <v-card color="#00000000">
                <v-card-text>
                  <v-form ref="step1Form" v-model="step1Valid">
                    <v-text-field
                      v-model="formData.firstName"
                      label="First Name"
                      :counter="50"
                      :rules="[rules.required, rules.max1]"
                    />
                    <v-text-field
                      v-model="formData.lastName"
                      label="Last Name"
                      :counter="50"
                      :rules="[rules.required, rules.max1]"
                    />
                    <v-text-field
                      v-model="formData.email"
                      type="email"
                      label="Email"
                      :counter="70"
                      :rules="[rules.required, rules.email, rules.max2]"
                    />
                    <v-select
                      v-model="formData.timezone"
                      :items="timezones"
                      label="Timezone"
                      :rules="[rules.required]"
                    ></v-select>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    class="foa_button_text--text px-5"
                    color="foa_button"
                    elevation="2"
                    :disabled="!step1Valid"
                    @click="verifyStep1"
                  >
                    Continue
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-stepper-content>
            <v-stepper-content step="2">
              <v-alert v-if="error" class="mb-2" text type="error">{{ errorMsg }}</v-alert>
              <v-card color="#00000000">
                <v-card-text>
                  <v-form ref="step2Form" v-model="step2Valid">
                    <v-text-field
                      v-model="formData.username"
                      label="Username"
                      :counter="50"
                      :rules="[rules.max1]"
                      required
                    />
                    <v-text-field
                      ref="password"
                      v-model="formData.password"
                      color="foa_button"
                      :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                      :type="show1 ? 'text' : 'password'"
                      label="Password"
                      counter="32"
                      max-length="32"
                      required
                      :rules="[checkPassword]"
                      @click:append="show1 = !show1"
                    />
                    <PasswordRequirement
                      v-if="Object.values(passwordReqs).some((req) => !req)"
                      class="mt-2"
                      :pw-length="passwordReqs.length"
                      :contains-lower-case="passwordReqs.lowerCase"
                      :contains-upper-case="passwordReqs.upperCase"
                      :contains-number="passwordReqs.number"
                      :contains-special="passwordReqs.special"
                    />
                    <v-text-field
                      v-model="formData.confirmPassword"
                      color="foa_button"
                      :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                      :type="show2 ? 'text' : 'password'"
                      label="Confirm Password"
                      :counter="32"
                      :rules="[comparePasswords, rules.max3]"
                      @click:append="show2 = !show2"
                    />
                    <v-alert v-if="confirmPwdError" class="mb-2" text type="error">{{ confirmPwdErrorMsg }}</v-alert>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-btn class="foa_button_text--text px-5" color="foa_button" elevation="2" @click="currentStep -= 1">
                    Back
                  </v-btn>
                  <v-spacer></v-spacer>
                  <v-btn
                    class="foa_button_text--text px-5"
                    color="foa_button"
                    elevation="2"
                    :loading="loading"
                    :disabled="!step2Valid"
                    @click="signUp"
                  >
                    Sign Up
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-stepper-content>
          </v-stepper-items>
        </v-stepper>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../api';
import PasswordRequirement from '../components/PasswordRequirement.vue';
import passwordChecker from '../util/PasswordRequirementChecker';

export default {
  name: 'SignUp',
  components: {
    PasswordRequirement,
  },

  data: () => ({
    formData: {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: '',
      timezone: '',
    },
    timezones: [],
    error: false,
    errorMsg: '',
    confirmPwdError: false,
    confirmPwdErrorMsg: '',
    loading: false,
    show1: false,
    show2: false,
    rules: {
      max1: (v) => (v && v.length <= 50) || 'Max 50 characters',
      max2: (v) => (v && v.length <= 70) || 'Max 70 characters',
      max3: (v) => (v && v.length <= 32) || 'Max 32 characters',
      required: (v) => !!v || 'This field is required for registration',
      email: (v) => /.+@.+\..+/.test(v) || 'E-mail must be a valid email',
    },
    passwordReqs: {
      length: false,
      lowerCase: false,
      upperCase: false,
      number: false,
      special: false,
    },
    step1Complete: false,
    step2Complete: false,
    currentStep: 1,
    step1Valid: false,
    step2Valid: false,
  }),
  async created() {
    const res = await api.getTimezones();
    this.timezones = res.data;
    this.formData.timezone = 'US/Central';
  },
  methods: {
    ...mapActions(['login']),
    async signUp() {
      this.error = false;
      this.errorMsg = '';
      if (!this.$refs.step2Form.validate()) {
        return;
      }
      try {
        this.loading = true;
        const unCheckRes = await api.usernameFree(this.formData.username);
        if (unCheckRes.status === 200 && !unCheckRes.data) {
          this.errorMsg = 'Username is already in use.';
          this.error = true;
          return;
        }

        const res = await api.createUser(this.formData);
        if (res.status === 201) {
          const loginRes = await this.login(this.formData);
          if (loginRes.status === 200) {
            this.$router.push('/');
          } else {
            this.errorMsg = 'We ran into an error creating your user. Please try again in a few minutes.';
            this.error = true;
          }
        } else {
          this.errorMsg = 'We ran into an error creating your user. Please try again in a few minutes.';
          this.error = true;
        }
      } catch (err) {
        const error = { err };
        if (error.err.isAxiosError) {
          this.errorMsg = 'We ran into an error creating your user. Please try again in a few minutes.';
        } else {
          this.errorMsg = 'We ran into an error creating your user. Please try again in a few minutes.';
        }
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    comparePasswords() {
      this.confirmPwdErrorMsg = '';
      this.confirmPwdError = false;
      if (this.formData.confirmPassword.length === 0) {
        return true;
      }
      if (this.formData.password === this.formData.confirmPassword) {
        return true;
      }
      this.confirmPwdErrorMsg = 'Passwords must match!';
      this.confirmPwdError = true;
      return false;
    },
    checkPassword() {
      this.passwordReqs = passwordChecker(this.formData.password);
      if (Object.values(this.passwordReqs).every((req) => req)) {
        return true;
      }
      return false;
    },
    async verifyStep1() {
      this.error = false;
      this.errorMsg = '';
      if (!this.$refs.step1Form.validate()) {
        return;
      }
      const res = await api.emailFree(this.formData.email);
      if (res.status === 200 && !res.data) {
        this.errorMsg = 'E-mail is already in use.';
        this.error = true;
        return;
      }
      this.currentStep += 1;
    },
  },
};
</script>
