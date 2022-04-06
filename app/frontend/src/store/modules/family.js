import api from '../../api';

export default {
  state: {
    families: [],
  },
  mutations: {
    fetch_families_success(state, payload) {
      state.families = payload;
    },
    fetch_families_failure(state) {
      state.families = [];
    },
  },
  actions: {
    async getFamilies({ commit }) {
      const res = await api.getFamilies();
      if (res.status === 200) {
        commit('fetch_families_success', res.data);
      } else {
        commit('fetch_families_failure');
        throw new Error('Could not fetch families');
      }
    },
  },
  getters: {
    families: (state) => state.families,
  },
};
