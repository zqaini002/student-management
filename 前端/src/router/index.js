import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { getToken, removeToken } from '@/utils/auth'
import { h } from 'vue'
import { ElMessage } from 'element-plus'
import { loadComponent } from '@/utils/importUtils'

/**
 * 路由配置
 * meta: {
 *  title: '标题',
 *  icon: '图标',
 *  hidden: true/false,
 *  permissions: ['permission1', 'permission2'],
 *  roles: ['admin', 'common'],
 *  keepAlive: true/false,
 *  activeMenu: '/path'
 * }
 */

// 创建一个临时组件用于替代缺失的页面
const TempComponent = {
  render() {
    return h('div', { style: { padding: '20px' } }, [
      h('h2', '页面开发中...'),
      h('p', '该功能模块正在开发中，敬请期待！')
    ])
  }
}

/**
 * 统一组件加载处理
 * @param {string} path 组件路径
 * @returns {Function} 组件加载函数
 */
function loadView(path) {
  // 使用统一加载器，以支持多种目录结构
  return loadComponent(path);
}

/**
 * 静态路由
 * 所有用户都可以访问的路由
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: loadView('redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: loadView('login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: loadView('error/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: loadView('dashboard'),
        meta: { title: '首页', icon: 'HomeFilled', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        name: 'Profile',
        component: loadView('profile/index'),
        meta: { title: '个人中心' }
      }
    ]
  },
  // 添加课程列表路由
  {
    path: '/course',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'CourseList',
        component: loadView('course/index'),
        meta: { title: '课程列表' }
      }
    ],
    hidden: true
  },
  // 添加课程新增路由
  {
    path: '/course-add',
    component: Layout,
    children: [
      {
        path: '',
        name: 'CourseAdd',
        component: loadView('course/add'),
        meta: { title: '新增课程' }
      }
    ],
    hidden: true
  },
  // 添加一个通用错误页
  {
    path: '/error',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'forbidden',
        name: 'Forbidden',
        component: loadView('error/403'),
        meta: { title: '无权访问' }
      }
    ]
  },
  // 404页面 - 放在最后，捕获所有未匹配的路由
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: loadView('error/404'),
    hidden: true
  }
]

/**
 * 动态路由
 * 根据用户角色动态分配的路由
 */
