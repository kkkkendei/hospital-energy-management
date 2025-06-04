import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';

Vue.config.productionTip = false

Vue.use(ElementUI);

// 配置 Axios 基础 URL (指向后端API)
// 在开发环境中，你可能需要配置 vue.config.js 来处理跨域问题
// axios.defaults.baseURL = 'http://localhost:8081/api'; // 后端 API 地址
axios.defaults.baseURL = ''; // 设置为空字符串，以便代理能够处理相对路径
Vue.prototype.$http = axios;

// 在创建 Vue 实例之前，尝试从 localStorage 恢复用户信息到 Vuex
store.dispatch('auth/fetchUser').then(() => {
  new Vue({
    router,
    store,
    render: h => h(App)
  }).$mount('#app');
}).catch(error => {
  console.error("Error during initial auth/fetchUser:", error);
  // 即使 fetchUser 失败，也继续加载应用
  new Vue({
    router,
    store,
    render: h => h(App)
  }).$mount('#app');
}); 