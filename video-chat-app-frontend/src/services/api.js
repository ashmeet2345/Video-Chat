import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor to add auth token to requests
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle response errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (userData) => apiClient.post('/auth/register', userData),
  logout: () => apiClient.post('/auth/logout'),
  getCurrentUser: () => apiClient.get('/auth/me')
};

// User API
export const userApi = {
  getUsers: () => apiClient.get('/users'),
  getUserById: (id) => apiClient.get(`/users/${id}`),
  updateUser: (id, data) => apiClient.put(`/users/${id}`, data)
};

// Contacts API
export const contactsApi = {
  getContacts: () => apiClient.get('/contacts'),
  addContact: (userId) => apiClient.post('/contacts', { userId }),
  removeContact: (contactId) => apiClient.delete(`/contacts/${contactId}`),
  acceptContact: (contactId) => apiClient.put(`/contacts/${contactId}/accept`),
  rejectContact: (contactId) => apiClient.put(`/contacts/${contactId}/reject`)
};

// Call History API
export const callHistoryApi = {
  getCallHistory: () => apiClient.get('/calls/history'),
  getCallDetails: (callId) => apiClient.get(`/calls/history/${callId}`)
};

export default {
  auth: authApi,
  users: userApi,
  contacts: contactsApi,
  callHistory: callHistoryApi
};