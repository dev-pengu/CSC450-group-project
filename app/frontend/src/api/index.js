import axios from 'axios';

const authHttp = axios.create({
  baseURL: '/api/services/auth',
  timeout: 1000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

const XSRF_TOKEN = document.cookie.match(new RegExp(`XSRF-TOKEN=([^;]+)`));
const http = axios.create({
  baseURL: '/api/v1',
  timeout: 1000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    'X-XSRF-TOKEN': XSRF_TOKEN === null ? null : XSRF_TOKEN[1],
  },
});

export default {
  getCsrf() {
    return authHttp.get('/csrf');
  },
  login(userCredentials) {
    return authHttp.post(
      '/login',
      {
        username: userCredentials.username,
        email: userCredentials.email,
        password: userCredentials.password,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  logout() {
    return authHttp.post('/logout');
  },
  createUser(formData) {
    return authHttp.post('/register', {
      username: formData.username,
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
    });
  },
  sendResetCode(username) {
    return authHttp.post('/passwordReset', {
      username,
    });
  },
  changePassword(formData) {
    return authHttp.post('/changePassword', {
      username: formData.username !== null && formData.username !== undefined ? formData.username : null,
      email: formData.email !== null && formData.email !== undefined ? formData.email : null,
      resetCode: formData.resetCode !== null && formData.resetCode !== undefined ? formData.resetCode : null,
      oldPassword: formData.oldPassword !== null && formData.oldPassword !== undefined ? formData.oldPassword : null,
      newPassword: formData.newPassword,
    });
  },
  usernameFree(username) {
    return authHttp.get('/usernameCheck', {
      params: { username },
    });
  },
  emailFree(email) {
    return authHttp.get('/emailCheck', {
      params: { email },
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
        initialRole: formData.role,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  getFamiliesForSelect() {
    return http.get('family/familySelect');
  },
  getMembersForSelect(familyId) {
    return http.get('family/memberSelect', { params: { id: familyId } });
  },
  searchPolls(req) {
    return http.post('poll/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getUserSettings() {
    return http.get('user/settings', {
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
  updateUserSettings(req) {
    return http.patch('user/settings', req, {
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
  deleteUser(username) {
    return http.delete('user', {
      params: { username },
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
  transferFamilyOwnership(req) {
    return http.patch('/family/admin/transferOwnership', req, {
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
  updateFamily(req) {
    return http.patch('/family/admin/update', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  leaveFamily(id) {
    return http.delete('/family/leave', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteFamily(id) {
    return http.delete('/family/admin/delete', {
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
  updateFamilyRoles(req) {
    return http.post('/family/admin/roles', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  searchShoppingLists(req) {
    return http.post('/shopping/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteShoppingList(id) {
    return http.delete('/shopping', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateShoppingList(req) {
    return http.patch('/shopping', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createShoppingList(req) {
    return http.post('/shopping', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getShoppingList(id) {
    return http.get('/shopping', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createShoppingListItem(req) {
    return http.post('/shopping/item', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateShoppingListItem(req) {
    return http.patch('/shopping/item', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteShoppingListItem(id) {
    return http.delete('/shopping/item', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
};
