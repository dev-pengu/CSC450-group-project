import api from '../../api';

export default {
  state: {
    loggedIn: false,
    loginError: false,
    // TODO: remove
    username: null,
    user: {},
  },
  mutations: {
    login_success(state, payload) {
      state.loggedIn = true;
      state.username = payload.username;
      state.user = payload;
    },
    login_error(state) {
      state.loginError = true;
      state.username = null;
      state.user = null;
    },
    logout_success(state) {
      state.loggedIn = false;
      state.username = null;
      state.user = null;
    },
  },
  actions: {
    login({ commit }, formData) {
      // TODO: update this to not be promise based
      return new Promise((resolve, reject) => {
        api
          .login(formData)
          .then((res) => {
            if (res.status === 200) {
              commit('login_success', res.data);
            }
            resolve(res);
          })
          // eslint-disable-next-line no-unused-vars
          .catch((_err) => {
            commit('login_error');
            reject(new Error('Invalid credentials!'));
          });
      });
    },
    logout({ commit }) {
      api.logout().then(() => {
        commit('logout_success');
      });
    },
  },
  getters: {
    isLoggedIn: (state) => state.loggedIn,
    hasLoginErrored: (state) => state.loginError,
    getUsername: (state) => state.username,
    user: (state) => state.user,
  },
};
