<template>
  <div class="toDoDialog">
    <v-dialog v-model="dialog" max-width="500px">
      <template #activator="{ on, attrs }">
        <v-btn icon :color="btnColor" v-bind="attrs" v-on="on"><v-icon>mdi-plus-box</v-icon></v-btn>
      </template>
      <v-card>
        <v-card-title class="pt-0 justify-center foa_text_header--text pt-4 pb-4">
          {{ type === 'update' ? 'Update To-Do' : 'Add a To-Do' }}
          <v-spacer></v-spacer>
          <v-btn class="pr-0" color="red" icon @click="closeDialog"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-row>
              <v-col cols="12" sm="6" class="pb-0">
                <v-select
                  v-model="internalTodo.familyId"
                  :items="families"
                  outlined
                  color="foa_button"
                  item-color="foa_button"
                  :rules="[rules.required]"
                  label="Family"
                  item-text="name"
                  item-value="id"
                  @change="fetchLists"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6" class="pb-0">
                <v-select
                  v-model="internalTodo.listId"
                  :items="lists"
                  outlined
                  color="foa_button"
                  item-color="foa_button"
                  :rules="[rules.required]"
                  label="Todo List"
                  item-text="description"
                  item-value="id"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6" class="py-0">
                <v-text-field
                  v-model="internalTodo.description"
                  color="foa_button"
                  label="Description"
                  :rules="[rules.required, rules.max]"
                  counter="50"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" class="py-0">
                <v-dialog
                  ref="dateDialog"
                  v-model="dateModal"
                  :return-value.sync="internalTodo.dueDate"
                  persistent
                  width="290px"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="internalTodo.dueDate"
                      label="Due Date"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      color="foa_button"
                      v-on="on"
                    ></v-text-field>
                  </template>
                  <v-date-picker v-model="internalTodo.dueDate" color="foa_button" scrollable>
                    <v-spacer></v-spacer>
                    <v-btn text color="foa_button" @click="dateModal = false"> Cancel </v-btn>
                    <v-btn text color="foa_button" @click="$refs.dateDialog.save(internalTodo.dueDate)"> OK </v-btn>
                  </v-date-picker>
                </v-dialog>
              </v-col>
              <v-col cols="12" class="pt-0">
                <v-textarea v-model="internalTodo.notes" rows="2" color="foa_button" label="Notes"></v-textarea>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text :loading="loading" :disabled="loading" @click="closeDialog">Cancel</v-btn>
          <v-btn color="foa_button" text :loading="loading" :disabled="!valid || loading" @click="addTodo">Add</v-btn>
        </v-card-actions>
      </v-card></v-dialog
    >
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';

export default {
  name: 'ToDoDialog',
  props: {
    todo: {
      default: () => ({}),
      type: Object,
    },
    type: {
      default: 'create',
      type: String,
      // eslint-disable-next-line
      validator: function (value) {
        return ['create', 'update', 'view'].includes(value);
      },
    },
    successCallback: {
      default: () => {},
      type: Function,
    },
    failureCallback: {
      default: () => {},
      type: Function,
    },
  },
  data: (instance) => ({
    loading: false,
    internalTodo: { ...instance.todo },
    rules: {
      required: (v) => !!v || 'This field is required',
      max: (v) => (v && v.length <= 50) || 'Max 50 characters',
    },
    valid: false,
    dialog: false,
    lists: [],
    dateModal: false,
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  methods: {
    ...mapActions(['showSnackbar']),
    closeDialog() {
      this.dialog = false;
      this.$refs.form.reset();
      this.$nextTick(() => {
        this.internalTodo = {};
      });
    },
    async addTodo() {
      const request = this.internalTodo;
      try {
        this.loading = true;
        const res = await api.addTask(request);
        if (res.status === 201) {
          this.closeDialog();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully added a todo!',
            timeout: 3000,
          });
          this.successCallback();
        } else {
          this.closeDialog();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem adding this todo, please try again.',
            timeout: 3000,
          });
          this.failureCallback();
        }
      } catch (err) {
        this.closeDialog();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem adding this todo, please try again.',
          timeout: 3000,
        });
        this.failureCallback();
      } finally {
        this.loading = false;
      }
    },
    async fetchLists() {
      try {
        const res = await api.getToDoLists(this.internalTodo.familyId);
        if (res.status === 200) {
          this.lists = res.data;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching todo lists.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching todo lists.',
          timeout: 3000,
        });
      }
    },
  },
};
</script>
