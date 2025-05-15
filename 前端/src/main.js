import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入图标
import 'virtual:svg-icons-register'

// 引入样式
import './styles/index.scss'

import App from './App.vue'
import router from './router'

// 权限控制
import './permission'

// 全局指令
import directives from './directives'

import { registerGlobalComponents, getGlobalComponent } from '@/router/globalComponents'
import { getManualComponent } from '@/router/manualRoutes'
import { loadComponent } from '@/utils/importUtils'

// 创建应用
const app = createApp(App)

// 注册ElementPlus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 配置Pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// 配置路由
app.use(router)

// 配置ElementPlus
app.use(ElementPlus, {
    locale: zhCn,
    size: 'default'
})

// 注册全局指令
app.use(directives)

// 注册全局组件
registerGlobalComponents(app)

// 挂载统一加载函数到全局对象
window.__app_loadComponent = loadComponent

// 添加全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('全局错误捕获:')
  console.error('错误信息:', err)
  console.error('错误位置:', info)
  
  // 特别处理Failed to fetch动态导入错误
  if (err.message && err.message.includes('Failed to fetch dynamically imported module')) {
    console.error('组件动态导入失败，路径可能不正确:', err.message)
    
    // 尝试提取路径信息
    const pathMatch = err.message.match(/http:\/\/.*\/src\/views\/(.+)\?import/)
    if (pathMatch && pathMatch[1]) {
      const componentPath = pathMatch[1]
      console.error(`问题组件路径: ${componentPath}`)
      
      // 记录特殊处理的组件路径
      const specialPaths = ['student/add/index.vue', 'course/add/index.vue', 'teacher/add/index.vue']
      console.log(`如果这是${specialPaths.join('或')}，请检查全局组件注册或使用特殊处理`)
      
      // 检查是否有全局组件可用
      const normalizedPath = componentPath.replace(/\.vue$/, '')
      const globalComponent = getGlobalComponent(normalizedPath)
      if (globalComponent) {
        console.log(`找到全局组件注册: ${normalizedPath}`)
      }
      
      // 检查是否有手动映射的组件
      const manualComponent = getManualComponent(normalizedPath)
      if (manualComponent) {
        console.log(`找到手动映射组件: ${normalizedPath}`)
      }
      
      // 尝试使用统一加载器
      console.log('尝试使用统一加载器重新加载...')
      const path = normalizedPath.replace(/\/index$/, '')
      try {
        const dynamicLoader = loadComponent(path)
        console.log('统一加载器创建成功，可在路由配置中使用')
      } catch (loaderError) {
        console.error('统一加载器创建失败:', loaderError)
      }
    }
  }
}

// 添加路由调试
router.beforeEach((to, from, next) => {
  console.log('===全局路由守卫===')
  console.log('从:', from.path)
  console.log('到:', to.path)
  console.log('完整路由信息:', to)
  
  try {
    next()
  } catch (error) {
    console.error('路由守卫错误:', error)
    next(false)
  }
})

// 挂载应用
app.mount('#app') 