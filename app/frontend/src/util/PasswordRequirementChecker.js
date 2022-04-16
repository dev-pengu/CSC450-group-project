export default (password) => {
  const lowerCaseMatch = password.match(/[a-z]/);
  const upperCaseMatch = password.match(/[A-Z]/);
  const numberMatch = password.match(/[0-9]/);
  const specialMatch = password.match(/[!@#$%^&*()-+?]/);
  return {
    length: password.length > 12,
    lowerCase: lowerCaseMatch !== null,
    upperCase: upperCaseMatch !== null,
    number: numberMatch !== null,
    special: specialMatch !== null,
  };
};
