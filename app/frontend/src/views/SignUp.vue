<template>
  <div class="signup">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col vols="10" md="4">
        <v-card elevation="4">
          <v-card-text>
            <v-form>
              <v-stepper v-model="e1">
                <v-stepper-header>
                  <v-stepper-step :complete="e1 > 1" step="1"></v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step :complete="e1 > 2" step="2"></v-stepper-step>
                </v-stepper-header>
                <v-stepper-items>
                  <v-stepper-content step="1">
                    <v-card>
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            v-model="formData.firstName"
                            label="First Name"
                            :counter="50"
                            :rules="[rules.max1]"
                          />
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            v-model="formData.lastName"
                            label="Last Name"
                            :counter="50"
                            :rules="[rules.max1]"
                          />
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            v-model="formData.email"
                            type="email"
                            label="Email"
                            :counter="70"
                            :rules="[rules.max2]"
                          />
                        </v-col>
                      </v-row>
                    </v-card>
                    <v-row align="center">
                      <v-col cols="12">
                        <v-select v-model="formData.timezone" :items="timezones" label="Timezone" required></v-select>
                      </v-col>
                    </v-row>
                    <v-btn color="primary" @click="e1 = 2"> Continue </v-btn>
                  </v-stepper-content>
                  <v-stepper-content step="2">
                    <v-card>
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            v-model="formData.username"
                            label="Username"
                            :counter="50"
                            :rules="[rules.max1]"
                            required
                          />
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            ref="password"
                            v-model="formData.Password"
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
                        </v-col>
                      </v-row>
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
                      <v-row>
                        <v-col cols="12">
                          <v-text-field
                            v-model="formData.confirmPassword"
                            type="password"
                            label="Confirm Password"
                            :counter="32"
                            :rules="[rules.max3]"
                          />
                        </v-col>
                      </v-row>
                    </v-card>
                    <v-btn color="primary" @click="e1 = 1"> Back </v-btn>
                  </v-stepper-content>
                </v-stepper-items>
              </v-stepper>
            </v-form>
            <v-row>
              <v-col cols="12" class="py-0">
                <v-alert v-if="error" class="mb-0" text type="error">{{ errorMsg }}</v-alert>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-row justify="center">
              <v-col cols="12" sm="6">
                <v-btn color="primary" block elevation="2" @click="signUp">Sign Up</v-btn>
              </v-col>
            </v-row>
          </v-card-actions>
        </v-card>
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
      timezone: [],
    },
    e1: 1,
    error: false,
    errors: [],
    errorMsg: '',
    confirmPwdError: '',
    loading: false,
    show1: false,
    show2: false,
    rules: {
      max1: (v) => v.length <= 50 || 'Max 50 characters',
      max2: (v) => v.length <= 70 || 'Max 70 characters',
      max3: (v) => v.length <= 32 || 'Max 32 characters',
    },
    passwordReqs: {
      length: false,
      lowerCase: false,
      upperCase: false,
      number: false,
      special: false,
    },
  }),
  async created() {
    const res = await api.getTimezones();
    this.timezones = res.data;
    this.createFamilyData.timezone = 'US/Central';
  },
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
