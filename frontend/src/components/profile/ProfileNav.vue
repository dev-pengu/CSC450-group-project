<template>
  <v-sheet>
    <v-list>
      <v-list-item v-for="(item, i) in navItems" :key="i" :to="item.route">
        <v-list-item-icon>
          <v-icon>{{ item.icon }}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-sheet>
</template>

<script>
export default {
  name: 'ProfileNav',
  data: () => ({
    navItems: [
      { title: 'Edit Profile', icon: 'mdi-pencil', route: '/profile/settings' },
      { title: 'Password & Security', icon: 'mdi-lock', route: '/profile/security' },
      { title: 'Manage Families', icon: 'mdi-human-male-female-child', route: '/profile/families' },
      { title: 'Manage Calendars', icon: 'mdi-calendar', route: '/profile/calendars' },
    ],
  }),
};
</script>
