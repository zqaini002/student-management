/**
 * Dashboard组件导入测试
 * 用于验证组件能否被正确导入
 */

// 方式1: 直接导入
import DashboardDirect from './index.vue'
console.log('直接导入:', !!DashboardDirect)

// 方式2: 异步导入
const importAsync = () => import('./index.vue')
importAsync().then(module => {
  console.log('异步导入成功:', !!module)
}).catch(err => {
  console.error('异步导入失败:', err)
})

// 方式3: 使用vite glob导入
const modules = import.meta.glob('./*.vue')
console.log('Vite glob找到的模块:', Object.keys(modules))

if (modules['./index.vue']) {
  modules['./index.vue']().then(module => {
    console.log('Vite glob导入成功:', !!module)
  }).catch(err => {
    console.error('Vite glob导入失败:', err)
  })
} else {
  console.error('找不到./index.vue模块')
}

// 方式4: 相对路径查找
const allModules = import.meta.glob('../**/*.vue')
console.log('所有可用模块数量:', Object.keys(allModules).length)

// 查找dashboard相关组件
const dashboardModules = Object.keys(allModules).filter(key => 
  key.includes('/dashboard/')
)
console.log('Dashboard相关模块:', dashboardModules)

export default {
  name: 'DashboardImportTest'
} 