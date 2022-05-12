import { http } from "./clients";

export default {
  searchPolls(req) {
    return http.post('poll/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  votePoll(req) {
    return http.post('poll/vote', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createPoll(req) {
    return http.post('poll', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updatePoll(req) {
    return http.patch('poll', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deletePoll(id) {
    return http.delete('poll', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getPollResults(id) {
    return http.get('poll/results', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
}
