import api from '../../api';

export default {
  state: {
    loggedIn: false,
    loginError: false,
    username: null,
  },
  mutations: {
    login_success(state, payload) {
      state.loggedIn = true;
      state.username = payload.username;
    },
    login_error(state, payload) {
      state.loginError = true;
      state.username = payload.username;
    },
    logout_success(state) {
      state.loggedIn = false;
      state.username = null;
    },
  },
  actions: {
    login({ commit }, formData) {
      return new Promise((resolve, reject) => {
        api
          .login(formData)
          .then((res) => {
            if (res.status === 200) {
              commit('login_success', {
                username: formData.username,
              });
            }
            resolve(res);
          })
          // eslint-disable-next-line no-unused-vars
          .catch((_err) => {
            commit('login_error', {
              username: formData.username,
            });
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
  },
};
