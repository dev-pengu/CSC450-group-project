<template>
  <div class="signup">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col cols="12" sm="10" md="7">
        <v-stepper v-model="currentStep" alt-labels>
          <v-stepper-header>
            <v-stepper-step color="foa_button" step="1" :complete="currentStep > 1">Personal Info</v-stepper-step>
            <v-divider></v-divider>
            <v-stepper-step color="foa_button" step="2" :complete="currentStep > 2">Credentials</v-stepper-step>
          </v-stepper-header>
          <v-stepper-items>
            <v-stepper-content step="1">
              <v-alert v-if="error" class="mb-2" text type="error">{{ errorMsg }}</v-alert>
              <v-card>
                <v-card-text>
                  <v-form ref="step1Form" v-model="step1Valid">
                    <v-row>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="formData.firstName"
                          label="First Name"
                          color="foa_button"
                          :counter="50"
                          :rules="[rules.required, rules.max1]"
                        />
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="formData.lastName"
                          label="Last Name"
                          color="foa_button"
                          :counter="50"
                          :rules="[rules.required, rules.max1]"
                        />
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="formData.email"
                          type="email"
                          label="Email"
                          color="foa_button"
                          :counter="70"
                          :rules="[rules.required, rules.email, rules.max2]"
                        />
                      </v-col>
                      <v-col cols="12" md="6">
                        <v-select
                          v-model="formData.timezone"
                          :items="timezones"
                          label="Timezone"
                          color="foa_button"
                          item-color="foa_button"
                          :rules="[rules.required]"
                        ></v-select>
                      </v-col>
                    </v-row>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn small class="foa_button_text--text px-5" color="foa_button" elevation="2" to="/login">
                    Back to Login
                  </v-btn>
                  <v-btn
                    small
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
              <v-card>
                <v-card-text>
                  <v-form ref="step2Form" v-model="step2Valid">
                    <v-row>
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="formData.username"
                          label="Username"
                          color="foa_button"
                          :counter="50"
                          :rules="[rules.required, rules.max1]"
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
                          :rules="[rules.required, checkPassword]"
                          @click:append="show1 = !show1"
                        />
                        <v-text-field
                          v-model="formData.confirmPassword"
                          color="foa_button"
                          :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                          :type="show2 ? 'text' : 'password'"
                          label="Confirm Password"
                          :counter="32"
                          :rules="[rules.required, checkPassword, comparePasswords, rules.max3]"
                          @click:append="show2 = !show2"
                        />
                      </v-col>
                      <v-col cols="12" md="6" align-self="center">
                        <PasswordRequirement
                          :pw-length="passwordReqs.length"
                          :contains-lower-case="passwordReqs.lowerCase"
                          :contains-upper-case="passwordReqs.upperCase"
                          :contains-number="passwordReqs.number"
                          :contains-special="passwordReqs.special"
                        />
                      </v-col>
                    </v-row>
                  </v-form>
                </v-card-text>
                <v-card-actions>
                  <v-btn icon @click="currentStep -= 1">
                    <v-icon>mdi-arrow-left</v-icon>
                  </v-btn>
                  <v-spacer></v-spacer>
                  <v-btn small class="foa_button_text--text px-3" color="foa_button" elevation="2" to="/login">
                    Back to Login
                  </v-btn>
                  <v-btn
                    small
                    class="foa_button_text--text px-3"
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
    loading: false,
    show1: false,
    show2: false,
    rules: {
      max1: (v) => v.length <= 50 || 'Max 50 characters',
      max2: (v) => v.length <= 70 || 'Max 70 characters',
      max3: (v) => v.length <= 32 || 'Max 32 characters',
      required: (v) => !!v || 'This field is required for registration',
      email: (v) => /.+@.+\..+/.test(v) || 'Email must be valid',
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
    ...mapActions(['loginUser']),
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
          const loginRes = await this.loginUser(this.formData);
          if (loginRes.status === 200) {
            if (this.$route.query.code !== undefined && this.$route.query.code !== '') {
              this.$router.push(`/profile/families?code=${this.$route.query.code.trim()}`);
            } else {
              this.$router.push('/');
            }
          } else if (this.$route.query.code !== undefined && this.$route.query.code !== '') {
            this.$router.push(`/login?code=${this.$route.query.code}`);
          } else {
            this.$router.push('/login');
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
      if (this.formData.password === this.formData.confirmPassword) {
        return true;
      }
      return 'Passwords must match';
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
        this.errorMsg = 'This email is already in use.';
        this.error = true;
        return;
      }
      this.currentStep += 1;
    },
  },
};
</script>