export const asyncRoutes = [
  // 教务管理
  {
    path: '/education',
    component: Layout,
    redirect: '/education/department',
    meta: { title: '教务管理', icon: 'School', roles: ['admin'] },
    children: [
      {
        path: 'department',
        name: 'DepartmentList',
        component: loadView('education/department/index'),
        meta: { title: '院系管理', icon: 'OfficeBuilding', roles: ['admin'] }
      },
      {
        path: 'major',
        name: 'MajorList',
        component: loadView('education/major/index'),
        meta: { title: '专业管理', icon: 'Memo', roles: ['admin'] }
      },
      {
        path: 'class',
        name: 'ClassList',
        component: loadView('education/class/index'),
        meta: { title: '班级管理', icon: 'House', roles: ['admin'] }
      }
    ]
  },
  
  // 学生管理
  {
    path: '/student',
    component: Layout,
    redirect: '/student/list',
    meta: { title: '学生管理', icon: 'User', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'list',
        name: 'StudentList',
        component: loadView('student/index'),
        meta: { title: '学生列表', icon: 'List' },
        beforeEnter: (to, from, next) => {
          console.log('----------学生列表路由钩子开始-----------');
          console.log('尝试进入学生列表路由', to.path);
          console.log('路由元数据:', to.meta);
          // 简化这部分代码，直接进入
          console.log('学生列表路由守卫通过，允许访问');
          next();
          console.log('----------学生列表路由钩子结束-----------');
        }
      },
      {
        path: 'add',
        name: 'StudentAdd',
        component: loadView('student/add'),
        meta: { title: '新增学生', icon: 'Plus' }
      },
      {
        path: 'detail/:id',
        name: 'StudentDetail',
        component: TempComponent,
        meta: { title: '学生详情', activeMenu: '/student/list' },
        hidden: true
      },
      {
        path: 'edit/:id',
        name: 'StudentEdit',
        component: loadView('student/edit'),
        meta: { title: '编辑学生', activeMenu: '/student/list' },
        hidden: true,
        alias: '/student/edit/:id'
      }
    ]
  },
  
  // 教师管理
  {
    path: '/teacher',
    component: Layout,
    redirect: '/teacher/list',
    meta: { title: '教师管理', icon: 'UserFilled', roles: ['admin'] },
    children: [
      {
        path: 'list',
        name: 'TeacherList',
        component: loadView('teacher/index'),
        meta: { title: '教师列表', icon: 'List' }
      },
      {
        path: 'add',
        name: 'TeacherAdd',
        component: loadView('teacher/add'),
        meta: { title: '新增教师', icon: 'Plus' }
      },
      {
        path: 'detail/:id',
        name: 'TeacherDetail',
        component: loadView('teacher/detail'),
        meta: { title: '教师详情', activeMenu: '/teacher/list' },
        hidden: true
      },
      {
        path: 'edit/:id',
        name: 'TeacherEdit',
        component: loadView('teacher/edit'),
        meta: { title: '编辑教师', activeMenu: '/teacher/list' },
        hidden: true
      }
    ]
  },
  
  // 课程管理
  {
    path: '/course',
    component: Layout,
    redirect: '/course/list',
    meta: { title: '课程管理', icon: 'Notebook', roles: ['admin', 'teacher'] },
    children: [
      {
        path: 'list',
        name: 'CourseList',
        component: loadView('course/index'),
        meta: { title: '课程列表', icon: 'List' }
      },
      {
        path: 'add',
        name: 'CourseAdd',
        component: loadView('course/add'),
        meta: { title: '新增课程', icon: 'Plus', roles: ['admin'] }
      },
      {
        path: 'detail/:id',
        name: 'CourseDetail',
        component: TempComponent,
        meta: { title: '课程详情', activeMenu: '/course/list' },
        hidden: true
      },
      {
        path: 'students/:id',
        name: 'CourseStudents',
        component: TempComponent,
        meta: { title: '课程学生', activeMenu: '/course/list' },
        hidden: true
      },
      {
        path: 'offering',
        name: 'CourseOfferingList',
        component: loadView('course/offering/index'),
        meta: { title: '课程开设记录', icon: 'Notebook', roles: ['admin', 'teacher'] }
      },
      {
        path: '',
        redirect: 'list'
      }
    ]
  },
  
  // 选课管理
  {
    path: '/selection',
    component: Layout,
    redirect: '/selection/list',
    meta: { title: '选课管理', icon: 'Calendar', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'my-courses',
        name: 'MyCoursesStudent',
        component: TempComponent,
        meta: { title: '我的课程', icon: 'Notebook', roles: ['student'] }
      },
      {
        path: 'select',
        name: 'CourseSelection',
        component: TempComponent,
        meta: { title: '选课', icon: 'Plus', roles: ['student'] }
      },
      {
        path: 'teaching',
        name: 'TeachingCourses',
        component: loadView('course/teaching/index'),
        meta: { title: '授课列表', icon: 'Reading', roles: ['teacher'] }
      },
      {
        path: 'teaching/:id/:courseId',
        name: 'TeacherCourseStudents',
        component: loadView('course/teaching/students'),
        meta: { title: '课程学生', activeMenu: '/selection/teaching', roles: ['teacher'] },
        hidden: true
      },
      {
        path: 'manage',
        name: 'SelectionManage',
        component: loadView('selection/manage'),
        meta: { title: '选课管理', icon: 'Setting', roles: ['admin'] }
      }
    ]
  },
  
  // 成绩管理
  {
    path: '/grade',
    component: Layout,
    redirect: '/grade/my',
    meta: { title: '成绩管理', icon: 'Document', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'my',
        name: 'MyGrades',
        component: TempComponent,
        meta: { title: '我的成绩', icon: 'Search', roles: ['student'] }
      },
      {
        path: 'input',
        name: 'GradeInput',
        component: loadView('grade/input/index'),
        meta: { title: '成绩录入', icon: 'Edit', roles: ['teacher'] }
      },
      {
        path: 'check',
        name: 'GradeCheck',
        component: loadView('grade/check'),
        meta: { title: '成绩查询', icon: 'Search', roles: ['admin', 'teacher'] }
      },
      {
        path: 'stats',
        name: 'GradeStats',
        component: loadView('grade/stats'),
        meta: { title: '成绩统计', icon: 'DataAnalysis', roles: ['admin', 'teacher'] }
      }
    ]
  },
  
  // 考勤管理
  {
    path: '/attendance',
    component: Layout,
    redirect: '/attendance/record',
    meta: { title: '考勤管理', icon: 'Clock', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'record',
        name: 'AttendanceRecord',
        component: loadView('attendance/record/index'),
        meta: { title: '考勤记录', icon: 'Document', roles: ['admin', 'teacher'], perms: ['attendance:record:list'] }
      },
      {
        path: 'stats',
        name: 'AttendanceStats',
        component: loadView('attendance/stats/index'),
        meta: { title: '考勤统计', icon: 'DataAnalysis', roles: ['admin', 'teacher'], perms: ['attendance:stats:list'] }
      },
      {
        path: 'my',
        name: 'MyAttendance',
        component: loadView('attendance/my/index'),
        meta: { title: '我的考勤', icon: 'Search', roles: ['student'], perms: ['attendance:record:list'] }
      }
    ]
  },
  
  // 消息管理
  {
    path: '/message',
    component: Layout,
    redirect: '/message/notice',
    meta: { title: '消息管理', icon: 'MessageBox', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'notice',
        name: 'NoticeList',
        component: loadView('message/notice/index'),
        meta: { title: '公告管理', icon: 'Bell', roles: ['admin', 'teacher', 'student'] }
      },
      {
        path: 'todo',
        name: 'TodoList',
        component: loadView('message/todo/index'),
        meta: { title: '待办事项', icon: 'List', roles: ['admin', 'teacher', 'student'] }
      }
    ]
  },
  
  // 系统管理
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting', roles: ['admin'] },
    children: [
      {
        path: 'user',
        name: 'UserManage',
        component: loadView('system/user/index'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'RoleManage',
        component: loadView('system/role/index'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'menu',
        name: 'MenuManage',
        component: loadView('system/menu/index'),
        meta: { title: '菜单管理', icon: 'Menu' }
      }
    ]
  },
  
  // 404页面必须放在最后
  // { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true }  // 移除这一行，避免重复定义
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(''),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 }) // 切换路由时滚动到顶部
})

