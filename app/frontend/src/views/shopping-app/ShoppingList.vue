<template>
  <div class="shoppinglist">
    <v-data-table
      :headers="headers"
      :items="items"
      sort-by="description"
      :footer-props="{
        showFirstLastPage: true,
        firstIcon: 'mdi-chevron-left',
        lastIcon: 'mdi-chevron-right',
        prevIcon: 'mdi-minus',
        nextIcon: 'mdi-plus',
      }"
      no-data-text="This shopping list doesn't have any items. Add one now!"
      class="elevation-1"
    >
      <template #top>
        <v-toolbar flat color="foa_button">
          <v-toolbar-title class="foa_button_text--text">{{ list.description }}</v-toolbar-title>
          <span v-if="list.default" class="text-caption foa_button_text--text ml-2">Default</span>
          <v-spacer></v-spacer>
          <v-dialog v-model="addEditDialogState" max-width="500px">
            <template #activator="{ on, attrs }">
              <v-btn color="foa_button_dark" class="foa_button_text--text" v-bind="attrs" v-on="on"> New Item </v-btn>
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
                        :rules="descriptionRules"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="editedItem.amount"
                        color="foa_button"
                        label="Amount"
                        :rules="amountRules"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="editedItem.unit"
                        color="foa_button"
                        label="Unit"
                        :rules="unitRules"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field v-model="editedItem.notes" color="foa_button" label="Notes"></v-text-field>
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
          <v-dialog v-model="deleteDialogState" max-width="500px">
            <v-card>
              <v-card-title class="justify-center foa_text_header--text">
                Are you sure you want to delete this item?
              </v-card-title>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="foa_button" text @click="closeDelete">Cancel</v-btn>
                <v-btn color="red" text @click="confirmDeleteItem">Delete</v-btn>
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template #item.actions="{ item }">
        <v-icon small class="mr-2" @click="editItem(item)">mdi-pencil</v-icon>
        <v-icon small @click="deleteItem(item)">mdi-delete</v-icon>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../../api';

export default {
  name: 'ShoppingList',
  data: () => ({
    addEditDialogState: false,
    deleteDialogState: false,
    headers: [
      { text: 'Description', value: 'description', align: 'start' },
      { text: 'Amount', value: 'amount' },
      { text: 'Unit', value: 'unit', sortable: false },
      { text: 'Notes', value: 'notes', sortable: false },
      { text: 'Added by', value: 'addedBy.firstName' },
      { text: 'Actions', value: 'actions', sortable: false },
    ],
    list: {},
    items: [],
    editedIndex: -1,
    editedItem: {},
    defaultItem: {},
    listId: null,
    valid: false,
    descriptionRules: [(v) => !!v || 'Description is required'],
    amountRules: [
      (v) => !!v || 'Amount is required',
      (v) => Number.isInteger(Number(v)) || 'Amount must be a whole number',
    ],
    unitRules: [(v) => !!v || 'Description is required'],
  }),
  computed: {
    formTitle() {
      return this.editedIndex === -1 ? 'New Item' : 'Edit Item';
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
  async created() {
    if (this.$route.query.id) {
      this.listId = this.$route.query.id;
      this.fetchList();
    } else {
      this.$router.push('/shopping/view');
    }
  },
  methods: {
    ...mapActions(['showSnackbar']),
    editItem(item) {
      this.editedIndex = this.items.indexOf(item);
      Object.assign(this.editedItem, item);
      this.addEditDialogState = true;
    },
    deleteItem(item) {
      this.editedIndex = this.items.indexOf(item);
      this.editedItem = { ...item };
      this.deleteDialogState = true;
    },
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
        this.addItem();
      } else {
        this.updateItem();
      }
    },
    async fetchList() {
      try {
        const res = await api.getShoppingList(this.listId);
        if (res.status === 200) {
          this.list = res.data;
          this.items = res.data.items;
          this.defaultItem.listId = res.data.id;
          this.editedItem = { ...this.defaultItem };
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching your shopping list',
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your shopping list',
        });
      }
    },
    async addItem() {
      try {
        const request = this.editedItem;
        const res = await api.createShoppingListItem(request);
        if (res.status === 201) {
          this.fetchList();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully added an item!',
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem adding this item, please try again.',
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem adding this item, please try again.',
        });
      }
    },
    async updateItem() {
      try {
        const request = this.editedItem;
        const res = await api.updateShoppingListItem(request);
        if (res.status === 200) {
          this.fetchList();
          this.closeAddEdit();
          this.showSnackbar({
            type: 'success',
            message: 'Item updated successfully!',
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem updating this item, please try again.',
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem updating this item, please try again.',
        });
      }
    },
    async confirmDeleteItem() {
      try {
        const res = await api.deleteShoppingListItem(this.editedItem.id);
        if (res.status === 200) {
          this.fetchList();
          this.closeDelete();
          this.showSnackbar({
            type: 'success',
            message: 'Item has been deleted successfully!',
          });
        } else {
          this.closeDelete();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem deleting this item, please try again.',
          });
        }
      } catch (err) {
        this.closeDelete();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem deleting this item, please try again.',
        });
      }
    },
  },
};
</script>
