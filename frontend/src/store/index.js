import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios';

Vue.use(Vuex)

const authModule = {
  namespaced: true,
  state: {
    user: null, // 用户信息对象
    // token: localStorage.getItem('user-token') || '' // No more token
  },
  mutations: {
    // SET_TOKEN(state, token) { // No more token
    //   state.token = token;
    //   localStorage.setItem('user-token', token);
    //   axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    // },
    // CLEAR_TOKEN(state) { // No more token
    //   state.token = '';
    //   localStorage.removeItem('user-token');
    //   delete axios.defaults.headers.common['Authorization'];
    // },
    SET_USER(state, user) {
      state.user = user; // user might be null to clear it
      if (user) {
        localStorage.setItem('user-info', JSON.stringify(user)); // Store user info
      } else {
        localStorage.removeItem('user-info'); // Clear user info
      }
    },
    CLEAR_USER(state) { // Combined into SET_USER(state, null)
      state.user = null;
      localStorage.removeItem('user-info');
    }
  },
  actions: {
    async login({ commit }, credentials) {
      try {
        const response = await axios.post('/api/auth/signin', credentials);
        // Backend now returns UserLoginResponse: { id, username, email, roles, message }
        const userData = response.data;
        if (userData && userData.username) { // Check if login was successful based on response
          const user = {
            id: userData.id,
            username: userData.username,
            email: userData.email,
            roles: userData.roles,
            enabled: userData.enabled
          };
          commit('SET_USER', user);
          return Promise.resolve(user);
        } else {
          // If backend doesn't send username, assume login failed or unexpected response
          throw new Error(userData.message || 'Login failed: Unexpected response from server.');
        }
      } catch (error) {
        commit('SET_USER', null); // Clear user on login failure
        const errorMessage = error.response && error.response.data && error.response.data.message
                           ? error.response.data.message
                           : (error.message || 'Login failed due to an unexpected error.');
        return Promise.reject(new Error(errorMessage));
      }
    },
    async register(_context, userData) {
      try {
        const response = await axios.post('/api/auth/signup', userData);
        return Promise.resolve(response.data);
      } catch (error) {
        const errorMessage = error.response && error.response.data && error.response.data.message
                           ? error.response.data.message
                           : (error.message || 'Registration failed due to an unexpected error.');
        return Promise.reject(new Error(errorMessage));
      }
    },
    logout({ commit }) {
      commit('SET_USER', null); // Clear user from state and localStorage
      // No token to clear from axios headers anymore
      return Promise.resolve();
    },
    fetchUser({ commit }) { // Simplified fetchUser
      const storedUser = localStorage.getItem('user-info');
      if (storedUser) {
        try {
          const user = JSON.parse(storedUser);
          commit('SET_USER', user); // Restore user from localStorage
          return Promise.resolve(user);
        } catch (e) {
          localStorage.removeItem('user-info'); // Corrupted data
          return Promise.reject(e);
        }
      }
      return Promise.resolve(null);
    }
  },
  getters: {
    isAuthenticated: state => !!state.user, // Authenticated if user object exists
    currentUser: state => state.user,
    userRole: state => (state.user && state.user.roles && state.user.roles.length > 0) ? state.user.roles[0] : null
  }
}

export default new Vuex.Store({
  modules: {
    auth: authModule
  }
  // 你可以在这里添加更多的模块
}) 