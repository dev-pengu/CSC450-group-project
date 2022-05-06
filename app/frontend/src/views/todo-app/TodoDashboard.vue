<template>
  <div class="tododashboard">
    <v-data-iterator
      :items="todoLists"
      :items-per-page.sync="itemsPerPage"
      :page.sync="page"
      :search="search"
      :sort-by="sortBy"
      :sort-desc="sortDesc"
      no-data-text="You don't have any todo lists. Create one for your family!"
      hide-default-footer
    >
      <template #header>
        <div else class="foa_text_header--text text-h4 mb-3 d-inline-flex align-center">
          <span v-if="$route.query.name">{{ $route.query.name }} Todo Lists</span>
          <span v-else>Todo Lists</span>
          <v-dialog v-model="addEditDialogState" max-width="500px">
            <template #activator="{ on, attrs }">
              <v-btn x-large icon :color="btnColor" v-bind="attrs" v-on="on">
                <v-icon>mdi-plus-box</v-icon>
              </v-btn>
            </template>
            <v-card>
              <v-card-title class="text-h5 justify-center foa_text_header--text">
                {{ formTitle }}
              </v-card-title>
              <v-card-text>
                <v-form ref="form" v-model="valid">
                  <v-row>
                    <v-col cols="12">
                      <v-select
                        v-model="editedItem.familyId"
                        :disabled="editedIndex > -1"
                        :items="families"
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
                        v-model="editedItem.description"
                        :rules="descriptionRules"
                        color="foa_button"
                        outlined
                        label="Description"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="foa_button" text @click="closeAddEdit">Cancel</v-btn>
                <v-btn color="foa_button" :disabled="!valid" text @click="submitAddEdit">{{ formAction }}</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </div>
        <v-toolbar class="mb-4" color="foa_nav_bg">
          <v-text-field
            v-model="search"
            clearable
            flat
            solo
            hide-details
            prepend-inner-icon="mdi-magnify"
            label="Search"
            color="foa_button"
          ></v-text-field>
          <template v-if="$vuetify.breakpoint.mdAndUp">
            <v-spacer></v-spacer>
            <v-select
              v-model="sortBy"
              flat
              hide-details
              :items="keys"
              item-text="friendlyName"
              item-value="name"
              solo
              prepend-inner-icon="mdi-magnify"
              label="Sort by"
              color="foa_button"
              item-color="foa_button"
            ></v-select>
            <v-spacer></v-spacer>
            <v-btn-toggle v-model="sortDesc" mandatory light>
              <v-btn large depressed color="foa_button" :value="false">
                <v-icon>mdi-arrow-up</v-icon>
              </v-btn>
              <v-btn large depressed color="foa_button" :value="true">
                <v-icon>mdi-arrow-down</v-icon>
              </v-btn>
            </v-btn-toggle>
          </template>
        </v-toolbar>
      </template>
      <template #default="props">
        <v-row>
          <v-col v-for="(list, i) in props.items" :key="i" cols="12" sm="6" md="4" lg="3">
            <v-hover v-slot="{ hover }">
              <v-card :elevation="hover ? 8 : 2" :to="{ path: '/todo/list', query: { id: list.id } }">
                <v-card-title
                  class="text-h5 font-weight-bold foa_text--text pb-0 text-break"
                  :class="{ 'foa_button--text': sortBy === 'description' }"
                >
                  {{ list.description }}
                </v-card-title>
                <v-card-text
                  class="text-h6 foa_text--text pt-0 d-flex align-center"
                  :class="{ 'foa_button--text': sortBy === 'familyId' }"
                >
                  {{ getFamily(list.familyId).name }}
                  <v-icon class="ml-1" :color="`#${list.color}`" small>mdi-circle</v-icon>
                </v-card-text>
                <v-card-text v-if="list.default" class="text-caption pt-0 pb-2">Default</v-card-text>
                <v-divider></v-divider>
                <v-list dense>
                  <v-list-item>
                    <v-list-item-content :class="{ 'foa_button--text': sortBy === 'createdBy.firstName' }">
                      Created by:
                    </v-list-item-content>
                    <v-list-item-content
                      class="align-end"
                      :class="{ 'foa_button--text': sortBy === 'createdBy.firstName' }"
                    >
                      {{ `${list.createdBy.firstName} ${list.createdBy.lastName}` }}
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-content :class="{ 'foa_button--text': sortBy === 'created' }">
                      Created:
                    </v-list-item-content>
                    <v-list-item-content class="align-end" :class="{ 'foa_button--text': sortBy === 'created' }">
                      {{ new Date(list.created).toLocaleDateString('en-US') }}
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-content :class="{ 'foa_button--text': sortBy === 'todos' }">
                      Todos:
                    </v-list-item-content>
                    <v-list-item-content class="align-end" :class="{ 'foa_button--text': sortBy === 'todos' }">
                      {{ list.tasks.length }}
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn v-if="canDelete(list)" icon prevent @click.prevent="deleteItem(list)">
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                  <v-btn v-if="canEdit(list)" icon prevent @click.prevent="editItem(list)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-hover>
          </v-col>
        </v-row>
      </template>
      <template #footer>
        <v-row class="mt-4 mx-1" align="center" justify="center">
          <span class="foa_text--text text-caption">Items per page</span>
          <v-menu offset-y>
            <template #activator="{ on, attrs }">
              <v-btn dark text color="foa_button" class="ml-2" v-bind="attrs" v-on="on">
                {{ itemsPerPage }}
                <v-icon>mdi-chevron-down</v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item
                v-for="(number, index) in itemsPerPageArray"
                :key="index"
                @click="updateItemsPerPage(number)"
              >
                <v-list-item-title>{{ number }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
          <v-spacer></v-spacer>
          <span class="mr-4 foa_text--text text-caption">Page {{ page }} of {{ numberOfPages }}</span>
          <v-btn fab dark x-small color="foa_button" class="mr-1" @click="formerPage">
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>
          <v-btn fab dark x-small color="foa_button" class="ml-1" @click="nextPage">
            <v-icon>mdi-chevron-right</v-icon>
          </v-btn>
        </v-row>
      </template>
    </v-data-iterator>
    <v-dialog v-model="deleteDialogState" persistent max-width="550px">
      <v-card>
        <v-card-title class="justify-center foa_text_header--text">
          Are you sure you want to delete this todo list?
        </v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text @click="closeDelete">Cancel</v-btn>
          <v-btn color="red" text @click="confirmDeleteList">Delete</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import { isAdmin } from '../../util/RoleUtil';

export default {
  name: 'TodoDashboard',
  data: () => ({
    keys: [
      { name: 'description', friendlyName: 'Description' },
      { name: 'familyId', friendlyName: 'Family' },
      { name: 'createdBy.firstName', friendlyName: 'Created by' },
      { name: 'created', friendlyName: 'Created date' },
      { name: 'tasks', friendlyName: 'Todos' },
    ],
    todoLists: [],
    searchFilters: { FAMILY: [], LIST: [] },
    itemsPerPageArray: [4, 8, 12],
    search: '',
    itemsPerPage: 8,
    page: 1,
    sortBy: 'created',
    sortDesc: true,
    editedItem: {},
    editedIndex: -1,
    deleteDialogState: false,
    addEditDialogState: false,
    familyRules: [(v) => !!v || 'Family is required'],
    descriptionRules: [(v) => !!v || 'Description is required'],
    valid: false,
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies', getFamily: 'getFamily', user: 'getUser' }),
    numberOfPages() {
      return Math.ceil(this.todoLists.length / this.itemsPerPage);
    },
    formTitle() {
      return this.editedIndex === -1 ? 'New List' : 'Edit List';
    },
    formAction() {
      return this.editedIndex === -1 ? 'Create' : 'Save';
    },
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  watch: {
    $route() {
      if (this.$route.query.familyId && this.$route.query.name) {
        this.searchFilters.FAMILY.push({ id: +this.$route.query.familyId, display: this.$route.query.name });
      }
      this.fetchTodoLists();
    },
    addEditDialogState(val) {
      if (!val) this.closeAddEdit();
    },
    deleteDialogState(val) {
      if (!val) this.closeDelete();
    },
  },
  async created() {
    if (this.$route.query.familyId && this.$route.query.name) {
      this.searchFilters.FAMILY.push({ id: +this.$route.query.familyId, display: this.$route.query.name });
    }
    this.fetchTodoLists();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    canDelete(list) {
      return (
        !list.default && (list.createdBy.id === this.user.id || isAdmin(this.getFamily(list.familyId).memberData.role))
      );
    },
    canEdit(list) {
      return list.createdBy.id === this.user.id || isAdmin(this.getFamily(list.familyId).memberData.role);
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
    closeDelete() {
      this.deleteDialogState = false;
      if (this.$refs.form) {
        this.$refs.form.reset();
      }
      this.$nextTick(() => {
        this.editedItem = {};
      });
    },
    closeAddEdit() {
      this.addEditDialogState = false;
      this.$refs.form.reset();
      this.$nextTick(() => {
        this.editedItem = {};
        this.editedIndex = -1;
      });
    },
    deleteItem(item) {
      this.editedItem = { ...item };
      this.deleteDialogState = true;
    },
    editItem(item) {
      this.editedIndex = this.todoLists.indexOf(item);
      this.editedItem = { ...item };
      this.addEditDialogState = true;
    },
    submitAddEdit() {
      if (this.editedIndex === -1) {
        this.createTodoList();
      } else {
        this.updateTodoList();
      }
    },
    async fetchTodoLists() {
      const request = {
        filters: this.searchFilters,
      };
      try {
        const res = await api.searchTodoLists(request);
        if (res.status === 200) {
          this.todoLists = res.data.lists;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching your todo lists.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your todo lists.',
          timeout: 3000,
        });
      } finally {
        this.searchFilters.FAMILY = [];
        this.searchFilters.LIST = [];
      }
    },
    async confirmDeleteList() {
      try {
        const res = await api.deleteTodoList(this.editedItem.id);
        if (res.status === 200) {
          this.closeDelete();
          this.fetchTodoLists();
          this.showSnackbar({
            type: 'success',
            message: 'Todo list has been deleted successfully!',
            timeout: 3000,
          });
        } else {
          this.closeDelete();
          this.showSnackbar({
            type: 'error',
            message: `There was a problem deleting the list ${this.editedItem.description}`,
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeDelete();
        this.showSnackbar({
          type: 'error',
          message: `There was a problem deleting the list ${this.editedItem.description}`,
          timeout: 3000,
        });
      }
    },
    async updateTodoList() {
      try {
        const request = this.editedItem;
        const listDescription = this.editedItem.description;
        const res = await api.updateTodoList(request);
        if (res.status === 200) {
          this.fetchTodoLists();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: `You have successfully updated ${listDescription}!`,
            timeout: 3000,
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: `There was a problem updating ${listDescription}, please try again.`,
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem updating this todo list, please try again.',
          timeout: 3000,
        });
      }
    },
    async createTodoList() {
      try {
        const request = this.editedItem;
        const res = await api.createTodoList(request);
        if (res.status === 201) {
          this.fetchTodoLists();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully created a todo list!',
            timeout: 3000,
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem creating this todo list, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem creating this todo list, please try again.',
          timeout: 3000,
        });
      }
    },
  },
};
</script>
