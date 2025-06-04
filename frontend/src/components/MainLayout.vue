<el-menu-item index="/dashboard">
  <el-icon><Monitor /></el-icon>
  <span>仪表盘</span>
</el-menu-item>

<!-- 只有管理员可见的菜单项 -->
<el-menu-item 
  v-if="isAdmin" 
  index="/user-management"
>
  <el-icon><User /></el-icon>
  <span>用户管理</span>
</el-menu-item> 