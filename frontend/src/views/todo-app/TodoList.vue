<template>
  <div class="todolist">
    <div v-if="$vuetify.breakpoint.smAndDown" class="d-flex justify-end">
      <v-btn small color="foa_button_dark" class="foa_button_text--text" to="/todo/view">Go Back</v-btn>
    </div>
    <div class="d-flex align-center text-h5 foa_text_header--text">
      {{ list.description }}:&nbsp;
      <v-fade-transition leave-absolute>
        <span :key="`tasks-${todos.length}`">{{ todos.length }} Tasks</span>
      </v-fade-transition>
      <v-dialog v-model="addEditDialogState" max-width="500px">
        <template #activator="{ on, attrs }">
          <v-btn large icon class="ml-1" :color="btnColor" v-bind="attrs" v-on="on">
            <v-icon>mdi-plus-box</v-icon>
          </v-btn>
        </template>
        <v-card>
          <v-card-title class="text-h5 foa_text_header--text justify-center">
            {{ formTitle }}
          </v-card-title>
          <v-card-text>
            <v-form ref="form" v-model="valid">
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.description"
                    color="foa_button"
                    label="Description"
                    :rules="required"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-menu
                    ref="dueMenu"
                    :close-on-content-click="false"
                    :return-value.sync="editedItem.dueDate"
                    transition="scale-transition"
                    offset-y
                  >
                    <template #activator="{ on, attrs }">
                      <v-text-field
                        v-model="editedItem.dueDate"
                        label="Due Date"
                        prepend-icon="mdi-calendar"
                        readonly
                        v-bind="attrs"
                        color="foa_button"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="editedItem.dueDate" no-title scrollable color="foa_button">
                      <v-spacer></v-spacer>
                      <v-btn text color="foa_button" @click="dueMenu = false">Cancel</v-btn>
                      <v-btn text color="foa_button" @click="$refs.dueMenu.save(editedItem.dueDate)">Ok</v-btn>
                    </v-date-picker>
                  </v-menu>
                </v-col>
                <v-col cols="12" class="pt-0">
                  <v-textarea v-model="editedItem.notes" rows="2" color="foa_button" label="Notes"></v-textarea>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="foa_button" text @click="closeAddEdit">Cancel</v-btn>
            <v-btn color="foa_button" text :disabled="!valid" @click="submitAddEdit">{{ formAction }}</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-spacer></v-spacer>
      <v-btn v-if="$vuetify.breakpoint.mdAndUp" color="foa_button_dark" class="foa_button_text--text" to="/todo/view"
        >Go Back</v-btn
      >
    </div>
    <v-divider class="mt-4"></v-divider>
    <v-row class="my-1" align="center">
      <strong class="mx-4 foa_text--text"> Remaining: {{ remainingTodos }} </strong>
      <v-divider vertical></v-divider>
      <strong class="mx-4 foa_button--text"> Completed: {{ completedTodos }} </strong>
      <v-spacer></v-spacer>
      <v-progress-circular :value="progress" class="mr-2" color="foa_button"></v-progress-circular>
    </v-row>
    <v-divider class="mb-4"></v-divider>
    <v-card v-if="todos.length > 0">
      <v-list class="py-0">
        <v-slide-y-transition group>
          <template v-for="(todo, i) in todos">
            <v-divider v-if="i !== 0" :key="`${i}-divider`"></v-divider>
            <v-list-item :key="`${i}-${todo.description}`">
              <v-list-item-action>
                <v-checkbox
                  v-model="todo.completed"
                  :color="(todo.completed && 'grey') || 'foa_button'"
                  :disabled="todo.completed"
                  @change="markComplete(todo)"
                >
                  <template #label>
                    <div
                      :class="(todo.completed && 'grey--text text-decoration-line-through') || 'foa_button--text'"
                      class="ml-4"
                      v-text="todo.description"
                    ></div>
                    <div
                      v-if="!todo.completed && todo.dueDateObj"
                      class="ml-4 text-caption"
                      :class="{ 'red--text': todo.dueDateObj < new Date() }"
                    >
                      Due {{ todo.dueDateObj.toLocaleDateString('en-US') }}
                    </div>
                  </template>
                </v-checkbox>
              </v-list-item-action>
              <v-scroll-x-transition>
                <v-icon v-if="todo.completed" color="success">mdi-check</v-icon>
              </v-scroll-x-transition>
              <v-scroll-x-transition>
                <div v-if="todo.completed" class="text-caption ml-2">
                  Completed on {{ new Date(todo.completedDateTime).toLocaleDateString('en-US') }}
                </div>
              </v-scroll-x-transition>
              <v-spacer></v-spacer>
              <v-icon
                v-if="(adult || todo.addedBy.id === user.id) && !todo.completed"
                class="mr-2"
                @click="editItem(todo)"
              >
                mdi-pencil
              </v-icon>
              <v-icon v-if="admin || todo.addedBy.id === user.id" @click="deleteItem(todo)">mdi-delete</v-icon>
            </v-list-item>
          </template>
        </v-slide-y-transition>
      </v-list>
    </v-card>
    <v-dialog v-model="deleteDialogState" max-width="500px">
      <v-card>
        <v-card-title class="justify-center foa_text_header--text">
          Are you sure you want to delete this todo?
        </v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text @click="closeDelete">Cancel</v-btn>
          <v-btn color="red" text @click="confirmDeleteTodo">Delete</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import { isAdult, isAdmin } from '../../util/RoleUtil';

