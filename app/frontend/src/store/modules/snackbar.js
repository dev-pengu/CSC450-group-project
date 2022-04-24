export default {
  state: {
    snackbarState: false,
    snackbarMessage: '',
    snackbarColor: 'green',
    snackbarIcon: '',
    timeout: -1,
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
    SET_SNACKBAR_ICON(state, payload) {
      state.snackbarIcon = payload.icon;
    },
    SET_TIMEOUT(state, payload) {
      state.timeout = payload.timeout;
    },
  },
  getters: {
    getSnackbarState: (state) => state.snackbarState,
    getSnackbarMessage: (state) => state.snackbarMessage,
    getSnackbarColor: (state) => state.snackbarColor,
    getSnackbarIcon: (state) => state.snackbarIcon,
    getTimeout: (state) => state.timeout,
  },
  actions: {
    showSnackbar({ commit }, payload) {
      let color;
      let icon;
      switch (payload.type) {
        case 'success':
          color = 'green';
          icon = 'mdi-check-bold';
          break;
        case 'error':
          color = 'red';
          icon = 'mdi-alert-circle';
          break;
        case 'warn':
          color = '#ffcc00';
          icon = 'mdi-alert';
          break;
        case 'info':
          color = '#33ccff';
          icon = 'mdi-information';
          break;
        default:
          color = 'green';
          icon = '';
          break;
      }

      commit('SET_SNACKBAR_COLOR', { color });
      commit('SET_SNACKBAR_MESSAGE', { message: payload.message });
      commit('SET_SNACKBAR_STATE', { state: true });
      commit('SET_SNACKBAR_ICON', { icon });
      commit('SET_TIMEOUT', { timeout: payload.timeout });
    },
  },
};
