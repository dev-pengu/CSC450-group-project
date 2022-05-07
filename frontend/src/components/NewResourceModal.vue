<template>
  <v-dialog v-model="addEditDialogState" persistent max-width="500px">
    <template #activator="{ on, attrs }">
      <v-btn icon :color="btnColor" v-bind="attrs" v-on="on">
        <v-icon>mdi-plus-box</v-icon>
      </v-btn>
    </template>
    <v-card>
      <div class="d-flex justify-space-between align-center">
        <v-card-title class="text-h5 foa_text_header--text"> {{ formTitle }}</v-card-title>
        <v-btn class="mr-5" color="error" icon @click="close"><v-icon>mdi-close</v-icon></v-btn>
      </div>
      <v-card-text v-if="availableFamilies && availableFamilies.length > 0">
        <v-form ref="form" v-model="valid">
          <v-row>
            <v-col cols="12">
              <v-select
                v-model="item.familyId"
                :disabled="familyIdDisabled"
                :items="availableFamilies"
                outlined
                color="foa_button"
                item-color="foa_button"
                :rules="familyRules"
                label="Family"
                item-text="name"
                item-value="id"
              ></v-select>
            </v-col>
            <v-col class="py-0" cols="12">
              <v-text-field
                v-model="item.description"
                :rules="descriptionRules"
                color="foa_button"
                outlined
                label="Description"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-text v-else>
        <div>You are not allowed to create calendars for your families or you are not a member of any families.</div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" text @click="close">Cancel</v-btn>
        <v-btn
          v-if="availableFamilies && availableFamilies.length > 0"
          color="foa_button"
          :disabled="!valid"
          text
          @click="submit"
          >{{ formAction }}</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from 'vuex';
import { isAdmin } from '../util/RoleUtil';

export default {
  name: 'NewResourceModal',
  props: {
    formTitle: {
      default: '',
      type: String,
    },
    formAction: {
      default: 'Submit',
      type: String,
    },
    familyIdDisabled: {
      default: false,
      type: Boolean,
    },
    successCode: {
      default: 201,
      type: Number,
    },
    apiCallback: {
      default: () => {},
      type: Function,
    },
    onSuccessCallback: {
      default: () => {},
      type: Function,
    },
    onFailureCallback: {
      default: () => {},
      type: Function,
    },
    limitToAdmin: {
      default: true,
      type: Boolean,
    },
  },
  data: () => ({
    addEditDialogState: false,
    valid: false,
    item: {
      familyId: -1,
      description: '',
    },
    familyRules: [(v) => !!v || 'Family is required'],
    descriptionRules: [(v) => !!v || 'Description is required'],
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
    availableFamilies() {
      return this.families.filter((family) => !this.limitToAdmin || isAdmin(family.memberData.role));
    },
  },
  methods: {
    close() {
      this.addEditDialogState = false;
      this.$refs.form.reset();
      this.$nextTick(() => {
        this.item = {};
      });
    },
    async submit() {
      try {
        const request = this.item;
        const res = await this.apiCallback(request);
        if (res.status === this.successCode) {
          this.close();
          this.onSuccessCallback();
        } else {
          this.close();
          this.onFailureCallback();
        }
      } catch (err) {
        this.close();
        this.onFailureCallback();
      }
    },
    isAdmin,
  },
};
</script>
