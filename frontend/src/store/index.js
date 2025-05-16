import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios';

Vue.use(Vuex)

const authModule = {
  namespaced: true,
  state: {
    user: null, // 可以存储用户信息对象
    token: localStorage.getItem('user-token') || ''
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem('user-token', token); // 持久化token
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`; // 设置axios请求头
    },
    CLEAR_TOKEN(state) {
      state.token = '';
      localStorage.removeItem('user-token');
      delete axios.defaults.headers.common['Authorization'];
    },
    SET_USER(state, user) {
      state.user = user;
      // 你也可以将用户信息存储在localStorage，但要注意敏感信息
    },
    CLEAR_USER(state) {
      state.user = null;
    }
  },
  actions: {
    // 登录action
    async login({ commit }, credentials) {
      try {
        const response = await axios.post('/api/auth/signin', credentials);
        // 后端返回的 JwtResponse 结构为 { token, id, username, email, roles }
        const token = response.data.token;
        const user = {
            id: response.data.id,
            username: response.data.username,
            email: response.data.email,
            roles: response.data.roles // roles 应该是数组
        };

        if (!token || !user.username) {
          throw new Error('Login failed: Invalid token or user data received.');
        }

        commit('SET_TOKEN', token);
        commit('SET_USER', user);
        return Promise.resolve(user);
      } catch (error) {
        commit('CLEAR_TOKEN');
        commit('CLEAR_USER');
        return Promise.reject(error.response ? error.response.data : error.message);
      }
    },
    // 注册action
    async register(_context, userData) {
      try {
        const response = await axios.post('/api/auth/signup', userData);
        // 注册成功后，后端返回 MessageResponse
        return Promise.resolve(response.data);
      } catch (error) {
        return Promise.reject(error.response ? error.response.data : error.message);
      }
    },
    // 登出action
    logout({ commit }) {
      commit('CLEAR_TOKEN');
      commit('CLEAR_USER');
      return Promise.resolve();
    },
    // 可选: 应用加载时尝试从token获取用户信息
    async fetchUser({ commit, state, dispatch }) { // Added dispatch
      if (state.token && !state.user) { // 检查 user 是否真的未设置
        try {
          // 理想情况: 有 /api/auth/me 接口
          // const { data } = await axios.get('/api/users/me'); // 假设接口路径
          // commit('SET_USER', data);
          // return Promise.resolve(data);

          // 简化逻辑：如果token存在但用户信息丢失（例如，页面刷新后Vuex状态重置）
          // 且我们信任存储在localStorage中的token是有效的（或者至少，它没有被明确地使无效）
          // 我们可以尝试重新解析用户信息。但JWT不应该在客户端解析。
          // 最好的做法是在应用启动时，如果token存在，就调用一个端点来验证token并取回用户信息。

          // 检查 token 是否是旧的模拟 token
          if (state.token.startsWith('fake-jwt-token-for-')) {
            const usernameFromToken = state.token.replace('fake-jwt-token-for-','');
            commit('SET_USER', { username: usernameFromToken, roles: ['USER'] }); // 假设角色，实际应从服务器获取
            console.warn("fetchUser: Restored user from fake token. This is for development only.");
          } else if (axios.defaults.headers.common['Authorization']) {
            // 如果 axios header 中有 Authorization，说明 token 应该在 SET_TOKEN 时设置过
            // 但 state.user 为空，说明可能刷新了页面，Vuex state 重置了。
            // 此时，如果没有 /me 接口，我们无法安全地恢复用户信息。
            // 强制重新登录是更安全的选择，或者接受一个简化的用户信息（如果可以从token中安全获取）。
            // 对于本示例，我们假设如果token存在，就尝试恢复一个最小化的用户信息，
            // 或者提示需要后端 /me 接口。
            // 重要：在生产中，应通过后端端点验证令牌并获取用户信息。
            console.warn("fetchUser: Token exists but user details are not in state. A backend '/api/auth/me' endpoint is highly recommended to securely fetch user details on app load.");
            // 如果你的JWT token在其payload中包含用户信息（不推荐，但常见），且你信任它未被篡改，
            // 你可以在这里解析它，但这不是最佳实践。
            // 暂不实现客户端JWT解析。如果用户信息无法恢复，用户可能需要重新登录。
            // 或者，如果登录时保存了足够的用户信息到localStorage（除了token），则可以在此恢复。
            // 现在，如果 user 为 null，并且不是 fake token，我们将依赖于后续的路由守卫来处理。
          } else {
             // Token 存在于 localStorage，但不在 axios headers 里，可能初始化时未正确设置
             // 或者 token 是无效的
             dispatch('logout'); // 清理无效状态
          }
        } catch (error) {
          console.error('Error in fetchUser, or token invalid:', error);
          dispatch('logout'); // 清理token和user
        }
      }
    }
  },
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user,
    userRole: state => (state.user && state.user.roles && state.user.roles.length > 0) ? state.user.roles[0] : null // 简化为取第一个角色
  }
}

export default new Vuex.Store({
  modules: {
    auth: authModule
  }
  // 你可以在这里添加更多的模块
}) 