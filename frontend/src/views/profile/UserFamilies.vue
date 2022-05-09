<template>
  <div class="userFamilies">
    <v-row>
      <v-col cols="12" sm="5" md="4" lg="3">
        <ProfileNav />
      </v-col>
      <v-col cols="12" sm="7" md="8" lg="9">
        <v-dialog v-model="memberDialog" max-width="500px" @click:outside="closeRoleManager">
          <v-card>
            <v-card-title class="pt-0 justify-center foa_text_header--text pt-4 pb-4">
              Member Roles
              <v-spacer></v-spacer>
              <v-btn class="pr-0" icon @click="closeRoleManager"><v-icon>mdi-close</v-icon></v-btn>
            </v-card-title>
            <v-card-text>
              <v-row justify="center">
                <v-col cols="12" sm="10">
                  <v-sheet
                    v-for="member in updateFamily.members"
                    :key="member.user.id"
                    elevation="6"
                    rounded
                    class="d-flex my-3 pa-2"
                  >
                    <v-row>
                      <v-col cols="5" sm="4">{{ `${member.user.firstName} ${member.user.lastName}` }}</v-col>
                      <v-col cols="7" sm="8">
                        <v-select
                          v-if="member.user.id !== updateFamily.owner.user.id"
                          v-model="member.role"
                          :items="['CHILD', 'ADULT', 'ADMIN']"
                        ></v-select>
                        <span v-else>The owner cannot be changed here.</span></v-col
                      >
                    </v-row>
                  </v-sheet>
                  <v-row class="mt-1">
                    <v-col class="d-flex justify-end">
                      <v-spacer></v-spacer>
                      <v-btn
                        class="foa_button_text--text"
                        color="foa_button"
                        elevation="2"
                        :loading="loading"
                        :disabled="loading"
                        @click="updateMemberRoles"
                      >
                        Submit Changes
                      </v-btn>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-card-text>
            <v-card-actions> </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="transferDialog" max-width="500px" @click:outside="closeTransferDialog">
          <v-card>
            <v-card-title class="pt-0 justify-center foa_text_header--text pt-4 pb-4">
              Ownership Transfer
              <v-spacer></v-spacer>
              <v-btn class="pr-0" icon @click="closeTransferDialog"><v-icon>mdi-close</v-icon></v-btn>
            </v-card-title>
            <v-card-text>
              <v-form>
                <v-select
                  v-model="newOwner"
                  :items="updateFamily.members"
                  item-value="user.username"
                  item-text="user.firstName"
                  label="New Owner"
                ></v-select>
                <v-row justify="center">
                  <v-col cols="6" sm="4">
                    <v-btn block color="foa_button" class="foa_button_text--text" @click="updateOwner">Submit</v-btn>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>
        </v-dialog>
        <v-sheet max-width="400" color="#00000000">
          <h4 class="text-h5 foa_text_header--text mb-5">Join A Family</h4>
          <v-form ref="joinForm" v-model="joinValid">
            <v-text-field
              v-model="joinFamilyReq.inviteCode"
              outlined
              color="foa_button"
              label="Invite Code"
              :rules="codeRules"
            ></v-text-field>
            <ColorPicker ref="joinPersonalColorPicker" label="Personal Event Color" />
            <v-row>
              <v-col>
                <v-alert v-if="joinError" class="mb-2" text type="error">{{ joinErrorMsg }}</v-alert>
              </v-col>
            </v-row>
            <v-row class="mt-1">
              <v-col class="d-flex justify-end">
                <v-spacer></v-spacer>
                <v-btn
                  class="foa_button_text--text"
                  color="foa_button"
                  elevation="2"
                  :loading="loading"
                  :disabled="loading || !joinValid"
                  @click="submitJoinFamily"
                  >Join</v-btn
                >
              </v-col>
            </v-row>
          </v-form>
          <div class="d-flex align-center text-h5 foa_text_header--text my-5">
            <span>Manage Families</span>
            <v-btn class="ml-1" icon :disabled="loading" :loading="loading" @click="fetchFamilies">
              <v-icon>mdi-cached</v-icon>
            </v-btn>
          </div>
          <v-card v-for="(family, i) in families" :key="family.id" class="mb-4" outlined>
            <v-card-title class="pt-2 pr-2">
              {{ family.name }}
              <v-spacer></v-spacer>
              <v-btn
                v-if="(!showUpdateForm || updateIndex !== i) && isAdmin(family.memberData.role)"
                icon
                @click="expand(i)"
              >
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn v-else-if="showUpdateForm && updateIndex === i" icon @click="collapse">
                <v-icon>mdi-pencil-off</v-icon>
              </v-btn>
            </v-card-title>
            <v-card-text>
              <div>{{ `Owner: ${family.owner.user.firstName} ${family.owner.user.lastName}` }}</div>
              <div>{{ `Your role: ${family.memberData.role}` }}</div>
            </v-card-text>
            <v-card-text v-if="updateIndex === i && showUpdateForm">
              <v-form v-model="updateFormValid">
                <v-row justify="center">
                  <v-col cols="9">
                    <v-text-field
                      v-model="updateFamily.name"
                      outlined
                      color="foa_button"
                      label="Family Name"
                      required
                      :rules="[(v) => !!v || 'Password is required']"
                    ></v-text-field>
                    <ColorPicker
                      :ref="`familyColor-${family.id}`"
                      label="Family Event color"
                      :initial-color="`${updateFamily.color}`"
                      class="mb-4"
                    />
                    <v-select
                      v-model="updateFamily.timezone"
                      :items="timezones"
                      color="foa_button"
                      item-color="foa_button"
                      label="Timezone"
                      required
                      hide-details
                    ></v-select>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
            <v-card-actions v-if="(showUpdateForm && updateIndex !== i) || !showUpdateForm" class="pb-4">
              <v-spacer></v-spacer>
              <v-btn
                v-if="user.id === family.owner.user.id"
                class="foa_button_text--text"
                color="error"
                elevation="2"
                :loading="loading"
                :disabled="loading"
                small
                @click="openTransferDialog(i)"
              >
                Transfer Ownership
              </v-btn>
              <v-btn
                v-else
                class="foa_button_text--text"
                color="error"
                elevation="2"
                :loading="loading"
                :disabled="loading"
                small
                @click="leaveDeleteFamily(family.id, 'leave')"
              >
                Leave Family
              </v-btn>
              <v-btn
                v-if="isAdmin(family.memberData.role)"
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                :loading="loading"
                :disabled="loading"
                small
                @click="openRoleManager(i)"
              >
                Manage Roles
              </v-btn>
            </v-card-actions>
            <v-card-actions v-else-if="updateIndex === i && showUpdateForm" class="pb-4">
              <v-spacer></v-spacer>
              <v-btn
                small
                class="foa_button_text--text"
                color="error"
                elevation="2"
                :loading="loading"
                :disabled="loading"
                @click="leaveDeleteFamily(family.id, 'delete')"
                >Delete Family</v-btn
              >
              <v-btn
                small
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                :loading="loading"
                :disabled="loading || !updateFormValid"
                @click="updateFamilyDetails"
              >
                Submit
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-sheet>
      </v-col>
    </v-row>
    <v-dialog v-model="leaveDeleteDialog" max-width="600px" persistent>
      <v-card>
        <v-card-title class="justify-center foa_text_header--text">
          Are you sure you want to {{ leaveDeleteOption }} {{ familyName }}?
        </v-card-title>
        <v-card-text v-if="leaveDeleteOption === 'leave'">
          <v-form ref="leaveDeleteForm" v-model="leaveDeleteValid">
            <v-row justify="center">
              <v-col cols="7">
                <v-text-field
                  v-model="userCredentials.password"
                  color="foa_button"
                  :rules="required"
                  prepend-icon="mdi-lock"
                  :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                  :type="showPass ? 'text' : 'password'"
                  label="Password"
                  :disabled="loading"
                  @click:append="showPass = !showPass"
                />
                <v-alert v-if="loginError" class="mb-0" text type="error">{{ loginErrorMsg }}</v-alert>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="foa_button" text @click="closeLeaveDelete">Cancel</v-btn>
          <v-btn
            color="red"
            text
            :disabled="(!leaveDeleteValid && leaveDeleteOption === 'leave') || loading"
            @click="leaveDeleteOption === 'delete' ? confirmDeleteFamily() : confirmLeaveFamily()"
          >
            {{ leaveDeleteOption }}
          </v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';