// 白名单路径，不需要重定向到登录页面
const whiteList = ['/login']

// 记录动态路由是否已经添加
let dynamicRoutesAdded = false

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  console.log('-------路由守卫开始-------')
  console.log('从路由:', from.path, '跳转到:', to.path)
  console.log('目标路由完整信息:', to)
  
  // 特殊处理课程列表路由
  if (to.path === '/course/list' && to.name !== 'CourseList') {
    console.log('检测到访问/course/list路径，进行特殊处理')
    // 标准化路由，确保能找到对应组件
    return next({ 
      path: '/course',
      name: 'CourseList',
      replace: true
    })
  }
  
  // 获取Token
  const hasToken = getToken()
  console.log('是否有Token:', hasToken ? '是' : '否')
  
  if (hasToken) {
    if (to.path === '/login') {
      // 如果已登录，重定向到首页
      console.log('已登录状态下访问登录页，重定向到首页')
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      const permissionStore = usePermissionStore()
      
      try {
        // 如果没有用户角色信息，先获取用户信息
        if (userStore.roles.length === 0) {
          console.log('用户角色信息为空，开始获取用户信息')
          // 获取用户信息
          await userStore.getInfo()
          console.log('获取用户信息成功，角色:', userStore.roles)
          
          // 根据用户角色权限生成可访问路由
          const accessRoutes = await permissionStore.generateRoutes()
          console.log('生成的可访问路由数量:', accessRoutes.length)
          
          // 动态添加路由
          accessRoutes.forEach(route => {
            console.log('动态添加路由:', route.path)
            router.addRoute(route)
          })
          
          dynamicRoutesAdded = true
          console.log('已完成动态路由添加，重新导航到:', to.path)
          
          // 重新导航到目标页面，确保路由已添加
          return next({ ...to, replace: true })
        }
        
        // 判断是否有访问权限
        if (to.matched.length === 0) {
          console.log('未找到匹配的路由，路径:', to.path)
          // 如果未找到匹配的路由，可能是路由未添加或没有权限
          if (!dynamicRoutesAdded) {
            console.log('动态路由未添加，重新生成路由')
            // 如果动态路由未添加，重新生成路由
            const accessRoutes = await permissionStore.generateRoutes()
            console.log('重新生成的可访问路由数量:', accessRoutes.length)
            
            accessRoutes.forEach(route => {
              console.log('重新添加路由:', route.path)
              router.addRoute(route)
            })
            
            dynamicRoutesAdded = true
            console.log('完成路由重新添加，重新导航')
            // 使用replace模式重新导航到当前路径
            return next({ ...to, replace: true })
          } else {
            // 防止直接跳转到404，添加重试逻辑
            console.log('动态路由已添加但未匹配，尝试延迟重试')
            
            // 检查路径是否为Tab关闭操作，通常这些路径会有特定格式
            const isTabOperation = to.path.includes('/redirect') || 
                                  to.fullPath.includes('redirect=') ||
                                  to.fullPath.includes('closeTab=');
                                  
            if (isTabOperation) {
              console.log('检测到可能是标签关闭操作，尝试导航至首页');
              return next('/dashboard');
            }
            
            // 检查是否在asyncRoutes中有匹配的路由，但可能是子路由未正确注册
            const findMatchRoute = (path) => {
              for (const route of accessRoutes) {
                if (path.startsWith(route.path)) {
                  return route.redirect || route.path;
                }
                
                // 检查子路由
                if (route.children) {
                  for (const child of route.children) {
                    const fullPath = route.path + '/' + child.path;
                    if (path.startsWith(fullPath)) {
                      return fullPath;
                    }
                  }
                }
              }
              return null;
            };
            
            const possibleRoute = findMatchRoute(to.path);
            if (possibleRoute) {
              console.log('找到可能的匹配路由:', possibleRoute);
              return next(possibleRoute);
            }
            
            // 都无法匹配，跳转到首页而不是404
            console.log('无法找到匹配路由，跳转到首页');
            return next('/dashboard');
          }
        }
        
        console.log('路由匹配成功，匹配的路由数:', to.matched.length)
        console.log('匹配的路由:', to.matched.map(m => m.path))
        
        // 检查用户权限
        if (to.meta && to.meta.roles && to.meta.roles.length > 0) {
          console.log('当前路由需要角色权限:', to.meta.roles)
          console.log('用户拥有角色:', userStore.roles)
          const hasRole = userStore.roles.some(role => to.meta.roles.includes(role))
          if (!hasRole) {
            console.log('用户无权访问此页面，重定向到403页面')
            return next('/error/forbidden')
          }
        }
        
        // 路由匹配成功，放行
        console.log('路由守卫检查通过，允许访问:', to.path)
        next()
      } catch (error) {
        console.error('路由守卫出错:', error)
        
        // 清除用户状态并重新登录
        userStore.resetState()
        permissionStore.resetRoutes()
        dynamicRoutesAdded = false
        
        ElMessage.error('登录状态已失效，请重新登录')
        next(`/login?redirect=${to.path}`)
      }
    }
  } else {
    // 没有Token
    console.log('未检测到Token')
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接访问
      console.log('目标路径在白名单中，直接访问')
      next()
    } else {
      // 没有访问权限，重定向到登录页面
      console.log('未登录，重定向到登录页')
      next(`/login?redirect=${to.path}`)
    }
  }
  console.log('-------路由守卫结束-------')
})

