<template>
  <div class="todopreview">
    <v-toolbar class="mb-3">
      <span class="text-h5 foa_text_header--text">My To-Do List</span>
      <ToDoDialog type="create" :success-callback="fetchTodos"></ToDoDialog>
      <v-spacer></v-spacer>
      <v-btn icon :disabled="loading" :loading="loading" @click="fetchTodos"><v-icon>mdi-cached</v-icon> </v-btn>
      <v-btn icon :disabled="loading" to="/todo/view"><v-icon>mdi-share</v-icon></v-btn>
    </v-toolbar>
    <div v-if="!todos.length" class="text-caption text-center">
      You have no upcoming todos!&nbsp;<router-link to="/todo/view" class="foa_nav_link--text">Click here</router-link
      >&nbsp;to see all todos.
    </div>
    <v-virtual-scroll v-else :items="todos" height="500" :item-height="44">
      <template #default="{ item }">
        <div class="d-flex align-center justify-space-between">
          <v-checkbox
            v-model="item.completed"
            class="mt-0"
            color="foa_button"
            :label="item.description"
            hide-details
            :disabled="loading"
            @change="markComplete(item)"
          >
            <template #prepend>
              <v-sheet height="24" width="8" :color="item.color"></v-sheet>
            </template>
          </v-checkbox>
          <span v-if="item.dueDate" class="text-caption">Due {{ item.dueDate.substring(5, 10) }}</span>
        </div>
      </template>
    </v-virtual-scroll>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import ToDoDialog from './ToDoDialog.vue';

export default {
  name: 'TodoPreview',
  components: {
    ToDoDialog,
  },
  props: {
    end: {
      default: '2000-01-01',
      type: String,
    },
  },
  data: () => ({
    todos: [],
    searchFilters: { FAMILY: [], LIST: [] },
    loading: false,
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  mounted() {
    this.fetchTodos();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async fetchTodos() {
      const request = {
        end: `${this.end} 23:59`,
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
          this.todos = temp.sort((a, b) => {
            if (a.dueDate && b.dueDate) {
              return new Date(a.dueDate) - new Date(b.dueDate);
            }
            if (!a.dueDate && !b.dueDate) {
              return new Date(a.createdDatetime) - new Date(b.createdDatetime);
            }
            if (!a.dueDate) {
              return 1;
            }
            return -1;
          });
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
  },
};
</script>
