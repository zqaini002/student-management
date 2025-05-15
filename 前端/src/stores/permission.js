import { defineStore } from 'pinia'
import { constantRoutes, asyncRoutes } from '@/router'
import { useUserStore } from './user'
import { getRoutes } from '@/api/user'
import Layout from '@/layout/index.vue'
import { h } from 'vue'
import { componentPathMap, getComponentPath, findMatchingComponent } from './componentPathMap'
import { findComponentModule, loadAndDebugComponents } from '@/utils/importUtils'

/**
 * 使用meta.roles判断当前用户是否有权限访问
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    // 如果没有设置roles，默认所有人可访问
    return true
  }
}

/**
 * 根据用户角色过滤可访问路由
 * @param routes 静态路由表
 * @param roles 用户角色
 */
function filterAsyncRoutes(routes, roles) {
  const res = []
  
  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })
  
  return res
}

/**
 * 将后端菜单数据转换为路由格式
 * @param menus 后端菜单数据
 */
function convertMenuToRoutes(menus) {
  // 创建一个临时组件用于替代缺失的页面
  const TempComponent = {
    render() {
      return h('div', { style: { padding: '20px' } }, [
        h('h2', '页面开发中...'),
        h('p', '该功能模块正在开发中，敬请期待！'),
        h('pre', { style: { color: 'blue', textAlign: 'left' } }, '调试信息将显示在控制台')
      ])
    }
  }
  
  // 预加载所有组件并进行调试
  const viewModules = loadAndDebugComponents()
  
  // 按父ID分组
  const menuMap = {}
  menus.forEach(menu => {
    if (!menuMap[menu.parentId]) {
      menuMap[menu.parentId] = []
    }
    menuMap[menu.parentId].push(menu)
  })
  
  // 递归构建路由
  function buildRoutes(parentId) {
    const routes = []
    const children = menuMap[parentId]
    
    if (!children) {
      return routes
    }
    
    children.forEach(menu => {
      // 只处理目录和菜单类型, 按钮类型跳过
      if (menu.menuType === 'F') {
        return
      }
      
      // 隐藏菜单不处理
      if (menu.visible === 1) {
        return
      }
      
      // 确保路径以/开头
      const path = menu.path || ''
      const normalizedPath = path.startsWith('/') ? path : `/${path}`
      
      const route = {
        path: normalizedPath,
        name: normalizedPath ? normalizedPath.replace(/\//g, '-').slice(1) : '',
        component: null,
        meta: {
          title: menu.menuName,
          icon: menu.icon || '',
          hidden: menu.visible === 1,
          keepAlive: menu.isCache === 0
        }
      }
      
      // 目录类型
      if (menu.menuType === 'M') {
        route.component = Layout
        
        // 获取子路由
        const childRoutes = buildRoutes(menu.id)
        if (childRoutes.length > 0) {
          route.children = childRoutes
          
          // 如果没有设置重定向，默认重定向到第一个子路由
          if (!route.redirect) {
            route.redirect = childRoutes[0].path
          }
        }
      }
      // 菜单类型
      else if (menu.menuType === 'C') {
        // 如果有组件路径，则动态导入组件
        if (menu.component) {
          // 判断是否为Layout组件
          if (menu.component === 'Layout') {
            route.component = Layout
          } else {
            console.log(`[路由生成] 处理菜单: ${menu.menuName}, ID: ${menu.id}, 路径: ${normalizedPath}, 组件: ${menu.component}`)
            
            // 核心改进：直接导入对应组件
            // 关键修复：使用import()动态导入方式代替import.meta.glob的静态导入映射
            try {
              // 标准化路径，确保一致性
              const componentPath = getComponentPath(menu.component)
              
              // 查找一个确切的组件模块
              const componentModule = findComponentModule(componentPath)
              
              if (componentModule) {
                route.component = componentModule
                console.log(`[路由生成] 成功加载组件: ${menu.menuName} -> ${componentPath}`)
              } else {
                // 尝试按照明确的路径规则加载
                const explicitPath = `../views/${componentPath}.vue`
                
                // 使用动态导入方式加载组件
                route.component = () => import(/* @vite-ignore */ explicitPath)
                  .then(module => {
                    console.log(`[路由生成] 动态导入组件成功: ${explicitPath}`)
                    return module
                  })
                  .catch(error => {
                    console.error(`[路由生成] 动态导入组件失败: ${explicitPath}`, error)
                    return { 
                      render() { 
                        return h('div', [
                          h('h2', '组件加载失败'),
                          h('p', `无法加载组件: ${componentPath}`),
                          h('pre', { style: { color: 'red' } }, `错误: ${error.message}`)
                        ])
                      } 
                    }
                  })
              }
            } catch (error) {
              console.error(`[路由生成] 加载组件错误: ${menu.component}`, error)
              route.component = TempComponent
            }
          }
        } else {
          console.warn(`[路由生成] 菜单[${menu.menuName}]没有设置组件路径, 使用临时组件`)
          route.component = TempComponent
        }
      }
      
      routes.push(route)
    })
    
    return routes
  }

  // 从静态路由配置中查找组件
  function findComponentFromStaticRoutes(path) {
    // 在asyncRoutes中查找匹配的路由
    for (const route of asyncRoutes) {
      if (route.path === path && route.component) {
        return route.component
      }
      
      if (route.children) {
        for (const child of route.children) {
          const childFullPath = `${route.path}/${child.path}`.replace(/\/+/g, '/')
          if (childFullPath === path && child.component) {
            return child.component
          }
        }
      }
    }
    return null
  }
  
  // 从根节点开始构建
  const routes = buildRoutes(0)
  
  // 深度检查所有路由路径，确保都以/开头
  function ensureValidPaths(routeList) {
    routeList.forEach(route => {
      if (route.path && !route.path.startsWith('/')) {
        route.path = `/${route.path}`
      }
      
      if (route.redirect && typeof route.redirect === 'string' && !route.redirect.startsWith('/')) {
        route.redirect = `/${route.redirect}`
      }
      
      if (route.children && route.children.length > 0) {
        ensureValidPaths(route.children)
      }
    })
  }
  
  ensureValidPaths(routes)
  return routes
}

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: []
  }),
  getters: {
    // 获取路由
    getRoutes: (state) => state.routes,
    // 获取添加的路由
    getAddRoutes: (state) => state.addRoutes
  },
  actions: {
    // 生成路由
    async generateRoutes() {
      const userStore = useUserStore()
      const roles = userStore.roles
      
      console.log('权限管理 - 开始生成路由, 用户角色:', roles)
      
      try {
        // 从后端获取菜单
        const response = await getRoutes()
        console.log('权限管理 - 从后端获取菜单数据:', response.data)
        
        let accessedRoutes = []
        
        if (response.data && Array.isArray(response.data)) {
          // 将后端菜单转换为路由格式
          const dynamicRoutes = convertMenuToRoutes(response.data)
          console.log('权限管理 - 转换后的路由:', dynamicRoutes)
          
          // 根据角色过滤路由
          accessedRoutes = filterAsyncRoutes(dynamicRoutes, roles)
        } else {
          // 如果后端接口未响应正确数据，则使用前端静态路由
          console.warn('权限管理 - 后端未返回有效菜单数据，使用前端静态路由')
          accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
        }
        
        this.addRoutes = accessedRoutes
        this.routes = constantRoutes.concat(accessedRoutes)
        
        return accessedRoutes
      } catch (error) {
        console.error('权限管理 - 获取菜单失败:', error)
        // 发生错误时，降级使用前端静态路由
        console.warn('权限管理 - 降级使用前端静态路由')
        const accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
        
        this.addRoutes = accessedRoutes
        this.routes = constantRoutes.concat(accessedRoutes)
        
        return accessedRoutes
      }
    },
    
    // 重置路由
    resetRoutes() {
      this.routes = []
      this.addRoutes = []
    }
  },
  persist: {
    enabled: false  // 路由状态不需要持久化
  }
}) 