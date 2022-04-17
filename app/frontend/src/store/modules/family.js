import api from '../../api';

export default {
  state: {
    families: [],
  },
  mutations: {
    FETCH_FAMILIES_SUCCESS(state, payload) {
      state.families = payload.families;
    },
    FETCH_FAMILIES_FAILURE(state) {
      state.families = [];
    },
  },
  actions: {
    async fetchFamilies({ commit }) {
      try {
        const res = await api.getFamilies();
        if (res.status === 200) {
          commit('FETCH_FAMILIES_SUCCESS', { families: res.data });
        } else {
          commit('FETCH_FAMILIES_FAILURE');
        }
        return res;
      } catch (err) {
        commit('FETCH_FAMILIES_FAILURE');
        throw err;
      }
    },
  },
  getters: {
    getFamilies: (state) => state.families,
  },
};
