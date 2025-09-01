import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  //配置server（server指的是前端web服务器）
  server: {
    proxy: {
      //url请求 以/api 开始，就进行代理请求
      // /api/login  --->  http://localhost:8080/api/login
      '/api': {
        // 代理的目标，例如/api/login  --->  http://localhost:8080/api/login
        target: 'http://localhost:8081',
        //是否允许跨域，true表示允许跨域
        changeOrigin: true,
        //重写路径，去掉/api前缀，例如/api/login ---> /login
        rewrite: (path) => path.replace(/^\/api/, '') // 重写路径，去掉前缀/api
      },
      //图片请求后端代理方式解决跨域
      '/image': {
        // 代理的目标，例如/api/login  --->  http://localhost:8080/api/login
        target: 'http://localhost:8081',
        //是否允许跨域，true表示允许跨域
        changeOrigin: true,
        //重写路径，去掉/api前缀，例如/api/login ---> /login
        rewrite: (path) => path.replace(/^\/api/, '') // 重写路径，去掉前缀/api
      }
    }
  }
})
