const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8083, // 前端开发服务器端口
    proxy: {
      '/api': { // 匹配所有以 /api 开头的请求
        target: 'http://localhost:8081', // 后端 Spring Boot 服务地址
        changeOrigin: true, // 是否改变源地址
         // pathRewrite: { '^/api': '' },
        logLevel: 'debug' // 添加代理日志级别
      }
    }
  }
})