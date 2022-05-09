import { http } from "./clients";

export default {
  getTimezones() {
    return http.get('/utility/timezones');
  },
}
