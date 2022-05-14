<template>
  <div class="inviteModal d-inline-block">
    <v-dialog v-model="dialogState" persistent transition="dialog-bottom-transition" max-width="600">
      <template #activator="{ on, attrs }">
        <div class="d-block foa_link--text text-decoration-underline" v-bind="attrs" v-on="on">
          Invite a Family Member
        </div>
      </template>
      <v-card>
        <v-card-actions class="pb-0">
          <v-spacer></v-spacer>
          <v-btn class="pr-0" icon @click="dialogState = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-actions>
        <v-card-title class="py-0 justify-center foa_text_header--text">Invite a Family Member to</v-card-title>
        <v-card-title class="pt-0 mb-2 justify-center foa_text_header--text">{{ familyName }}</v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-row justify="center">
              <v-col cols="12" sm="6" class="pb-0">
                <v-text-field
                  v-model="formData.recipientEmail"
                  outlined
                  color="foa_button"
                  label="User's Email"
                  append-icon="mdi-email"
                  type="email"
                  required
                  :rules="emailRules"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row justify="center">
              <v-col cols="12" sm="6">
                <v-select
                  v-model="formData.role"
                  :items="roles"
                  hide-details
                  outlined
                  color="foa_button"
                  item-color="foa_button"
                  label="Starting Role"
                ></v-select>
              </v-col>
            </v-row>
            <v-row justify="center">
              <v-col cols="6" sm="4">
                <v-btn :disabled="!valid" block color="foa_button" class="foa_button_text--text" @click="submit">
                  Send
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../api';

export default {
  name: 'InviteModal',
  props: {
    familyId: {
      default: 0,
      type: Number,
    },
    familyName: {
      default: '',
      type: String,
    },
  },
  data: (instance) => ({
    dialogState: false,
    valid: false,
    emailRules: [(v) => !!v || 'Email is required', (v) => /.+@.+\..+/.test(v) || 'Email must be valid'],
    roles: ['CHILD', 'ADULT', 'ADMIN'],
    formData: {
      family: instance.familyId,
      recipientEmail: '',
      role: 'CHILD',
    },
  }),
  methods: {
    ...mapActions(['showSnackbar']),
    async submit() {
      try {
        await api.sendInviteCode(this.formData);
        this.dialogState = false;
        this.showSnackbar({ type: 'success', message: `You've sent an invite to ${this.formData.recipientEmail}!` });
        this.$refs.form.reset();
      } catch (err) {
        this.dialogState = false;
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an issue sending the invite, please try again later.',
        });
        this.$refs.form.reset();
      }
    },
  },
};
</script>
