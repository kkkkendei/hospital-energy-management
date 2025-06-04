<template>
  <div class="user-management">
    <h2>用户权限管理</h2>
    
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="180" />
      <el-table-column prop="email" label="邮箱" width="220" />
      <el-table-column label="权限" width="180">
        <template slot-scope="scope">
          {{ scope.row.enabled ? '管理员' : '普通用户' }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="!scope.row.enabled"
            type="primary"
            size="small"
            @click="updateUserRole(scope.row.id, true)"
          >
            升级为管理员
          </el-button>
          <el-button
            v-else
            type="warning"
            size="small"
            @click="updateUserRole(scope.row.id, false)"
          >
            降级为普通用户
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'UserManagement',
  data() {
    return {
      users: []
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get('/api/user-management/users');
        this.users = response.data;
      } catch (error) {
        Message.error('获取用户列表失败');
        console.error('Error fetching users:', error);
      }
    },
    async updateUserRole(userId, enabled) {
      try {
        await axios.put(`/api/user-management/users/${userId}/role?enabled=${enabled}`);
        Message.success('用户权限更新成功');
        this.fetchUsers(); // 刷新用户列表
      } catch (error) {
        Message.error('更新用户权限失败');
        console.error('Error updating user role:', error);
      }
    }
  }
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}
</style> 