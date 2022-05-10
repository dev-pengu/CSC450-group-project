<template>
  <div class="managemodal">
    <v-dialog v-model="dialog" transition="dialog-bottom-transition" max-width="600" @click:outside="closeDialog">
      <template #activator="{ on, attrs }">
        <div class="d-block foa_link--text text-decoration-underline" v-bind="attrs" v-on="on">Manage Members</div>
      </template>
      <v-card>
        <v-card-title class="foa_text_header--text pt-4 pb-4">
          Manage Members
          <v-spacer></v-spacer>
          <v-btn class="pr-0" icon color="red" @click="closeDialog"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <v-select
            v-model="userIds"
            color="foa_button"
            prepend-icon="mdi-account-multiple"
            :items="family.members.filter((member) => member.user.id !== user.id)"
            :item-text="getFullName"
            item-value="user.id"
            item-color="foa_button"
            label="Members"
            multiple
          >
            <template #selection="{ item, index }">
              <v-chip v-if="index <= 4" small>
                <span>{{ getFullName(item) }}</span>
              </v-chip>
              <span v-if="index === 4" class="grey--text text-caption"> (+{{ userIds.length - 1 }} others)</span>
            </template>
          </v-select>
          <v-alert v-if="error" class="mb-0" :type="errorType" text>{{ errorMsg }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text :loading="loading" :disabled="loading" @click="closeDialog">Cancel</v-btn>
          <v-btn color="red" text :loading="loading" :disabled="!userIds.length || loading" @click="kickMembers">
            Kick Member(s)
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../api';

export default {
  name: 'ManageMembersModal',
  props: {
    family: {
      default: () => ({}),
      type: Object,
    },
  },
  data: () => ({
    dialog: false,
    userIds: [],
    loading: false,
    errorType: 'error',
    error: false,
    errorMsg: '',
  }),
  computed: {
    ...mapGetters({ user: 'getUser' }),
  },
  methods: {
    ...mapActions(['fetchFamilies', 'showSnackbar']),
    closeDialog() {
      this.dialog = false;
      this.userIds = [];
      this.error = false;
    },
    getFullName(member) {
      return `${member.user.firstName} ${member.user.lastName}`;
    },
    async kickMembers() {
      const request = {
        familyId: this.family.id,
        userIds: this.userIds,
      };
      try {
        this.loading = true;
        this.error = false;
        const res = await api.removeMembers(request);
        if (res.status === 200) {
          this.fetchFamilies();
          this.closeDialog();
          this.showSnackbar({
            type: 'success',
            message: 'You have successfully removed member(s) from your family!',
            timeout: 3000,
          });
        } else if (res.data.errorCode === 1008) {
          this.errorMsg = 'You are not authorized to perform this action.';
          this.errorType = 'warning';
          this.error = true;
        } else {
          this.errorMsg = 'There was a problem removing members from your family, please try again later.';
          this.errorType = 'error';
          this.error = true;
        }
      } catch (err) {
        this.errorMsg = 'There was a problem removing members from your family, please try again later.';
        this.errorType = 'error';
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
