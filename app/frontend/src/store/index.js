import { createStore } from 'vuex';
import api from '../api';

export default createStore({
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
    login({ commit }, { username, password }) {
      return new Promise((resolve, reject) => {
        api.login(username, password)
          .then((res) => {
            if (res.status === 200) {
              commit('login_success', {
                username,
              });
            }
            resolve(res);
          })
        // eslint-disable-next-line no-unused-vars
          .catch((_err) => {
            commit('login_error', {
              username,
            });
            reject(new Error('Invalid credentials!'));
          });
      });
    },
    // eslint-disable-next-line no-unused-vars
    signUp({ commit }, {
      username, email, firstName, lastName, password,
    }) {
      return new Promise((resolve, reject) => {
        api.createUser(username, email, firstName, lastName, password)
          .then((res) => {
            resolve(res);
          })
          .catch((err) => {
            if (err.message.includes('409')) {
              reject(new Error('Username or email already in use'));
            }
            reject(new Error('Couldn\'t create user.'));
          });
      });
    },
    logout({ commit }) {
      commit('logout_success');
    },
  },
  getters: {
    isLoggedIn: (state) => state.loggedIn,
    hasLoginErrored: (state) => state.loginError,
    getUsername: (state) => state.username,
  },
});
