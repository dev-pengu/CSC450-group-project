<template>
  <div class="userSecurity">
    <v-row>
      <v-col cols="12" sm="5" md="4" lg="3">
        <ProfileNav />
      </v-col>
      <v-col cols="12" sm="7" md="8" lg="9">
        <v-dialog v-model="deleteDialog" max-width="500px">
          <v-card>
            <v-card-title class="text-h5"
              >Confirm
              <v-spacer></v-spacer>
              <v-btn class="pr-0" icon @click="closeDelete">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </v-card-title>
            <v-card-text
              >Are you sure you want to delete your account? This action is not reversible and you will lose all of your
              data. If you still wish to delete your account, please enter your password.</v-card-text
            >
            <v-row justify="center">
              <v-col cols="10" sm="8">
                <v-form v-model="confirmDeleteValid">
                  <v-text-field
                    v-model="deletePassword"
                    label="Password"
                    color="foa_button"
                    prepend-icon="mdi-lock"
                    :append-icon="show4 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show4 ? 'text' : 'password'"
                    :rules="[(v) => !!v || 'Password is required']"
                    @click:append="show4 = !show4"
                  ></v-text-field>
                </v-form>
                <v-alert v-if="deleteError" class="mb-0" text type="error">{{ deleteErrorMsg }}</v-alert>
              </v-col>
            </v-row>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                class="foa_button_text--text mr-4"
                color="error"
                elevation="2"
                max-width="150px"
                :disabled="loading"
                @click="closeDelete"
                >Cancel</v-btn
              >
              <v-btn
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                max-width="150px"
                :loading="loading"
                :disabled="loading || !confirmDeleteValid"
                @click="deleteAccountConfirm"
                >Yes</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-sheet max-width="400" color="#00000000">
          <h4 class="text-h5 foa_text_header--text mb-5">Change Password</h4>
          <v-form ref="pwForm" v-model="valid" class="ml-5">
            <v-text-field
              v-model="oldPassword"
              label="Old Password"
              color="foa_button"
              prepend-icon="mdi-lock"
              counter="32"
              max-length="32"
              :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
              :type="show1 ? 'text' : 'password'"
              :rules="[(v) => !!v || 'Password is required']"
              @click:append="show1 = !show1"
            ></v-text-field>
            <v-text-field
              ref="password"
              v-model="newPassword"
              color="foa_button"
              prepend-icon="mdi-lock"
              :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
              :type="show2 ? 'text' : 'password'"
              label="New Password"
              counter="32"
              max-length="32"
              :rules="[checkPassword, (v) => !!v || 'Password is required']"
              @click:append="show2 = !show2"
            />
            <PasswordRequirement
              v-if="newPassword.length > 0"
              class="mt-2"
              :pw-length="passwordReqs.length"
              :contains-lower-case="passwordReqs.lowerCase"
              :contains-upper-case="passwordReqs.upperCase"
              :contains-number="passwordReqs.number"
              :contains-special="passwordReqs.special"
            />
            <v-text-field
              ref="confirmPassword"
              v-model="confirmPassword"
              color="foa_button"
              prepend-icon="mdi-lock"
              :append-icon="show3 ? 'mdi-eye' : 'mdi-eye-off'"
              :type="show3 ? 'text' : 'password'"
              label="Confirm Password"
              counter="32"
              max-length="32"
              :rules="[comparePasswords, (v) => !!v || 'Password is required']"
              :error-messages="confirmPwdError"
              @click:append="show3 = !show3"
            />
            <v-alert v-if="error" class="mb-0" text type="error">{{ errorMsg }}</v-alert>
            <v-row class="mt-1">
              <v-col class="d-flex justify-end">
                <v-spacer></v-spacer>
                <v-btn
                  class="foa_button_text--text"
                  color="foa_button"
                  elevation="2"
                  :loading="loading"
                  :disabled="loading || !valid"
                  @click="submitPasswordChange"
                  >Submit</v-btn
                >
              </v-col>
            </v-row>
          </v-form>
          <h4 class="text-h5 foa_text_header--text mb-5">Delete Account</h4>
          <v-btn
            class="foa_button_text--text mr-4"
            color="error"
            elevation="2"
            max-width="150px"
            :disabled="loading"
            @click="deleteDialog = true"
            >Delete Account
          </v-btn>
        </v-sheet>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';
