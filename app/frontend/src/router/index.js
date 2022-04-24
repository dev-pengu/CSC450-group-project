import VueRouter from 'vue-router';
import Vue from 'vue';
import store from '../store';
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

Vue.use(VueRouter);
const routes = [
  {
    path: '/dashboard',
    name: 'Home',
    component: Home,
    meta: {
      requiresAuth: true,
      title: 'Home',
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
  },
  {
    path: '/signup',
    name: 'Sign Up',
    component: SignUp,
  },
  {
    path: '/passwordReset',
    name: 'Password Reset',
    component: PasswordReset,
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

router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!store.getters.isLoggedIn) {
      next({
        path: '/login',
      });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
