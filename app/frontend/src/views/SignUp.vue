<template>
  <div class="signup">
    <v-img v-if="$vuetify.theme.dark" height="250" contain src="@/assets/logo-dark.png"></v-img>
    <v-img v-else height="250" contain src="@/assets/logo-light.png"></v-img>
    <v-row justify="center">
      <v-col vols="10" md="4">
        <v-card elevation="4">
          <v-card-text>
            <v-form>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.username" label="Username" required />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.email" type="email" label="Email" />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.firstName" label="First Name" />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.lastName" label="Last Name" />
                </v-col>
              </v-row>
              <v-row align="center">
                <v-col cols="12">
                  <v-select v-model="formData.timezone" :items="timezones" label="Timezone" required></v-select>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.password" type="password" label="Password" />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.confirmPassword" type="password" label="Confirm Password" />
                </v-col>
              </v-row>
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

export default {
  name: 'SignUp',
  data: () => ({
    formData: {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: '',
      timezone: 'US/Central',
    },
    error: false,
    errors: [],
    timezones: ['US/Central', 'US/Eastern'],
  }),
  methods: {
    ...mapActions(['loginUser']),
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
          const loginRes = await this.loginUser(this.formData);
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
  },
};
</script>
