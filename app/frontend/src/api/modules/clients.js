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

export { authHttp, http }
