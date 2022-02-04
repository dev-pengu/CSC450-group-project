import axios from 'axios';
import { getCookie } from '../util/CookieUtil';

const http = axios.create({
  baseURL: '/api/services',
  timeout: 1000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

export default {
  login(username, password) {
    return http.post('/auth/login', {
      username,
      password,
    }, {
      // eslint-disable-next-line object-shorthand
      validateStatus: function (status) {
        return status < 500;
      },
    });
  },
  getDashboardData() {
    return http.get('/dashboard', {
      headers: {
        Authorization: `Bearer ${getCookie('Authorization')}`,
      },
    });
  },
  createUser(username, email, firstName, lastName, password) {
    return http.post('/auth', {
      username,
      firstName,
      lastName,
      email,
      password,
    });
  },
};
