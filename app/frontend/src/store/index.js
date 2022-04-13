import Vuex from 'vuex';
import Vue from 'vue';
import createPersistedState from 'vuex-persistedstate';

// store modules
import auth from './modules/auth';
import navDrawer from './modules/navDrawer';
import family from './modules/family';
import snackbar from './modules/snackbar';

Vue.use(Vuex);
export default new Vuex.Store({
  modules: {
    auth,
    navDrawer,
    family,
    snackbar,
  },
  plugins: [createPersistedState({ key: 'appStore', storage: window.sessionStorage })],
});