// 全局后置钩子
router.afterEach(() => {
  // 处理页面加载状态或者分析等
})

/**
 * 手动注册特定路由
 * 用于确保特定路由被正确注册
 */
export function registerMessageRoutes() {
  console.log('开始手动注册消息相关路由...')
  
  // 先确保没有重复路由
  const existingRoutes = router.getRoutes()
  const messageRouteExists = existingRoutes.some(route => route.path === '/message')
  
  if (messageRouteExists) {
    console.log('已存在消息路由，先移除...')
    // 移除现有的路由
    router.removeRoute('NoticeList')
    router.removeRoute('TodoList')
    const parentRoute = router.removeRoute('MessageManage')
    console.log('移除结果:', parentRoute ? '成功' : '失败或不存在')
  }
  
  // 创建消息管理路由
  const messageRoute = {
    path: '/message',
    name: 'MessageManage',
    component: Layout,
    redirect: '/message/notice',
    meta: { title: '消息管理', icon: 'MessageBox', roles: ['admin', 'teacher', 'student'] },
    children: [
      {
        path: 'notice',  // 相对路径，不要以斜杠开头
        name: 'NoticeList',
        component: loadView('message/notice/index'),
        meta: { title: '公告管理', icon: 'Bell', roles: ['admin', 'teacher', 'student'] }
      },
      {
        path: 'todo',  // 相对路径，不要以斜杠开头
        name: 'TodoList',
        component: loadView('message/todo/index'),
        meta: { title: '待办事项', icon: 'List', roles: ['admin', 'teacher', 'student'] }
      }
    ]
  }
  
  // 添加路由
  console.log('正在添加消息路由:', messageRoute)
  router.addRoute(messageRoute)
  
  // 验证是否添加成功
  const routesAfterAdd = router.getRoutes()
  const messageRouteAdded = routesAfterAdd.some(route => route.path === '/message')
  console.log('消息路由添加是否成功:', messageRouteAdded ? '是' : '否')
  
  // 打印所有路由路径，检查是否存在
  console.log('当前所有路由路径:', 
    router.getRoutes().map(route => ({ 
      path: route.path, 
      name: route.name,
      childrenCount: route.children ? route.children.length : 0 
    }))
  )
  
  return messageRouteAdded
}

