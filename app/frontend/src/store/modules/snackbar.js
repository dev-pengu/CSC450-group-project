export default {
  state: {
    snackbarState: false,
    snackbarMessage: '',
    snackbarColor: 'green',
  },
  mutations: {
    SET_SNACKBAR_STATE(state, payload) {
      state.snackbarState = payload.state;
    },
    SET_SNACKBAR_MESSAGE(state, payload) {
      state.snackbarMessage = payload.message;
    },
    SET_SNACKBAR_COLOR(state, payload) {
      state.snackbarColor = payload.color;
    },
  },
  getters: {
    getSnackbarState: (state) => state.snackbarState,
    getSnackbarMessage: (state) => state.snackbarMessage,
    getSnackbarColor: (state) => state.snackbarColor,
  },
  actions: {
    showSnackbar({ commit }, payload) {
      commit('SET_SNACKBAR_COLOR', { color: payload.type === 'success' ? 'green' : 'red' });
      commit('SET_SNACKBAR_MESSAGE', { message: payload.message });
      commit('SET_SNACKBAR_STATE', { state: true });
    },
  },
};
