export default {
  state: {
    drawerState: null,
    drawerMiniState: true,
  },
  mutations: {
    toggleDrawerState(state, val) {
      state.drawerState = val;
    },
    toggleDrawerMiniState(state) {
      state.drawerMiniState = !state.drawerMiniState;
    },
  },
  getters: {
    drawerState: (state) => state.drawerState,
    drawerMiniState: (state) => state.drawerMiniState,
  },
};
