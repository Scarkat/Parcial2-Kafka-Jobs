import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8082', // URL del Gateway (apiservice)
});

export default api;
