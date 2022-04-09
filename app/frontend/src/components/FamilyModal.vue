<template>
  <div class="familymodal d-inline-block">
    <v-dialog v-model="dialogState" persistent transition="dialog-bottom-transition" max-width="600">
      <template #activator="{ on, attrs }">
        <v-btn class="pb-1" icon color="foa_button_dark" v-bind="attrs" v-on="on"><v-icon>mdi-plus-box</v-icon></v-btn>
      </template>
      <v-card>
        <v-card-actions class="pb-0">
          <v-spacer></v-spacer>
          <v-btn class="pr-0" icon @click="dialogState = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-actions>
        <v-card-title class="pt-0 justify-center foa_text_header--text">Join a Family</v-card-title>
        <v-card-text>
          <v-row justify="center">
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="joinFamilyData.inviteCode"
                hide-details
                outlined
                color="foa_button"
                label="Invite Code"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6" class="pt-0">
              <ColorPicker ref="joinPersonalColorPicker" label="Personal Event Color" />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6" class="py-0">
              <v-alert v-if="joinError" class="mb-2" text type="error">{{ joinErrorMsg }}</v-alert>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="6" sm="4">
              <v-btn block color="foa_button" class="foa_button_text--text" @click="submitJoinFamily">Join</v-btn>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-title class="justify-center foa_text_header--text">Create a Family</v-card-title>
        <v-card-text>
          <v-row justify="center">
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="createFamilyData.familyName"
                hide-details
                outlined
                color="foa_button"
                label="Family Name"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6">
              <v-select
                v-model="createFamilyData.timezone"
                :items="timezones"
                hide-details
                outlined
                color="foa_button"
                item-color="foa_button"
                label="Timezone"
              ></v-select>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6" class="pt-0">
              <ColorPicker ref="createFamilyColorPicker" label="Family Event Color" />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6" class="pt-0">
              <ColorPicker ref="createFamilyPersonalColorPicker" label="Personal Event Color" />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" sm="6" class="py-0">
              <v-alert v-if="createError" class="mb-2" text type="error">{{ createErrorMsg }}</v-alert>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="6" sm="4">
              <v-btn block color="foa_button" class="foa_button_text--text" @click="submitCreateFamily">Create</v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapActions, mapMutations } from 'vuex';
import ColorPicker from './ColorPicker.vue';
import api from '../api';

export default {
  name: 'FamilyModal',
  components: {
    ColorPicker,
  },
  data: () => ({
    dialogState: false,
    timezones: [],
    joinFamilyData: {
      inviteCode: '',
      personalEventColor: '',
    },
    createFamilyData: {
      familyName: '',
      timezone: '',
      familyEventColor: '',
      personalEventColor: '',
    },
    joinError: false,
    createError: false,
    joinErrorMsg: '',
    createErrorMsg: '',
  }),
  async created() {
    const res = await api.getTimezones();
    this.timezones = res.data;
    this.createFamilyData.timezone = 'US/Central';
  },
  methods: {
    ...mapActions(['fetchFamilies']),
    ...mapMutations({ setSnackbarState: 'SET_SNACKBAR_STATE', setSnackbarMessage: 'SET_SNACKBAR_MESSAGE' }),
    async submitJoinFamily() {
      try {
        this.joinFamilyData.personalEventColor = this.$refs.joinPersonalColorPicker.color;
        const res = await api.joinFamily(this.joinFamilyData);
        if (res.status === 200) {
          this.dialogState = false;
          this.fetchFamilies();
          this.setSnackbarState({ state: true });
          this.setSnackbarMessage({ message: 'You have successfully joined a family!' });
        } else {
          this.joinError = true;
          this.joinErrorMsg =
            'There was a problem joining this family. Make sure the invite code is correct and try again.';
        }
      } catch (err) {
        this.joinError = true;
        this.joinErrorMsg =
          'There was a problem joining this family. Make sure the invite code is correct and try again.';
      }
    },
    async submitCreateFamily() {
      try {
        this.createFamilyData.familyEventColor = this.$refs.createFamilyColorPicker.color;
        this.createFamilyData.personalEventColor = this.$refs.createFamilyPersonalColorPicker.color;
        const res = await api.createFamily(this.createFamilyData);
        if (res.status === 201) {
          this.dialogState = false;
          this.fetchFamilies();
          this.setSnackbarState({ state: true });
          this.setSnackbarMessage({ message: 'You have successfully created a family!' });
        } else {
          this.createError = true;
          this.createErrorMsg = 'There was a problem creating your family, please try again.';
        }
      } catch (err) {
        this.createError = true;
        this.createErrorMsg = 'There was a problem creating your family, please try again.';
      }
    },
  },
};
</script>
