export default {
  state: {
    snackbarState: false,
    snackbarMessage: '',
  },
  mutations: {
    SET_SNACKBAR_STATE(state, payload) {
      state.snackbarState = payload.state;
    },
    SET_SNACKBAR_MESSAGE(state, payload) {
      state.snackbarMessage = payload.message;
    },
  },
  getters: {
    getSnackbarState: (state) => state.snackbarState,
    getSnackbarMessage: (state) => state.snackbarMessage,
  },
};
