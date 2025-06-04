import Vue from 'vue'
import VueRouter from 'vue-router'
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
const UserManagement = () => import(/* webpackChunkName: "user-management" */ '../views/UserManagement.vue');

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
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: UserManagement,
        // meta: { requiresAuth: true, requiresAdmin: true }
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
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userInfo = localStorage.getItem('user-info');
    if (!userInfo) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
      return;
    }

    // 如果需要管理员权限
    if (to.meta.requiresAdmin) {
      const user = JSON.parse(userInfo);
      if (!user.enabled) {
        next('/app/dashboard');
        return;
      }
    }
  }

  // 如果已登录用户访问登录页，重定向到仪表盘
  if ((to.path === '/login' || to.path === '/register') && localStorage.getItem('user-info')) {
    next('/app/dashboard');
    return;
  }

  next();
});

export default router