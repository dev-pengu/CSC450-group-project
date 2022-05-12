<template>
  <div class="colorpicker">
    <v-dialog v-model="dialogState" width="300">
      <template #activator="{ on, attrs }">
        <div class="d-flex align-center">
          <v-sheet
            rounded
            height="30"
            width="85"
            color="grey lighten-1"
            class="d-inline-flex align-center justify-center"
          >
            <v-sheet rounded height="80%" width="90%" class="py-auto" :color="color" v-bind="attrs" v-on="on"></v-sheet>
          </v-sheet>
          <div class="d-inline-flex pl-3 foa_text--text">{{ label }}</div>
        </div>
      </template>
      <v-color-picker v-model="color" hide-inputs hide-mode-switch></v-color-picker>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'ColorPicker',
  props: {
    label: {
      default: 'Color Picker',
      type: String,
    },
    initialColor: {
      default: '#ff0000',
      type: String,
    },
  },
  data: (instance) => ({
    dialogState: false,
    color: instance.initialColor,
  }),
};
</script>
