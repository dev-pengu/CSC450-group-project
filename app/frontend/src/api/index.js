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
  login(formData) {
    return http.post('/auth/login', {
      username: formData.username,
      password: formData.password,
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
  createUser(formData) {
    return http.post('/auth', {
      username: formData.username,
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
    });
  },
};
