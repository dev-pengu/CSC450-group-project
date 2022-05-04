<template>
  <div class="pollCard d-inline-block">
    <v-dialog v-model="dialogState" persistent transition="dialog-bottom-transition" max-width="600">
      <template #activator="{ on, attrs }">
        <v-btn v-if="type === 'vote'" icon small v-bind="attrs" v-on="on"><v-icon>mdi-vote</v-icon></v-btn>
        <v-btn v-else-if="type === 'create'" icon :color="btnColor" v-bind="attrs" v-on="on"
          ><v-icon>mdi-plus-box</v-icon></v-btn
        >
      </template>
      <v-card>
        <v-card-title class="pt-0 justify-center foa_text_header--text pt-4 pb-4">
          {{ type === 'vote' ? 'Vote' : 'Create a Poll' }}
          <v-spacer></v-spacer>
          <v-btn class="pr-0" icon @click="closeDialog"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <PollCard
            v-if="type === 'vote'"
            :poll="poll"
            :success-callback="submitSuccessCallback"
            :failure-callback="submitFailureCallback"
          />
          <PollForm
            v-else-if="type === 'create'"
            :success-callback="submitSuccessCallback"
            :failure-callback="submitFailureCallback"
          />
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import PollCard from './PollCard.vue';
import PollForm from './PollForm.vue';

export default {
  name: 'PollDialog',
  components: {
    PollCard,
    PollForm,
  },
  props: {
    poll: {
      default: () => ({}),
      type: Object,
    },
    successCallback: {
      default: () => {},
      type: Function,
    },
    failureCallback: {
      default: () => {},
      type: Function,
    },
    type: {
      default: 'create',
      type: String,
      // eslint-disable-next-line
      validator: function (value) {
        return ['create', 'update', 'vote', 'view'].includes(value);
      },
    },
  },
  data: () => ({
    dialogState: false,
  }),
  computed: {
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  methods: {
    closeDialog() {
      this.dialogState = false;
    },
    submitSuccessCallback() {
      this.closeDialog();
      this.successCallback();
    },
    submitFailureCallback() {
      this.failureCallback();
    },
  },
};
</script>
