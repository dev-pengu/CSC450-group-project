import api from '../../api';

export default {
  state: {
    loggedIn: false,
    loginError: false,
    user: {},
  },
  mutations: {
    LOGIN_SUCCESS(state, payload) {
      state.loggedIn = true;
      state.user = payload.user;
    },
    LOGIN_ERROR(state) {
      state.loginError = true;
      state.user = null;
    },
    LOGOUT_SUCCESS(state) {
      state.loggedIn = false;
      state.user = null;
    },
  },
  actions: {
    async loginUser({ commit }, formData) {
      try {
        const res = await api.login(formData);
        if (res.status === 200) {
          commit('LOGIN_SUCCESS', { user: res.data });
        } else {
          commit('LOGIN_ERROR');
        }
        return res;
      } catch (err) {
        commit('LOGIN_ERROR');
        throw err;
      }
    },
    async logoutUser({ commit }) {
      await api.logout();
      commit('LOGOUT_SUCCESS');
    },
  },
  getters: {
    isLoggedIn: (state) => state.loggedIn,
    hasLoginErrored: (state) => state.loginError,
    getUser: (state) => state.user,
  },
};
