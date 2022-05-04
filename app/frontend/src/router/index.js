import VueRouter from 'vue-router';
import Vue from 'vue';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import SignUp from '../views/SignUp.vue';
import NotFound from '../views/NotFound.vue';
import PasswordReset from '../views/PasswordReset.vue';
import Polling from '../views/poll-app/Polling.vue';
import CreatePoll from '../views/poll-app/CreatePoll.vue';
import PollManager from '../views/poll-app/ManagePolls.vue';
import ResultViewer from '../views/poll-app/ResultViewer.vue';
import ProfileSettings from '../views/profile/UserProfile.vue';
import UserSecurity from '../views/profile/UserSecurity.vue';
import UserFamilies from '../views/profile/UserFamilies.vue';
import Shopping from '../views/shopping-app/Shopping.vue';
import ShoppingList from '../views/shopping-app/ShoppingList.vue';
import Calendaring from '../views/calendar/Calendar.vue';
import CalendarManager from '../views/profile/CalendarManager.vue';

Vue.use(VueRouter);
const routes = [
  {
    path: '/dashboard',
    name: 'Home',
    component: Home,
    meta: {
      requiresAuth: true,
      title: 'Dashboard',
    },
  },
  {
    path: '/home',
    redirect: '/dashboard',
  },
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: 'Login'
    }
  },
  {
    path: '/signup',
    name: 'Sign Up',
    component: SignUp,
    meta: {
      title: 'Sign Up'
    }
  },
  {
    path: '/passwordReset',
    name: 'Password Reset',
    component: PasswordReset,
    meta: {
      title: 'Reset Password'
    }
  },
  {
    path: '/polls',
    redirect: '/polls/view',
  },
  {
    path: '/polls/view',
    name: 'Family Polling',
    component: Polling,
    meta: {
      requiresAuth: true,
      title: 'Polls',
    },
  },
  {
    path: '/polls/create',
    name: 'Create a Poll',
    component: CreatePoll,
    meta: {
      requiresAuth: true,
      title: 'Create Poll',
    },
  },
  {
    path: '/polls/manage',
    name: 'Manage Polls',
    component: PollManager,
    meta: {
      requiresAuth: true,
      title: 'Manage Polls',
    },
  },
  {
    path: '/polls/results',
    name: 'View Poll Results',
    component: ResultViewer,
    meta: {
      requiresAuth: true,
      title: 'Poll Results',
    },
  },
  {
    path: '/profile/settings',
    name: 'Profile Settings',
    component: ProfileSettings,
    meta: {
      requiresAuth: true,
      title: 'Edit Profile',
    },
  },
  {
    path: '/profile/security',
    name: 'Password & Security',
    component: UserSecurity,
    meta: {
      requiresAuth: true,
      title: 'Password & Security',
    },
  },
  {
    path: '/profile/families',
    name: 'Manage Families',
    component: UserFamilies,
    meta: {
      requiresAuth: true,
      title: 'Manage Families',
    },
  },
  {
    path: '/profile',
    redirect: '/profile/settings',
  },
  {
    path: '/shopping',
    redirect: '/shopping/view',
  },
  {
    path: '/shopping/view',
    name: 'Family Shopping Lists',
    component: Shopping,
    meta: {
      requiresAuth: true,
      title: 'Shopping Lists',
    },
  },
  {
    path: '/shopping/list',
    name: 'Shopping List',
    component: ShoppingList,
    meta: {
      requiresAuth: true,
      title: 'Shopping List',
    },
  },
  {
    path: '/calendar/view',
    name: 'Family Calendar',
    component: Calendaring,
    meta: {
      requiresAuth: true,
      title: 'Family Calendar',
    },
  },
  {
    path: '/calendar',
    redirect: '/calendar/view',
  },
  {
    path: '/profile/calendars',
    name: 'Manage Calendars',
    component: CalendarManager,
    meta: {
      requiresAuth: true,
      title: 'Manage Calendars',
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'Not Found',
    component: NotFound,
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

export default router;
