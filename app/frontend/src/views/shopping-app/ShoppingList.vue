<template>
  <div class="shoppinglist">
    <v-data-iterator
      :items="items"
      item-key="id"
      :loading="loading"
      no-data-text="Nothing to see here! Add an item to the list."
      hide-default-footer
      :search="search"
    >
      <template #header>
        <div v-if="$vuetify.breakpoint.smAndDown" class="d-flex justify-end">
          <v-btn small color="foa_button_dark" class="foa_button_text--text" to="/shopping/view">Go Back</v-btn>
        </div>
        <div class="foa_text_header--text text-h5 mb-3 d-flex align-center">
          <span>{{ list.description }}</span>
          <span v-if="list.default" class="text-caption foa_button_text--text ml-2">Default</span>
          <v-dialog v-model="addEditDialogState" max-width="500px">
            <template #activator="{ on, attrs }">
              <v-btn icon large :color="btnColor" v-bind="attrs" v-on="on"><v-icon>mdi-plus-box</v-icon></v-btn>
            </template>
            <v-card>
              <v-card-title class="text-h5 foa_text_header--text justify-center">
                {{ formTitle }}
              </v-card-title>
              <v-card-text>
                <v-form ref="form" v-model="valid">
                  <v-row>
                    <v-col cols="12">
                      <v-text-field
                        v-model="editedItem.description"
                        dense
                        color="foa_button"
                        label="Description"
                        :rules="descriptionRules"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="editedItem.amount"
                        dense
                        type="number"
                        color="foa_button"
                        label="Amount"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-combobox
                        v-model="editedItem.unit"
                        dense
                        hint="Pick a unit from the list or type your own"
                        persistent-hint
                        label="Unit"
                        color="foa_button"
                        :items="defaultUnits"
                      ></v-combobox>
                    </v-col>
                    <v-col cols="12">
                      <v-textarea
                        v-model="editedItem.notes"
                        dense
                        color="foa_button"
                        label="Notes"
                        rows="1"
                      ></v-textarea>
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
          <v-spacer></v-spacer>
          <v-btn
            v-if="$vuetify.breakpoint.mdAndUp"
            color="foa_button_dark"
            class="foa_button_text--text"
            to="/shopping/view"
            >Go Back</v-btn
          >
        </div>
        <v-toolbar class="mb-4" color="foa_nav_bg">
          <v-text-field
            v-model="search"
            clearable
            flat
            solo
            color="foa_button"
            append-icon="mdi-magnify"
            label="Search"
            hide-details
          ></v-text-field>
          <v-btn icon color="foa_button_dark" :disabled="loading" :loading="loading" @click="fetchList"
            ><v-icon>mdi-cached</v-icon>
          </v-btn>
        </v-toolbar>
        <div v-if="items.length > 0" class="text-caption">Click an item to edit/delete or view additional info</div>
      </template>
      <template #default="props">
        <v-list>
          <template v-for="(item, i) in props.items">
            <div :key="item.id" class="d-flex">
              <v-list-item
                class="item_vessel"
                @click.prevent="oneself(item)"
                @touchstart.capture="touchStart"
                @touchend.capture="touchEnd($event, i)"
              >
                <v-list-item-content v-if="editedItem.id !== item.id">
                  <v-list-item-title v-text="item.description"></v-list-item-title>
                  <v-list-item-subtitle v-text="`${item.amount} ${item.unit}`"></v-list-item-subtitle>
                  <v-list-item-subtitle
                    class="text-caption"
                    v-text="`Added by: ${item.addedBy.firstName} ${item.addedBy.lastName}`"
                  ></v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-content v-else>
                  <v-form>
                    <v-row>
                      <v-col cols="6">
                        <v-text-field
                          v-model="editedItem.description"
                          dense
                          color="foa_button"
                          label="Description"
                          :rules="descriptionRules"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="4" md="3">
                        <v-text-field
                          v-model="editedItem.amount"
                          dense
                          type="number"
                          color="foa_button"
                          label="Amt"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" md="3">
                        <v-combobox
                          v-model="editedItem.unit"
                          dense
                          hint="Pick a unit from the list or type your own"
                          label="Unit"
                          color="foa_button"
                          :items="defaultUnits"
                        ></v-combobox>
                      </v-col>
                    </v-row>
                    <v-row>
                      <v-col>
                        <v-textarea
                          v-model="editedItem.notes"
                          dense
                          color="foa_button"
                          label="Notes"
                          rows="1"
                        ></v-textarea>
                      </v-col>
                    </v-row>
                  </v-form>
                </v-list-item-content>
                <v-list-item-action v-if="editedItem.id === item.id">
                  <v-btn icon @click.stop.prevent="editedItem = defaultItem"><v-icon>mdi-cancel</v-icon></v-btn>
                  <div class="d-flex">
                    <v-btn icon :color="btnColor" :loading="loading" @click="updateItem(item)"
                      ><v-icon>mdi-check</v-icon></v-btn
                    >
                    <v-btn icon color="error" :loading="loading" @click="deleteItem(item)"
                      ><v-icon>mdi-delete</v-icon></v-btn
                    >
                  </div>
                </v-list-item-action>
              </v-list-item>
              <v-sheet v-if="deleteIdx === i" color="error" width="50px" class="d-flex justify-center align-center">
                <v-btn icon :loading="loading" @click="deleteItem(item)"><v-icon>mdi-delete</v-icon></v-btn>
              </v-sheet>
            </div>
            <v-divider v-if="i < items.length - 1" :key="i"></v-divider>
          </template>
        </v-list>
      </template>
    </v-data-iterator>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import { isAdult } from '../../util/RoleUtil';

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
    defaultItem: {
      id: null,
      description: '',
      unit: '',
      amount: null,
      notes: '',
    },
    listId: null,
    valid: false,
    descriptionRules: [(v) => !!v || 'Description is required'],
    defaultUnits: ['ea', 'package', 'gallon', 'liter', 'fl oz', 'mL', 'lb', 'oz', 'grams', 'kg'],
    search: '',
    loading: false,
    startX: 0,
    endX: 0,
    deleteIdx: -1,
  }),
  computed: {
    ...mapGetters({ getFamily: 'getFamily', user: 'getUser' }),
    formTitle() {
      return this.editedIndex === -1 ? 'New Item' : 'Edit Item';
    },
    formAction() {
      return this.editedIndex === -1 ? 'Add' : 'Save';
    },
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
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
          this.items = res.data.items.map((item) => ({ ...item, swipeType: 0 }));
          this.defaultItem.listId = res.data.id;
          this.editedItem = { ...this.defaultItem };
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem fetching your shopping list',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem fetching your shopping list',
          timeout: 3000,
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
            timeout: 3000,
          });
        } else {
          this.closeAddEdit();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem adding this item, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeAddEdit();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem adding this item, please try again.',
          timeout: 3000,
        });
      }
    },
    async updateItem() {
      try {
        const request = this.editedItem;
        const res = await api.updateShoppingListItem(request);
        if (res.status === 200) {
          this.fetchList();
          this.showSnackbar({
            type: 'success',
            message: 'Item updated successfully!',
            timeout: 3000,
          });
          this.editedItem = {};
        } else {
          this.editedItem = {};
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem updating this item, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.editedItem = {};
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem updating this item, please try again.',
          timeout: 3000,
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
            timeout: 3000,
          });
        } else {
          this.closeDelete();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem deleting this item, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeDelete();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem deleting this item, please try again.',
          timeout: 3000,
        });
      }
    },
    touchStart(e) {
      this.startX = e.touches[0].clientX;
    },
    touchEnd(e, idx) {
      this.endX = e.changedTouches[0].clientX;
      let item;
      if (idx >= 0) {
        item = this.items[idx];
      }
      if (isAdult(this.getFamily(this.list.familyId).memberData.role) || item.addedBy.id === this.user.id) {
        // swipe left
        if (item && this.startX - this.endX > 30) {
          this.deleteIdx = idx;
          this.items[idx].swipeType = 1;
        }
        // swipe right
        if (item && item.swipeType === 1 && this.startX - this.endX < -30) {
          this.resetSlide();
          this.items[idx].swipeType = 0;
        }
      } else {
        this.showSnackbar({
          type: 'warn',
          message: `You don't have permission to delete this item`,
          timeout: 3000,
        });
      }
      this.startX = 0;
      this.endX = 0;
    },
    checkSlide() {
      for (let i = 0; i < this.items.length; i++) {
        if (this.items[i].swipeType === 1) {
          return true;
        }
      }
      return false;
    },
    resetSlide() {
      for (let i = 0; i < this.items.length; i++) {
        this.items[i].swipeType = 0;
      }
      this.deleteIdx = -1;
    },
    oneself(item) {
      if (this.checkSlide()) {
        this.resetSlide();
      } else if (this.editedItem.id !== item.id) {
        if (isAdult(this.getFamily(this.list.familyId).memberData.role) || item.addedBy.id === this.user.id) {
          this.editedItem = { ...item };
        } else {
          this.showSnackbar({
            type: 'warn',
            message: `You don't have permission to edit this item`,
            timeout: 3000,
          });
        }
      }
    },
  },
};
</script>
