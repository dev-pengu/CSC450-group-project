const setCookie = (name, value, expiry) => {
  const expiryDate = new Date(expiry);
  const expires = `expires=${expiryDate.toUTCString()}`;
  document.cookie = `${name}=${value};${expires};path=/`;
};

const getCookie = (name) => {
  const cookieName = `${name}=`;
  const decodedCookie = decodeURIComponent(document.cookie);
  const ca = decodedCookie.split(';');
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) === 0) {
      return c.substring(cookieName.length, c.length);
    }
  }
  return '';
};

export { setCookie, getCookie };
