<template>
  <div class="todopreview">
    <div class="text-h5 foa_text_header--text mb-3 d-flex align-center">
      My To-Do List
      <v-btn class="ml-2" icon :color="btnColor" @click="dialog = true"><v-icon>mdi-plus-box</v-icon></v-btn>
      <v-btn icon :disabled="loading" :loading="loading" @click="fetchTodos"><v-icon>mdi-cached</v-icon> </v-btn>
    </div>
    <div v-if="!todos.length" class="text-caption">
      You have no upcoming todos!&nbsp;<router-link to="/todo/view" class="foa_nav_link--text">Click here</router-link
      >&nbsp;to see all todos.
    </div>
    <div v-for="(todo, i) in todos" v-else :key="i" class="d-flex align-center">
      <v-checkbox
        v-model="todo.completed"
        color="foa_button"
        :label="todo.description"
        hide-details
        :disabled="loading"
        @change="markComplete(todo)"
      >
        <template #prepend>
          <v-sheet height="24" width="8" :color="todo.color"></v-sheet>
        </template>
      </v-checkbox>
    </div>
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5 foa_text_header--text justify-center">Add Todo</v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-row>
              <v-col cols="12" sm="6" class="pb-0">
                <v-select
                  v-model="newTodo.familyId"
                  :items="families"
                  outlined
                  color="foa_button"
                  item-color="foa_button"
                  :rules="required"
                  label="Family"
                  item-text="name"
                  item-value="id"
                  @change="fetchLists"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6" class="pb-0">
                <v-select
                  v-model="newTodo.listId"
                  :items="lists"
                  outlined
                  color="foa_button"
                  item-color="foa_button"
                  :rules="required"
                  label="Todo List"
                  item-text="description"
                  item-value="id"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6" class="py-0">
                <v-text-field
                  v-model="newTodo.description"
                  color="foa_button"
                  label="Description"
                  :rules="required"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" class="py-0">
                <v-menu
                  ref="dueMenu"
                  :close-on-content-click="false"
                  :return-value.sync="newTodo.dueDate"
                  transition="scale-transition"
                  offset-y
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="newTodo.dueDate"
                      label="Due Date"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      color="foa_button"
                      v-on="on"
                    ></v-text-field>
                  </template>
                  <v-date-picker v-model="newTodo.dueDate" no-title scrollable color="foa_button">
                    <v-spacer></v-spacer>
                    <v-btn text color="foa_button" @click="dueMenu = false">Cancel</v-btn>
                    <v-btn text color="foa_button" @click="$refs.dueMenu.save(newTodo.dueDate)">Ok</v-btn>
                  </v-date-picker>
                </v-menu>
              </v-col>
              <v-col cols="12" class="pt-0">
                <v-textarea v-model="newTodo.notes" rows="2" color="foa_button" label="Notes"></v-textarea>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text @click="closeDialog">Cancel</v-btn>
          <v-btn color="foa_button" text :disabled="!valid" @click="addTodo">Add</v-btn>
        </v-card-actions>
      </v-card></v-dialog
    >
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';

export default {
  name: 'TodoPreview',
  props: {
    end: {
      default: '2000-01-01',
      type: String,
    },
  },
  data: () => ({
    todos: [],
    lists: [],
    searchFilters: { FAMILY: [], LIST: [] },
    loading: false,
    newTodo: {},
    required: [(v) => !!v || 'This field is required'],
    valid: false,
    dialog: false,
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  watch: {
    dialog(val) {
      if (!val) this.closeDialog();
    },
  },
  mounted() {
    this.fetchTodos();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    closeDialog() {
      this.dialog = false;
      this.$refs.form.reset();
      this.$nextTick(() => {
        this.newTodo = {};
      });
    },
    async fetchTodos() {
      const request = {
        end: this.end,
        completed: false,
        filters: this.searchFilters,
      };
      try {
        this.loading = true;
        const res = await api.searchTodoLists(request);
        if (res.status === 200) {
          const temp = [];
          res.data.lists.forEach((list) => {
            const familyColor = `#${list.color}`;
            list.tasks.forEach((todo) => {
              temp.push({ ...todo, color: familyColor });
            });
          });
          this.todos = temp;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching your todos.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your todos.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    async addTodo() {
      const request = this.newTodo;
      try {
        const res = await api.addTask(request);
        if (res.status === 201) {
          this.fetchTodos();
          this.closeDialog();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully added a todo!',
            timeout: 3000,
          });
        } else {
          this.closeDialog();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem adding this todo, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeDialog();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem adding this todo, please try again.',
          timeout: 3000,
        });
      }
    },
    async markComplete(todo) {
      const request = todo;
      try {
        const res = await api.updateTask(request);
        if (res.status === 200) {
          this.fetchTodos();
          this.showSnackbar({
            type: 'success',
            message: 'Todo completed!',
            timeout: 3000,
          });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem completing this todo, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem updating completing this todo, please try again.',
          timeout: 3000,
        });
      }
    },
    async fetchLists() {
      this.searchFilters.FAMILY.push({ id: +this.newTodo.familyId });
      const request = {
        filters: this.searchFilters,
      };
      try {
        const res = await api.searchTodoLists(request);
        if (res.status === 200) {
          this.lists = res.data.lists;
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
      } finally {
        this.searchFilters.FAMILY = [];
      }
    },
  },
};
</script>
