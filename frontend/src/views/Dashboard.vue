<template>
  <div>
    <el-card>
      <div slot="header">
        <span>欢迎回来, {{ currentUser ? currentUser.username : 'wzy' }}!</span>
      </div>
      <p>这里是医院能源管理系统仪表盘。</p>
      <p>当前用户：{{ currentUser ? currentUser.username : 'wzy' }} ({{ currentUser && currentUser.enabled ? '管理员' : '管理员' }})</p>
      <!-- 根据需要在此处添加更多仪表盘内容 -->
      <div v-if="isAdmin">
        <p style="color: green;">您拥有管理员权限，可以进行用户管理等高级操作。</p>
        <!-- 管理员可见内容 -->
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'UserDashboard',
  computed: {
    ...mapState('auth', ['currentUser']),
    isAdmin() {
      return this.currentUser && this.currentUser.enabled === true;
    }
  }
};
</script>

<style scoped>
.el-card {
  margin: 20px;
}

p {
  margin: 10px 0;
  font-size: 16px;
  line-height: 1.5;
}
</style> 