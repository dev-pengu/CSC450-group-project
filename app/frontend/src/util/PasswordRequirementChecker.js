export default (password) => ({
  length: password.length > 12,
  lowerCase: password.match(/[a-z]/),
  upperCase: password.match(/[A-Z]/),
  number: password.match(/[0-9]/),
  special: password.match(/[!@#$%^&*()-+?]/),
});
