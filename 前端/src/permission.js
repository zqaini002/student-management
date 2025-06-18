import router from '@/router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { getToken } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { ElMessage } from 'element-plus'
import { registerMessageRoutes } from '@/router'

// 在应用启动时优先注册消息路由
console.log('Permission.js - 初始化时注册消息路由')
registerMessageRoutes()

// 进度条配置
NProgress.configure({ showSpinner: false })

// 白名单路径，无需登录即可访问
const whiteList = ['/login', '/404']

// 是否已添加动态路由
let hasAddRoutes = false
// 添加一个标记来处理刷新问题
let isRefreshing = false

// 路由前置守卫
router.beforeEach(async (to, from, next) => {
  // 开始进度条
  NProgress.start()
  
  // 设置页面标题
  document.title = to.meta.title 
    ? `${to.meta.title} - EduSmart 高校智能管理平台` 
    : 'EduSmart 高校智能管理平台'
  
  // 获取Token
  const hasToken = getToken()
  const userStore = useUserStore()
  
  console.log('路由守卫 - 目标路径:', to.path)
  console.log('路由守卫 - 是否有Token:', hasToken)
  console.log('路由守卫 - 是否有用户信息:', userStore.userId ? '是' : '否')
  
  // 针对消息路由的特殊处理
  if ((to.path === '/message/notice' || to.path === '/message/todo') && hasToken) {
    console.log('检测到访问消息路由，确保路由已注册')
    // 确保消息路由已注册
    registerMessageRoutes()
  }
  
  // 已登录状态
  if (hasToken) {
    if (to.path === '/login') {
      // 如果已登录，重定向到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      const permissionStore = usePermissionStore()
      
      // 判断用户信息是否存在
      const hasUserInfo = userStore.userId && userStore.roles && userStore.roles.length > 0
      console.log('路由守卫 - 用户信息是否完整:', hasUserInfo)
      console.log('路由守卫 - 用户角色:', userStore.roles)
      
      if (hasUserInfo) {
        // 如果已有用户信息，检查路由是否已添加
        if (!hasAddRoutes || isRefreshing) {
          try {
            isRefreshing = false
            // 根据用户角色生成可访问路由
            const accessRoutes = await permissionStore.generateRoutes()
            console.log('路由守卫 - 动态路由生成完成:', accessRoutes)
            
            // 添加动态路由
            accessRoutes.forEach(route => {
              router.addRoute(route)
            })
            
            // 确保消息路由已正确添加
            registerMessageRoutes()
            
            hasAddRoutes = true
            
            // 强制刷新路由 - 使用 path 而非整个 to 对象，避免对象引用问题
            next({ path: to.path, replace: true })
          } catch (error) {
            console.error('路由生成错误:', error)
            userStore.resetState() // 重置用户状态
            next('/login')
          }
        } else {
          // 处理404问题：检查当前路由是否可以匹配
          if (to.matched.length === 0) {
            console.log('路由守卫 - 路由未匹配，尝试重新加载路由')
            // 如果路径看起来合法但未匹配，先重置路由状态
            hasAddRoutes = false
            isRefreshing = true
            
            // 特殊处理一些常见但未匹配到的路由
            const specialRoutes = ['/student/edit/', '/teacher/edit/', '/course/edit/']
            if (specialRoutes.some(route => to.path.includes(route))) {
              console.log(`路由守卫 - 检测到特殊路由: ${to.path}，确保路由加载正确`)
              // 确保消息路由已正确添加
              registerMessageRoutes()
            }
            
            // 重新生成动态路由
            const accessRoutes = await permissionStore.generateRoutes()
            accessRoutes.forEach(route => {
              router.addRoute(route)
            })
            
            // 确保消息路由已正确添加
            registerMessageRoutes()
            
            // 重试访问同一路径
            const redirectPath = to.path
            next({ path: redirectPath, replace: true })
          } else {
            console.log('路由守卫 - 正常访问:', to.path)
          next()
          }
        }
      } else {
        try {
          // 获取用户信息（包含角色）
          await userStore.getInfo()
          
          // 根据用户角色生成可访问路由
          const accessRoutes = await permissionStore.generateRoutes()
          
          // 添加动态路由
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })
          
          // 确保消息路由已正确添加
          registerMessageRoutes()
          
          hasAddRoutes = true
          
          // 强制刷新路由
          next({ path: to.path, replace: true })
        } catch (error) {
          // 获取用户信息失败
          console.error('获取用户信息失败:', error)
          
          // 清空Token并跳转到登录页
          userStore.resetState()
          
          ElMessage.error(error.message || '验证失败，请重新登录')
          
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    // 未登录状态
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接访问
      next()
    } else {
      // 不在白名单中，重定向到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

// 路由后置守卫
router.afterEach(() => {
  // 结束进度条
  NProgress.done()
}) 