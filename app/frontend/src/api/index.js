import axios from 'axios';

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
  logout() {
    return http.post('/auth/logout');
  },
  getDashboardData() {
    return http.get('/dashboard');
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
