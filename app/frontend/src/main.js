import Vue from 'vue';
import axios from 'axios';

import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';

// eslint-disable-next-line consistent-return
axios.interceptors.response.use(undefined, (err) => {
  if (err) {
    const originalRequest = err.config;
    // eslint-disable-next-line no-underscore-dangle
    if (err.response.status === 401 && !originalRequest._retry) {
      // eslint-disable-next-line no-underscore-dangle
      originalRequest._retry = true;
      store.dispatch('logout');
      return router.push('/login');
    }
  }
});

Vue.config.productionTip = false;

new Vue({
  store,
  router,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
