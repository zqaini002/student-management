import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'
// 导入sass配置
import sassConfig from './sass.js'

// 抑制Node.js弃用警告
process.noDeprecation = true;
// 设置Sass使用新API
process.env.SASS_API = 'new';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          whitespace: 'preserve',
        }
      }
    }),
    vueJsx(),
    createSvgIconsPlugin({
      // 指定需要缓存的图标文件夹
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
      // 指定symbolId格式
      symbolId: 'icon-[dir]-[name]',
    }),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        charset: false,
        // 使用新的Sass模块API语法
        additionalData: `@use "@/styles/variables.scss" as *;`,
        // 使用外部导入的sass配置
        sassOptions: sassConfig
        }
    },
    // 禁用遗留警告
    devSourcemap: false
  },
  server: {
    port: 8081,
    host: true,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // 不再重写路径，因为后端已经有/api前缀了
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    },
    historyApiFallback: true
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    // 生产环境移除console
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    // 增加警告限制，避免大块警告
    chunkSizeWarningLimit: 1000,
    // 改进分块策略
    rollupOptions: {
      output: {
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
        manualChunks: {
          // 将Element Plus单独打包
          'element-plus': ['element-plus'],
          // 将图标库单独打包
          'icons': ['@element-plus/icons-vue'],
          // 将核心依赖打包在一起
          'vue-core': ['vue', 'vue-router', 'pinia'],
          // 将工具库打包在一起
          'utils': ['axios', 'echarts', 'lodash-es'],
          // 其他依赖打包
          'vendor': [
            // ...可能的其他第三方库
          ]
        }
      }
    },
    // 提高构建性能
    sourcemap: false,
    // 启用浏览器兼容性
    target: 'es2015'
  },
  optimizeDeps: {
    // 预构建依赖项
    include: ['vue', 'vue-router', 'pinia', 'axios', '@element-plus/icons-vue'],
    // 强制预构建链接包
    force: true
  },
  // 添加错误处理
  clearScreen: false,
}) 