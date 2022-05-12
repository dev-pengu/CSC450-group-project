<template>
  <v-sheet
    width="100%"
    :color="$vuetify.theme.dark ? '#6D6968' : 'white'"
    rounded
    class="pl-2 py-2"
    elevation="2"
    outlined
  >
    <h6 class="text-subtitle-2 foa_text--text">Password Requirements</h6>
    <div class="text-caption foa_text--text">
      <v-icon v-if="pwLength" color="green" class="mr-1" small>mdi-checkbox-marked-circle</v-icon>
      <v-icon v-else color="red" class="mr-1" small>mdi-cancel</v-icon>
      At least 12 characters
    </div>
    <div class="text-caption foa_text--text">
      <v-icon v-if="containsLowerCase || containsUpperCase" color="green" class="mr-1" small>
        mdi-checkbox-marked-circle
      </v-icon>
      <v-icon v-else color="red" class="mr-1" small>mdi-cancel</v-icon>
      At least 1 letter
    </div>
    <div class="text-caption foa_text--text">
      <v-icon v-if="containsLowerCase && containsUpperCase" color="green" class="mr-1" small>
        mdi-checkbox-marked-circle
      </v-icon>
      <v-icon v-else color="red" class="mr-1" small>mdi-cancel</v-icon>
      At least 1 uppercase and lowercase letter
    </div>
    <div class="text-caption foa_text--text">
      <v-icon v-if="containsNumber" color="green" class="mr-1" small>mdi-checkbox-marked-circle</v-icon>
      <v-icon v-else color="red" class="mr-1" small>mdi-cancel</v-icon>
      At least 1 number
    </div>
    <div class="text-caption foa_text--text">
      <v-icon v-if="containsSpecial" color="green" class="mr-1" small>mdi-checkbox-marked-circle</v-icon>
      <v-icon v-else color="red" class="mr-1" small>mdi-cancel</v-icon>
      At least 1 special character !@#$%^&amp;*()-+?
    </div>
  </v-sheet>
</template>

<script>
export default {
  props: {
    pwLength: {
      default: false,
      type: Boolean,
    },
    containsLowerCase: {
      default: false,
      type: Boolean,
    },
    containsUpperCase: {
      default: false,
      type: Boolean,
    },
    containsNumber: {
      default: false,
      type: Boolean,
    },
    containsSpecial: {
      default: false,
      type: Boolean,
    },
  },
  data: () => ({
    icons: {},
  }),
  methods: {},
};
</script>
