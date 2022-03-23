<template>
  <div class="login">
    <v-img height="250" contain src="../assets/logo.png"></v-img>
    <v-row justify="center">
      <v-col cols="10" md="4">
        <v-card elevation="4">
          <v-card-text>
            <v-form>
              <v-row>
                <v-col cols="12">
                  <v-text-field v-model="formData.username" prepend-icon="mdi-account" label="Username" />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="formData.password"
                    prepend-icon="mdi-lock"
                    type="password"
                    label="Password"
                    @keyup.enter="submit"
                  />
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
                <v-btn block color="primary" elevation="2" :loading="loading" :disabled="loading" @click="submit"
                  >Login</v-btn
                >
              </v-col>
            </v-row>
          </v-card-actions>
          <v-card-text>
            <p class="ma-0 text-center">Don't have an account? <router-link to="/signup">Sign up now!</router-link></p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'Login',
  components: {},
  data: () => ({
    formData: {
      username: '',
      password: '',
    },
    error: false,
    errorMsg: '',
    loading: false,
  }),
  methods: {
    ...mapActions(['login']),
    async submit() {
      this.loading = true;
      this.error = false;
      this.errors = [];
      if (this.formData.username === '' || this.formData.password === '') {
        this.loading = false;
        return;
      }
      try {
        const res = await this.login(this.formData);
        if (res.status === 200) {
          this.$router.push('/');
        } else {
          this.error = true;
          console.log('test');
          this.errorMsg('Username or password is incorrect');
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
  },
};
</script>

<style scoped lang="less"></style>
