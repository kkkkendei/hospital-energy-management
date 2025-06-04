<template>
  <el-container class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">医院能源管理系统 - 登录</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit.native.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width:100%;">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="goToRegister">没有账户？去注册</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon :closable="false"></el-alert>
    </el-card>
  </el-container>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'UserLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false,
      errorMessage: ''
    };
  },
  methods: {
    ...mapActions('auth', ['login']),
    handleLogin() {
      this.$refs.loginFormRef.validate(valid => {
        if (valid) {
          this.loading = true;
          this.errorMessage = '';
          this.login(this.loginForm)
            .then((user) => {
              this.loading = false;
              this.$message.success(`欢迎回来，${user.username}！`);
              // 保存用户信息到localStorage
              localStorage.setItem('user-info', JSON.stringify(user));
              // 根据角色或其他逻辑跳转到不同页面
              const redirectPath = this.$route.query.redirect || '/app/dashboard';
              this.$router.push(redirectPath);
            })
            .catch(error => {
              this.loading = false;
              this.errorMessage = (error.response && error.response.data && error.response.data.message) || '登录失败，请检查您的凭据或网络连接。';
              this.$message.error(this.errorMessage);
            });
        } else {
          return false;
        }
      });
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5; /* Optional: Add a background color */
}

.login-card {
  width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
}
</style> 