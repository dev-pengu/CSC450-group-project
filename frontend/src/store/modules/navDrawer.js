export default {
  state: {
    drawerState: null,
    drawerMiniState: true,
  },
  mutations: {
    SET_DRAWER_STATE(state, payload) {
      state.drawerState = payload.state;
    },
    SET_DRAWER_MINI_STATE(state, payload) {
      state.drawerMiniState = payload.state;
    },
  },
  getters: {
    getDrawerState: (state) => state.drawerState,
    getDrawerMiniState: (state) => state.drawerMiniState,
  },
};
