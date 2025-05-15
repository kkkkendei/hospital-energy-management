<template>
  <el-container class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">医院能源管理系统 - 注册</h2>
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" @submit.native.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="el-icon-message"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="确认密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width:100%;">注册</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="goToLogin">已有账户？去登录</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon :closable="false"></el-alert>
      <el-alert v-if="successMessage" :title="successMessage" type="success" show-icon :closable="false"></el-alert>
    </el-card>
  </el-container>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'UserRegister',
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.registerForm.confirmPassword !== '') {
          this.$refs.registerFormRef.validateField('confirmPassword');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        password: [
          { validator: validatePass, trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePass2, trigger: 'blur' }
        ]
      },
      loading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  methods: {
    ...mapActions('auth', ['register']),
    handleRegister() {
      this.$refs.registerFormRef.validate(valid => {
        if (valid) {
          this.loading = true;
          this.errorMessage = '';
          this.successMessage = '';
          const userData = {
            username: this.registerForm.username,
            email: this.registerForm.email,
            password: this.registerForm.password,
            roles: ['user'] // 默认注册为普通用户
          };
          this.register(userData)
            .then(() => {
              this.loading = false;
              this.successMessage = '注册成功！请登录。';
              this.$message.success('注册成功！即将跳转到登录页...');
              setTimeout(() => {
                this.$router.push('/login');
              }, 2000);
            })
            .catch(error => {
              this.loading = false;
              this.errorMessage = (error.response && error.response.data && error.response.data.message) || '注册失败，请稍后重试。';
              this.$message.error(this.errorMessage);
            });
        } else {
          return false;
        }
      });
    },
    goToLogin() {
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.register-card {
  width: 450px;
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
}
</style> 