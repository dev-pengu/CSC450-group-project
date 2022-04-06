<template>
  <div class="home">
    <div class="text-h4 foa_text_header--text my-3">Welcome back, {{ user.firstName }}</div>
    <v-row>
      <v-col cols="12" sm="8" md="6">
        <v-sheet color="foa_content_bg" rounded>
          <div class="text-h5 foa_nav_link--text pl-4 py-3">
            My Families
            <FamilyModal />
          </div>
          <v-carousel hide-delimiters cycle interval="6000" height="340">
            <v-carousel-item v-for="(family, i) in families" :key="i">
              <v-card class="mx-auto" width="65%" max-height="95%">
                <v-card-text class="pt-1">
                  <div class="text-h6 foa_text--text">{{ family.name }}</div>
                  <div class="pl-4">
                    <div class="d-inline-flex align-center foa_text--text">
                      Family:
                      <v-sheet
                        color="grey lighten-1"
                        rounded
                        height="15"
                        width="15"
                        class="d-inline-flex align-center justify-center ml-2"
                      >
                        <v-sheet rounded height="80%" width="80%" :color="'#' + family.eventColor"></v-sheet>
                      </v-sheet>
                    </div>
                    <div class="d-inline-flex align-center foa_text--text ml-3">
                      Me:
                      <v-sheet
                        color="grey lighten-1"
                        rounded
                        height="15"
                        width="15"
                        class="d-inline-flex align-center justify-center ml-2"
                      >
                        <v-sheet rounded height="80%" width="80%" :color="'#' + family.memberData.eventColor"></v-sheet>
                      </v-sheet>
                    </div>
                    <div class="foa_text--text">
                      Owner: {{ family.owner.user.firstName + ' ' + family.owner.user.lastName }}
                    </div>
                    <router-link class="d-block foa_link--text" to="/test">Manage Members</router-link>
                    <div class="d-flex align-center foa_text--text">
                      Invite code: {{ family.inviteCode }}
                      <v-btn
                        v-if="isAdmin(family.memberData.role) && family.inviteCode == null"
                        x-small
                        color="foa_button"
                        class="ml-2 foa_button_text--text"
                        @click="generateInviteCode(family.id)"
                        >Generate</v-btn
                      >
                    </div>
                    <InviteModal
                      v-if="isAdmin(family.memberData.role)"
                      :family-id="family.id"
                      :family-name="family.name"
                    />
                    <router-link class="d-block foa_link--text" to="/test">Family Calendar</router-link>
                    <router-link class="d-block foa_link--text" to="/test">Manage Members</router-link>
                    <router-link class="d-block foa_link--text" to="/test">Family To Do List</router-link>
                    <router-link class="d-block foa_link--text" to="/test">Family Polls</router-link>
                    <router-link class="d-block foa_link--text" to="/test">Family Shopping List</router-link>
                    <router-link v-if="isAdmin(family.memberData.role)" class="d-block foa_link--text" to="/test"
                      >Family Settings</router-link
                    >
                  </div>
                </v-card-text>
              </v-card>
            </v-carousel-item>
          </v-carousel>
        </v-sheet>
      </v-col>
      <v-col cols="12" sm="4" md="6" align-self="center">
        <v-alert type="warning" icon="mdi-alert-outline">Work in progess</v-alert>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" sm="6">
        <div class="text-h5 foa_text_header--text mb-3">
          My Calendar
          <v-btn class="pb-1" icon :color="btnColor"><v-icon>mdi-plus-box</v-icon></v-btn>
        </div>
        <v-sheet height="400">
          <v-calendar type="4day" :events="events"></v-calendar>
        </v-sheet>
      </v-col>
      <v-col cols="12" sm="6">
        <div class="text-h5 foa_text_header--text mb-3">
          My To-Do List
          <v-btn class="pb-1" icon :color="btnColor"><v-icon>mdi-plus-box</v-icon></v-btn>
        </div>
        <div v-for="(todo, i) in todos" :key="i" class="d-flex align-center">
          <v-checkbox color="foa_button" :label="todo.title" hide-details>
            <template #prepend>
              <v-sheet height="24" width="8" :color="todo.familyColor"></v-sheet>
            </template>
          </v-checkbox>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import FamilyModal from '../components/FamilyModal.vue';
import InviteModal from '../components/InviteUserModal.vue';
import api from '../api';
import { isAdmin } from '../util/RoleUtil';

export default {
  name: 'Home',
  components: {
    FamilyModal,
    InviteModal,
  },
  data: () => ({
    // this is all dummy data
    // will be replaced by proper API calls later
    events: [
      {
        name: 'Event #1',
        start: new Date(),
        end: new Date(new Date().getTime() + 60 * 60000),
        timed: true,
        color: 'red',
      },
    ],
    todos: [
      { title: 'Mow the lawn', familyColor: 'red' },
      { title: 'File taxes', familyColor: 'orange' },
      { title: 'Go to the store', familyColor: 'green' },
    ],
  }),
  computed: {
    ...mapGetters(['families', 'user']),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  created() {
    this.getFamilies();
  },
  methods: {
    ...mapActions(['getFamilies']),
    async generateInviteCode(id) {
      const res = await api.generateInviteCode(id);
      if (res.status === 200) {
        this.getFamilies();
      }
      // TODO: add an error to snackbar stating there was an error with adding invite code
    },
    isAdmin,
  },
};
</script>
