import { authHttp } from './clients';

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
      timezone: formData.timezone,
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
};