import { isAdmin } from '../../util/RoleUtil';
import ProfileNav from '../../components/profile/ProfileNav.vue';
import ColorPicker from '../../components/ColorPicker.vue';

export default {
  name: 'UserFamilies',
  components: {
    ProfileNav,
    ColorPicker,
  },
  data: () => ({
    joinValid: false,
    joinError: false,
    joinErrorMsg: '',
    loading: false,
    codeRules: [(v) => !!v || 'Invite code is required'],
    joinFamilyReq: {
      inviteCode: '',
      personalEventColor: '',
    },
    updateIndex: -1,
    updateFamily: {
      id: -1,
      name: '',
      color: '#ff0000',
      timezone: 'US/Central',
      members: [],
      owner: {},
    },
    defaultExpandedFamily: {
      id: -1,
      name: '',
      color: '#ff0000',
      timezone: 'US/Central',
      members: [],
      owner: {},
    },
    timezones: [],
    updateFormValid: false,
    memberDialog: false,
    showUpdateForm: false,
    transferDialog: false,
    newOwner: '',
    leaveDeleteId: null,
    leaveDeleteDialog: false,
    leaveDeleteOption: null,
    userCredentials: {
      username: '',
      password: '',
    },
    showPass: false,
    required: [(v) => !!v || 'You need to enter your password to perform this action'],
    leaveDeleteValid: false,
    loginError: false,
    loginErrorMsg: '',
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies', user: 'getUser', getFamily: 'getFamily' }),
    familyName() {
      return this.leaveDeleteId ? this.getFamily(this.leaveDeleteId).name : 'this family';
    },
  },
  async created() {
    const res = await api.getTimezones();
    this.timezones = res.data;
    this.userCredentials.username = this.user.username;
  },
  mounted() {
    if (this.$route.query.code !== undefined && this.$route.query.code !== '') {
      this.joinFamilyReq.inviteCode = this.$route.query.code.trim();
    }
  },
  methods: {
    ...mapActions(['fetchFamilies', 'showSnackbar', 'reauthenticateUser']),
    async submitJoinFamily() {
      try {
        this.loading = true;
        this.joinError = false;
        this.joinFamilyReq.personalEventColor = this.$refs.joinPersonalColorPicker.color;
        const res = await api.joinFamily(this.joinFamilyReq);
        if (res.status === 200) {
          this.fetchFamilies();
          this.$refs.joinForm.reset();
          this.showSnackbar({ type: 'success', message: 'You have successfully joined a family!' });
        } else if (res.data.errorCode === 3001) {
          this.joinError = true;
          this.joinErrorMsg = 'This invite code does not exist. Make sure the invite code is correct and try again.';
        } else {
          this.joinError = true;
          this.joinErrorMsg =
            'There was a problem joining this family. Make sure the invite code is correct and try again.';
        }
      } catch (err) {
        this.joinError = true;
        this.joinErrorMsg =
          'There was a problem joining this family. Make sure the invite code is correct and try again.';
      } finally {
        this.loading = false;
      }
    },
    isAdmin,
    expand(index) {
      this.loadUpatedFamily(index);
      this.showUpdateForm = true;
    },
    collapse() {
      this.unloadUpdatedFamily();
      this.showUpdateForm = false;
    },
    loadUpatedFamily(index) {
      const family = this.families[index];
      this.updateFamily = {
        id: family.id,
        name: family.name,
        color: `#${family.eventColor}`,
        timezone: family.timezone,
        members: [...family.members],
        owner: { ...family.owner },
      };
      this.updateIndex = index;
    },
    unloadUpdatedFamily() {
      this.$nextTick(() => {
        this.updateIndex = -1;
        this.updateFamily = { ...this.defaultExpandedFamily };
      });
    },
    openRoleManager(index) {
      this.loadUpatedFamily(index);
      this.memberDialog = true;
    },
    closeRoleManager() {
      this.unloadUpdatedFamily();
      this.memberDialog = false;
    },
    async updateMemberRoles() {
      const req = {
        familyId: this.updateFamily.id,
        members: this.updateFamily.members
          .filter((member) => member.role !== 'OWNER')
          .map((member) => ({ user: { id: member.user.id }, role: member.role })),
      };
      try {
        this.loading = true;
        const res = await api.updateFamilyRoles(req);
        if (res.status === 200) {
          this.showSnackbar({ type: 'success', message: 'Your role changes were saved successfully!', timeout: 3000 });
          this.closeRoleManager();
        } else if (res.data.errorCode === 1007) {
          this.showSnackbar({
            type: 'warn',
            message: 'You are not authorized to update roles for this family!',
            timeout: 3000,
          });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an error processing your changes. Please try again in a few minutes!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error processing your changes. Please try again in a few minutes!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    async updateFamilyDetails() {
      const req = {
        id: this.updateFamily.id,
        eventColor: this.$refs[`familyColor-${this.updateFamily.id}`][0].color.replace('#', ''),
        name: this.updateFamily.name,
        timezone: this.updateFamily.timezone,
      };
      try {
        this.loading = true;
        const res = await api.updateFamily(req);
        if (res.status === 200) {
          this.collapse();
          this.showSnackbar({ type: 'success', message: 'Your changes were saved successfully!', timeout: 3000 });
        } else if (res.data.errorCode === 1007) {
          this.showSnackbar({
            type: 'warn',
            message: 'You are not authorized to update roles for this family!',
            timeout: 3000,
          });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an error processing your changes. Please try again in a few minutes!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error processing your changes. Please try again in a few minutes!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    openTransferDialog(index) {
      this.loadUpatedFamily(index);
      this.transferDialog = true;
    },
    closeTransferDialog() {
      this.unloadUpdatedFamily();
      this.transferDialog = false;
      this.newOwner = '';
    },
    async updateOwner() {
      const req = {
        id: this.updateFamily.id,
        owner: {
          user: {
            username: this.newOwner,
          },
        },
      };
      try {
        this.loading = true;
        const res = await api.transferFamilyOwnership(req);
        if (res.status === 200) {
          this.fetchFamilies();
          this.closeTransferDialog();
          this.showSnackbar({
            type: 'success',
            message: 'The family has been succesfully transferred!',
            timeout: 3000,
          });
        } else if (res.data.errorCode === 1007) {
          this.showSnackbar({
            type: 'warn',
            message: 'You are not authorized to update roles for this family!',
            timeout: 3000,
          });
        } else if (res.data.errorCode === 1001) {
          this.showSnackbar({
            type: 'error',
            message: 'The user you specified to be the new owner is not registered!',
            timeout: 3000,
          });
        } else if (res.data.errorCode === 1006) {
          this.showSnackbar({
            type: 'error',
            message: 'The user you specified to be the new owner is not a member of this family!',
            timeout: 3000,
          });
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an error processing your changes. Please try again in a few minutes!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error processing your changes. Please try again in a few minutes!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    leaveDeleteFamily(id, option) {
      this.leaveDeleteOption = option;
      this.leaveDeleteId = id;
      this.leaveDeleteDialog = true;
    },
    closeLeaveDelete() {
      this.leaveDeleteDialog = false;
      this.$nextTick(() => {
        this.leaveDeleteId = null;
        this.loginError = false;
        this.loginErrorMsg = '';
        if (this.leaveDeleteOption === 'leave') {
          this.$refs.leaveDeleteForm.reset();
        }
      });
    },
    async confirmDeleteFamily() {
      try {
        const res = await api.deleteFamily(this.leaveDeleteId);
        if (res.status === 200) {
          this.fetchFamilies();
          this.closeLeaveDelete();
          this.showSnackbar({
            type: 'success',
            message: 'Family has been deleted successfully!',
            timeout: 3000,
          });
        } else if (res.status === 1001) {
          this.closeLeaveDelete();
          this.showSnackbar({
            type: 'warn',
            message: 'You are not authorized to perform this action.',
            timeout: 3000,
          });
        } else {
          this.closeLeaveDelete();
          this.showSnackbar({
            type: 'error',
            message: 'We ran into a problem deleting this family. Please try again later.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeLeaveDelete();
        this.showSnackbar({
          type: 'error',
          message: 'We ran into a problem deleting this family. Please try again later.',
          timeout: 3000,
        });
      }
    },
    async confirmLeaveFamily() {
      try {
        const res = await this.reauthenticateUser(this.userCredentials);
        if (res.status !== 200) {
          this.loginError = true;
          this.loginErrorMsg = 'Your username or password was incorrect.';
          return;
        }
      } catch (err) {
        this.loginError = true;
        this.loginErrorMsg = 'Login failed, please try again.';
        return;
      }

      try {
        const res = await api.leaveFamily(this.leaveDeleteId);
        if (res.status === 200) {
          this.fetchFamilies();
          this.closeLeaveDelete();
          this.showSnackbar({
            type: 'success',
            message: `You have successfully left ${this.getFamily(this.leaveDeleteId).name}!`,
            timeout: 3000,
          });
        } else {
          this.closeLeaveDelete();
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem leaving this family. Please try again later.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.closeLeaveDelete();
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem leaving this family. Please try again later.',
          timeout: 3000,
        });
      }
    },
  },
};
</script>
