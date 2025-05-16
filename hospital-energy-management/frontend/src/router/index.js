import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'; // 引入 Vuex store
// 懒加载组件，提高初始加载速度
const Login = () => import(/* webpackChunkName: "login" */ '../views/Login.vue')
const Register = () => import(/* webpackChunkName: "register" */ '../views/Register.vue')
const Dashboard = () => import(/* webpackChunkName: "dashboard" */ '../views/Dashboard.vue')
const MainLayout = () => import(/* webpackChunkName: "layout" */ '../views/MainLayout.vue')
// const EnergyDataManagement = () => import(/* webpackChunkName: "energy" */ '../views/EnergyDataManagement.vue'); // 移除旧的
const DeviceManagement = () => import(/* webpackChunkName: "device" */ '../views/DeviceManagement.vue');

// 新的能源模块组件
const EnergyDataEntry = () => import(/* webpackChunkName: "energy-entry" */ '../views/EnergyDataEntry.vue');
const EnergyDataView = () => import(/* webpackChunkName: "energy-view" */ '../views/EnergyDataView.vue');
const EnergyAnalysis = () => import(/* webpackChunkName: "energy-analysis" */ '../views/EnergyAnalysis.vue'); // 新增导入

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login' // 默认重定向到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/app',
    component: MainLayout, // 使用主布局组件
    meta: { requiresAuth: true }, // 给父路由添加 meta，子路由会继承
    children: [
      {
        path: '', // /app 默认重定向到 dashboard
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        // meta: { requiresAuth: true } // 从父路由继承
      },
      {
        path: 'energy-data-entry', // 新的能源数据录入路由
        name: 'EnergyDataEntry',
        component: EnergyDataEntry,
        // meta: { requiresAuth: true } // 从父路由继承
      },
      {
        path: 'energy-data', // 修改为能源数据查看，并指向新的组件
        name: 'EnergyDataView',
        component: EnergyDataView,
        // meta: { requiresAuth: true } // 从父路由继承
      },
      {
        path: 'energy-analysis', // 新增能耗分析路由
        name: 'EnergyAnalysis',
        component: EnergyAnalysis,
      },
      {
        path: 'devices',
        name: 'DeviceManagement',
        component: DeviceManagement,
        // meta: { requiresAuth: true } // 从父路由继承
      }
      // 可以在这里继续添加其他子路由
    ]
  }
  // 你可以在这里添加更多的路由
]

const router = new VueRouter({
  mode: 'history', // 使用 history 模式，需要后端配合处理URL刷新
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 从 Vuex store 获取认证状态
  // 确保在 store 初始化之后，特别是 fetchUser action 执行之后再准确获取
  // 对于首次加载，store.state.auth.user 可能还未从 localStorage恢复
  // 一个更稳妥的方式是检查 localStorage 或者等待 store 初始化完成
  // 但直接用 getter 也是常见做法，依赖于 store/index.js 中 fetchUser 的同步性（或异步后的状态）

  let isAuthenticated = store.getters['auth/isAuthenticated'];

  // 如果 store 中的 user 状态尚未通过 fetchUser 从 localStorage 初始化，
  // 我们可以尝试直接检查 localStorage 作为备用方案，但这会使逻辑分散。
  // 更好的做法是确保 fetchUser 在应用初始化时被调用。
  if (!isAuthenticated) {
      const storedUserToken = localStorage.getItem('user-token'); // 检查 token 而不是 user-info
      if (storedUserToken) {
          // 如果token存在，但Vuex状态未同步，尝试通过fetchUser同步
          // 这假设 fetchUser 会设置 token 和 user 状态
          // 注意：直接在守卫中 commit 可能不是最佳实践，最好是 dispatch action
          // 但为了快速修复/检查，我们先这样；理想情况是应用加载时 fetchUser 已完成
          store.dispatch('auth/fetchUser'); // dispatch fetchUser action
          isAuthenticated = store.getters['auth/isAuthenticated']; // Re-check after dispatch
      }
  }

  if (to.matched.some(record => record.meta.requiresAuth)) { // 检查目标路由是否需要认证
    if (!isAuthenticated) { // 如果需要认证但用户未认证
      next({
        name: 'Login', // 使用命名路由跳转更佳
        query: { redirect: to.fullPath } // 重定向到登录页，并保存原始目标路径
      });
    } else { // 用户已认证
      next(); // 继续导航
    }
  } else if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) { // 如果用户已认证，但尝试访问登录或注册页
    next({ name: 'Dashboard' }); // 重定向到仪表盘
  } else { // 对于不需要认证的路由，或者其他情况
    next(); // 总是确保调用 next()
  }
});

export default router