export default {
  name: 'TodoList',
  data: () => ({
    addEditDialogState: false,
    deleteDialogState: false,
    todos: [],
    list: {},
    defaultItem: {},
    editedItem: {},
    editedIndex: -1,
    listId: null,
    adult: false,
    admin: false,
    valid: false,
    required: [(v) => !!v || 'This field is required'],
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies', getFamily: 'getFamily', user: 'getUser' }),
    completedTodos() {
      return this.todos.filter((todo) => todo.completed).length;
    },
    remainingTodos() {
      return this.todos.length - this.completedTodos;
    },
    progress() {
      return (this.completedTodos / this.todos.length) * 100;
    },
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
    formTitle() {
      return this.editedIndex === -1 ? 'New Todo' : 'Edit Todo';
    },
    formAction() {
      return this.editedIndex === -1 ? 'Add' : 'Save';
    },
  },
  watch: {
    addEditDialogState(val) {
      if (!val) this.closeAddEdit();
    },
    deleteDialogState(val) {
      if (!val) this.closeDelete();
    },
  },
  created() {
    if (this.$route.query.id) {
      this.listId = this.$route.query.id;
      this.fetchList();
    } else {
      this.$router.push('/todo/view');
    }
  },
  methods: {
    ...mapActions(['showSnackbar']),
    closeAddEdit() {
      this.addEditDialogState = false;
      this.$refs.form.reset();
      this.$nextTick(() => {
        this.editedItem = { ...this.defaultItem };
        this.editedIndex = -1;
      });
    },
    closeDelete() {
      this.deleteDialogState = false;
      if (this.$refs.form) {
        this.$refs.form.reset();
      }
      this.$nextTick(() => {
        this.editedItem = { ...this.defaultItem };
        this.editedIndex = -1;
      });
    },
    submitAddEdit() {
      if (this.editedIndex === -1) {
        this.addTodo();
      } else {
        this.updateTodo();
      }
    },
    editItem(item) {
      this.editedIndex = this.todos.indexOf(item);
      Object.assign(this.editedItem, item);
      this.addEditDialogState = true;
    },
    deleteItem(item) {
      this.editedIndex = this.todos.indexOf(item);
      this.editedItem = { ...item };
      this.deleteDialogState = true;
    },
    async fetchList() {
      try {
        const res = await api.getTodoList(this.listId);
        if (res.status === 200) {
          this.list = res.data;
          this.todos = res.data.tasks;
          this.todos = this.todos.map((t) => {
            let dueDate = null;
            if (t.dueDate) {
              const dateParts = t.dueDate.split('-');
              dueDate = new Date();
              dueDate.setFullYear(dateParts[0]);
              dueDate.setMonth(dateParts[1] - 1);
              dueDate.setDate(dateParts[2]);
            }
            return { ...t, dueDateObj: dueDate };
          });
          this.todos.sort((a, b) => a.completed - b.completed || a.dueDateObj - b.dueDateObj);
          this.defaultItem.listId = res.data.id;
          this.editedItem = { ...this.defaultItem };
          this.adult = isAdult(this.getFamily(this.list.familyId).memberData.role);
          this.admin = isAdmin(this.getFamily(this.list.familyId).memberData.role);
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching your todo list',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your todo list',
          timeout: 3000,
        });
      }
    },
    async addTodo() {
      const request = this.editedItem;
      try {
        const res = await api.addTask(request);
        if (res.status === 201) {
          this.fetchList();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully added a todo!',
            timeout: 3000,
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem adding this todo, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem adding this todo, please try again.',
          timeout: 3000,
        });
      }
    },
    async updateTodo() {
      const request = this.editedItem;
      try {
        const res = await api.updateTask(request);
        if (res.status === 200) {
          this.fetchList();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: 'Todo updated successfully!',
            timeout: 3000,
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem updating this todo, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem updating this todo, please try again.',
          timeout: 3000,
        });
      }
    },
    async confirmDeleteTodo() {
      try {
        const res = await api.deleteTask(this.editedItem.id);
        if (res.status === 200) {
          this.fetchList();
          this.closeDelete();
          this.showSnackbar({
            type: 'success',
            message: 'Todo has been deleted successfully!',
            timeout: 3000,
          });
        } else {
          this.closeDelete();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem deleting this todo, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeDelete();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem deleting this todo, please try again.',
          timeout: 3000,
        });
      }
    },
    async markComplete(todo) {
      try {
        const res = await api.updateTask(todo);
        if (res.status === 200) {
          this.fetchList();
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
  },
};
</script>
