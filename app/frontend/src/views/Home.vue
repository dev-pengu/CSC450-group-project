<template>
  <div class="home">
    <div class="text-h4 foa_text_header--text my-3">Welcome back, {{ userFirstName }}</div>
    <v-row>
      <v-col cols="12" sm="8" md="6">
        <v-sheet color="foa_content_bg" rounded>
          <div class="text-h5 foa_nav_link--text pl-4 py-3">
            My Families
            <v-btn class="pb-1" icon color="foa_button_dark"><v-icon>mdi-plus-box</v-icon></v-btn>
          </div>
          <v-carousel hide-delimiters cycle height="325">
            <v-carousel-item v-for="(family, i) in families" :key="i">
              <v-card class="mx-auto" color="white" width="55%">
                <v-card-text>
                  <div class="text-subtitle-1 foa_text--text">{{ family.name }}</div>
                  <div class="pl-4">
                    <div class="foa_text--text">Owner: {{ family.owner }}</div>
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Manage Members</router-link>
                    <div class="d-flex align-center foa_text--text">
                      Invite code: {{ family.inviteCode }}
                      <v-btn x-small color="foa_button" class="ml-2 foa_button_text--text">Generate</v-btn>
                    </div>
                    <router-link class="d-block foa_link--text" :to="family.testRoute"
                      >Invite a Family Member</router-link
                    >
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Family Calendar</router-link>
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Manage Members</router-link>
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Family To Do List</router-link>
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Family Polls</router-link>
                    <router-link class="d-block foa_link--text" :to="family.testRoute"
                      >Family Shopping List</router-link
                    >
                    <router-link class="d-block foa_link--text" :to="family.testRoute">Family Settings</router-link>
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
export default {
  name: 'Home',
  data: () => ({
    // this is all dummy data
    // will be replaced by proper API calls later
    userFirstName: '<User>',
    families: [
      { name: '<Family Name>', owner: '<Family Owner>', inviteCode: '<Invite Code>', testRoute: '/test' },
      { name: '<Family Name>', owner: '<Family Owner>', inviteCode: '<Invite Code>', testRoute: '/test' },
      { name: '<Family Name>', owner: '<Family Owner>', inviteCode: '<Invite Code>', testRoute: '/test' },
    ],
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
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
};
</script>
