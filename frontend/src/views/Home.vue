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
              <v-card class="mx-auto" width="65%" height="95%">
                <v-card-text class="pt-1">
                  <div class="text-h6 foa_text--text">{{ family.name }}</div>
                  <div class="pl-4">
                    <div class="d-inline-flex align-center foa_text--text">
                      Family:
                      <v-sheet
                        color="grey lighten-1"
                        rounded
                        height="16"
                        width="16"
                        class="d-inline-flex align-center justify-center ml-2"
                      >
                        <v-sheet rounded height="12" width="12" :color="'#' + family.eventColor"></v-sheet>
                      </v-sheet>
                    </div>
                    <div class="d-inline-flex align-center foa_text--text ml-3">
                      Me:
                      <v-sheet
                        color="grey lighten-1"
                        rounded
                        height="16"
                        width="16"
                        class="d-inline-flex align-center justify-center ml-2"
                      >
                        <v-sheet rounded height="12" width="12" :color="'#' + family.memberData.eventColor"></v-sheet>
                      </v-sheet>
                    </div>
                    <div class="foa_text--text" :class="{ 'font-weight-bold': family.owner.user.id === user.id }">
                      Owner:
                      {{
                        family.owner.user.firstName +
                        ' ' +
                        family.owner.user.lastName +
                        (family.owner.user.id === user.id ? ' (me)' : '')
                      }}
                    </div>
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
                    <ManageMembersModal v-if="isAdmin(family.memberData.role)" :family="family" />
                    <router-link
                      class="d-block foa_link--text"
                      :to="{ path: '/calendar/view', query: { familyId: family.id } }"
                      >Family Calendar</router-link
                    >
                    <router-link
                      class="d-block foa_link--text"
                      :to="{ path: '/todo/view', query: { familyId: family.id, name: family.name } }"
                      >Family To Do List</router-link
                    >
                    <router-link
                      class="d-block foa_link--text"
                      :to="{ path: '/polls/view', query: { familyId: family.id, name: family.name } }"
                      >Family Polls</router-link
                    >
                    <router-link
                      class="d-block foa_link--text"
                      :to="{ path: '/shopping/view', query: { familyId: family.id, name: family.name } }"
                      >Family Shopping List</router-link
                    >
                    <router-link
                      v-if="isAdmin(family.memberData.role)"
                      class="d-block foa_link--text"
                      to="/profile/families"
                      >Family Settings</router-link
                    >
                  </div>
                </v-card-text>
              </v-card>
            </v-carousel-item>
          </v-carousel>
        </v-sheet>
      </v-col>
      <v-col cols="12" sm="4" md="6">
        <PollPreview></PollPreview>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" sm="6">
        <CalendarPreview :start="calendarStart" :end="calendarEnd" :calendar-height="500"></CalendarPreview>
      </v-col>
      <v-col cols="12" sm="6">
        <TodoPreview :end="todoEnd"></TodoPreview>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import FamilyModal from '../components/FamilyModal.vue';
import InviteModal from '../components/InviteUserModal.vue';
import ManageMembersModal from '../components/ManageMembersModal.vue';
import PollPreview from '../components/poll-app/PollPreview.vue';
import CalendarPreview from '../components/calendar/CalendarPreview.vue';
import TodoPreview from '../components/todo-app/TodoPreview.vue';
import api from '../api';
import { isAdmin } from '../util/RoleUtil';

export default {
  name: 'Home',
  components: {
    FamilyModal,
    InviteModal,
    ManageMembersModal,
    PollPreview,
    CalendarPreview,
    TodoPreview,
  },
  computed: {
    ...mapGetters({ families: 'getFamilies', getFamily: 'getFamily', user: 'getUser' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
    calendarStart() {
      const date = new Date();
      return `${date.getFullYear()}-${`0${date.getMonth() + 1}`.slice(-2)}-${`0${date.getDate()}`.slice(-2)}`;
    },
    calendarEnd() {
      const date = new Date();
      date.setDate(date.getDate() + 1);
      return `${date.getFullYear()}-${`0${date.getMonth() + 1}`.slice(-2)}-${`0${date.getDate()}`.slice(-2)}`;
    },
    todoEnd() {
      const date = new Date();
      date.setDate(date.getDate() + 3);
      return date.toISOString().substring(0, 10);
    },
  },
  created() {
    this.fetchFamilies();
  },
  methods: {
    ...mapActions(['fetchFamilies', 'showSnackbar']),
    async generateInviteCode(id) {
      try {
        const res = await api.generateInviteCode(id);
        if (res.status === 200) {
          this.fetchFamilies();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem generating an invite code for this family, please try again.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem generating an invite code for this family, please try again.',
          timeout: 3000,
        });
      }
    },
    isAdmin,
  },
};
</script>