/**
 * 重置路由
 */
export function resetRouter() {
  const newRouter = createRouter({
    history: createWebHistory(),
    routes: constantRoutes,
    scrollBehavior: () => ({ top: 0 })
  })
  
  // 替换原有路由
  router.matcher = newRouter.matcher
  
  // 调用清除缓存函数
  clearRouteCache()
  
  console.log('路由已重置')
}

/**
 * 清除路由缓存
 */
export function clearRouteCache() {
  // 获取所有已加载的路由缓存
  const routerCache = window?.__vueRouter__?._routes || []
  
  // 清空缓存
  if (routerCache.length > 0) {
    console.log('清理路由缓存:', routerCache.length)
    routerCache.length = 0
  }
  
  // 清除其他可能的缓存
  if (window.__vite_plugin_vue_router_cache__) {
    window.__vite_plugin_vue_router_cache__ = {}
  }
  
  console.log('路由缓存已清理')
}

/**
 * 强制刷新当前路由
 * 适用于调试和解决路由不匹配问题
 * @param {string} targetPath 目标路径，不指定则刷新当前路径
 */
export function forceRefreshRoute(targetPath = null) {
  const currentPath = router.currentRoute.value.path
  console.log(`尝试强制刷新路由，当前路径：${currentPath}, 目标路径：${targetPath || '不变'}`)
  
  // 确保动态路由已添加
  const permissionStore = usePermissionStore()
  const accessRoutes = permissionStore.generateRoutes()
  console.log(`重新生成的可访问路由数量: ${accessRoutes.length}`)
  
  // 移除所有动态路由
  accessRoutes.forEach(route => {
    console.log(`准备添加路由：${route.path}`)
    try {
      router.addRoute(route)
    } catch (error) {
      console.error(`添加路由 ${route.path} 失败:`, error)
    }
  })

  // 获取所有已定义路由，用于调试
  console.log('所有定义的路由路径:', 
    router.getRoutes().map(route => route.path)
  )

  // 刷新路由
  dynamicRoutesAdded = true
  const path = targetPath || currentPath
  console.log(`即将导航到: ${path}`)

  // 使用replace避免在历史记录中新增记录
  router.replace({ path: '/redirect' + path })
}

export default router 