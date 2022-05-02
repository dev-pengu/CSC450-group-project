import { http } from "./clients";

export default {
  getUserSettings() {
    return http.get('user/settings', {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateUserSettings(req) {
    return http.patch('user/settings', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteUser(username) {
    return http.delete('user', {
      params: { username },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
}