import ProfileNav from '../../components/profile/ProfileNav.vue';
import PasswordRequirement from '../../components/PasswordRequirement.vue';
import passwordChecker from '../../util/PasswordRequirementChecker';

export default {
  name: 'UserSecuritySettings',
  components: {
    ProfileNav,
    PasswordRequirement,
  },
  data: () => ({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    loading: false,
    valid: false,
    confirmDeleteValid: false,
    passwordReqs: {
      length: false,
      lowerCase: false,
      upperCase: false,
      number: false,
      special: false,
    },
    defaultPasswordReqs: {
      length: false,
      lowerCase: false,
      upperCase: false,
      number: false,
      special: false,
    },
    show1: false,
    show2: false,
    show3: false,
    show4: false,
    confirmPwdError: '',
    error: false,
    errorMsg: '',
    deleteError: false,
    deleteErrorMsg: '',
    deleteDialog: false,
    deletePassword: '',
  }),
  computed: {
    ...mapGetters({ user: 'getUser' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  watch: {
    deleteDialog(val) {
      if (!val) this.closeDelete();
    },
  },
  methods: {
    ...mapActions(['logoutUser', 'showSnackbar']),
    comparePasswords() {
      if (this.confirmPassword.length === 0) {
        return true;
      }
      if (this.newPassword === this.confirmPassword) {
        this.confirmPwdError = '';
        return true;
      }
      this.confirmPwdError = 'Passwords must match!';
      return false;
    },
    checkPassword() {
      this.passwordReqs = passwordChecker(this.newPassword);
      if (Object.values(this.passwordReqs).every((req) => req)) {
        return true;
      }
      return false;
    },
    async submitPasswordChange() {
      if (!this.valid) {
        return;
      }
      try {
        this.loading = true;
        const res = await api.changePassword({
          username: this.user.username,
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
        });
        if (res.status === 200) {
          this.$refs.pwForm.reset();
          this.confirmPassword = '';
          this.newPassword = '';
          this.oldPassword = '';
          this.valid = true;
          this.showSnackbar({ type: 'success', message: 'Your new password was saved successfully!', timeout: 3000 });
        } else if (res.data.errorCode === 2003) {
          this.showSnackbar({ type: 'error', message: 'Your password was incorrect.', timeout: 3000 });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an error processing your changes. Please try again in a few minutes!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error processing your changes. Please try again in a few minutes!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    deleteAccount() {
      this.deleteDialog = true;
    },
    closeDelete() {
      this.deleteDialog = false;
      this.$nextTick(() => {
        this.deletePassword = '';
      });
    },
    async deleteAccountConfirm() {
      if (!this.confirmDeleteValid) {
        return;
      }

      try {
        this.loading = true;
        const authRes = await api.login({ username: this.user.username, password: this.deletePassword });
        if (authRes.status !== 200) {
          this.deleteError = true;
          this.deleteErrorMsg = 'Your password was incorrect!';
          return;
        }
        const res = await api.deleteUser(this.user.username);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: 'Your account has been deleted, you will now be logged out.',
            timeout: 3000,
          });
          setTimeout(this.logoutUser, 2000);
        } else if (res.data.errorCode === 2003) {
          this.deleteError = true;
          this.deleteErrorMsg = 'You are not permitted to perform this action!';
        } else {
          this.deleteError = true;
          this.deleteErrorMsg = 'There was an error processing your request. Please try again in a few minutes.';
        }
      } catch (err) {
        this.deleteError = true;
        this.deleteErrorMsg = 'We ran into an issue deleting your account. Please try again in a few minutes.';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
