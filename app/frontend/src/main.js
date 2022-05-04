import Vue from 'vue';
import { http, authHttp } from './api/modules/clients';
import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';

Vue.config.productionTip = false;

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

router.beforeEach((to, from, next) => {
  document.title = to.meta ? `${to.meta.title} | Happy Home` : 'Happy Home';
  next()
})

http.interceptors.response.use((response) => {
  if (response.status === 403) {
    router.push('/login');
  }
  return response;
}, (err) => {
  if (err) {
    alert(err);
    if (err.response.status === 403) {
      router.push('/login');
    }
  }
  return Promise.reject(err);
});

authHttp.interceptors.response.use((response) => {
  if (response.status === 403) {
    router.push('/login');
  }
  return response;
}, (err) => {
  if (err) {
    alert(err);
    if (err.response.status === 403) {
      router.push('/login');
    }
  }
  return Promise.reject(err);
});

new Vue({
  store,
  router,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
