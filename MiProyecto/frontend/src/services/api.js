import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // Usa el proxy configurado en vue.config.js
});

export default {
  getProducts() {
    return api.get('/products');
  },
  createUser(user) {
    return api.post('/users/register', user);
  },
  login(credentials) {
    return api.post('/auth/login', credentials);
  },
};
