<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1']" router :default-active="$route.path">
        <el-menu-item index="/app/dashboard">
          <i class="el-icon-s-home"></i>
          <span>仪表盘</span>
        </el-menu-item>
        <el-submenu index="1">
          <template slot="title"><i class="el-icon-menu"></i>能源管理</template>
          <el-menu-item index="/app/energy-data-entry">数据录入</el-menu-item>
          <el-menu-item index="/app/energy-data">能源数据</el-menu-item>
          <el-menu-item index="/app/energy-analysis">能耗分析</el-menu-item>
        </el-submenu>
        <el-menu-item index="/app/devices">
          <i class="el-icon-setting"></i>
          <span>设备管理</span>
        </el-menu-item>
        <!-- 只有管理员可见的用户管理菜单 -->
        <el-menu-item v-if="isAdmin" index="/app/user-management">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between;">
        <span>医院能源管理系统</span>
        <div>
          <el-dropdown @command="handleCommand">
            <i class="el-icon-setting" style="margin-right: 15px"></i>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <span>{{ currentUser ? currentUser.username : '用户' }}</span>
        </div>
      </el-header>

      <el-main>
        <router-view /> <!-- 子路由的视图将在这里渲染 -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  name: 'MainLayout',
  computed: {
    ...mapState('auth', ['currentUser']),
    isAdmin() {
      return this.currentUser && this.currentUser.enabled === true;
    }
  },
  methods: {
    ...mapActions('auth', ['logout', 'fetchUser']),
    handleCommand(command) {
      if (command === 'logout') {
        this.logout().then(() => {
          this.$router.push('/login');
          this.$message.success('已成功退出登录');
        });
      } else if (command === 'profile') {
        // TODO: 跳转到用户个人信息页
        this.$message.info('功能待开发：个人信息');
      }
    }
  },
  created() {
    // 组件创建时尝试从localStorage恢复用户信息
    if (!this.currentUser) {
      this.fetchUser();
    }
  }
};
</script>

<style scoped>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
.el-menu {
  border-right: none; /* 移除默认的右边框 */
}
</style> 