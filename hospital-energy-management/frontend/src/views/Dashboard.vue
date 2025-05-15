<template>
  <div>
    <el-card>
      <div slot="header">
        <span>欢迎回来, {{ currentUser ? currentUser.username : '用户' }}!</span>
      </div>
      <p>这里是医院能源管理系统仪表盘。</p>
      <p>您当前的角色是: {{ currentUser && currentUser.role ? currentUser.role : (userRole ? userRole : '未知') }}</p>
      <!-- 根据需要在此处添加更多仪表盘内容 -->
      <div v-if="isAdmin">
        <p style="color: green;">您拥有管理员权限。</p>
        <!-- 管理员可见内容 -->
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex';

export default {
  name: 'UserDashboard',
  computed: {
    ...mapState('auth', ['currentUser']),
    ...mapGetters('auth', ['userRole']),
    isAdmin() {
      // 直接从 currentUser.roles (如果后端返回的是数组且包含角色字面量)
      // 或者从 userRole getter (如果getter返回单个主要角色字符串)
      // 此处我们假设 userRole 返回的是单个角色字符串，例如 "ROLE_ADMIN"
      return this.userRole === 'ROLE_ADMIN' || (this.currentUser && this.currentUser.roles && this.currentUser.roles.includes('ROLE_ADMIN'));
    }
  }
};
</script>

<style scoped>
/* 根据需要添加样式 */
</style> 