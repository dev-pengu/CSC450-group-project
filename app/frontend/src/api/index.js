import axios from 'axios';

const authHttp = axios.create({
  baseURL: '/api/services',
  timeout: 1000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 1000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

export default {
  login(formData) {
    return authHttp.post(
      '/auth/login',
      {
        username: formData.username,
        password: formData.password,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  logout() {
    return authHttp.post('/auth/logout');
  },
  getDashboardData() {
    return authHttp.get('/dashboard');
  },
  createUser(formData) {
    return authHttp.post('/auth', {
      username: formData.username,
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
    });
  },
  getTimezones() {
    return http.get('/utility/timezones');
  },
  joinFamily(formData) {
    return http.get(
      'family/invite/join',
      {
        params: {
          code: formData.inviteCode,
          eventColor: formData.personalEventColor.replace('#', ''),
        },
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  createFamily(formData) {
    return http.post(
      'family',
      {
        name: formData.familyName,
        eventColor: formData.familyEventColor.replace('#', ''),
        timezone: formData.timezone,
        owner: {
          eventColor: formData.personalEventColor.replace('#', ''),
        },
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  getFamilies() {
    return http.get('family/get-family');
  },
  generateInviteCode(id) {
    return http.post(
      'family/admin/invites/generate',
      {
        familyId: id,
        persistent: true,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  sendInviteCode(formData) {
    return http.post(
      'family/admin/invites/generate',
      {
        familyId: formData.family,
        persistent: false,
        recipientEmail: formData.recipientEmail,
        role: formData.role,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
};
