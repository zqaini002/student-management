/**
 * 组件路径映射表
 * 用于处理数据库中的component字段与实际组件路径不一致的情况
 */
export const componentPathMap = {
  // 系统管理
  'system/user/index': 'system/user/index',
  'system/role/index': 'system/role/index',
  'system/menu/index': 'system/menu/index',
  
  // 教务管理
  'education/department/index': 'education/department/index',
  'education/major/index': 'education/major/index',
  'education/class/index': 'education/class/index',
  
  // 学生管理
  'student/index': 'student/index',
  'student/add': 'student/add',
  'student/edit': 'student/edit',
  
  // 教师管理
  'teacher/index': 'teacher/index',
  'teacher/add': 'teacher/add',
  'teacher/edit': 'teacher/edit',
  'teacher/detail': 'teacher/detail',
  
  // 课程管理
  'course/index': 'course/index',
  'course/add': 'course/add',
  'course/offering/index': 'course/offering/index',
  'course/list': 'course/index', // 兼容旧路径
  
  // 考勤管理
  'attendance/record/index': 'attendance/record/index',
  'attendance/stats/index': 'attendance/stats/index',
  'attendance/my/index': 'attendance/my/index',
  
  // 选课管理
  'selection/manage': 'selection/manage',
  'course/selection/index': 'course/selection/index',
  'course/my-courses/index': 'course/my-courses/index',
  'course/teaching/index': 'course/teaching/index',
  
  // 成绩管理
  'grade/input/index': 'grade/input/index',
  'grade/check': 'grade/check',
  'grade/stats': 'grade/stats',
  
  // 特殊处理不规则路径
  'course/selection': 'course/selection/index',
  'my-courses': 'course/my-courses/index',
  'teaching': 'course/teaching/index',
}

/**
 * 获取组件路径
 * @param {string} path 数据库中的组件路径
 * @returns {string} 实际组件路径
 */
export function getComponentPath(path) {
  if (!path) return null
  
  // 去掉.vue后缀
  let componentPath = path
  if (componentPath.endsWith('.vue')) {
    componentPath = componentPath.slice(0, -4)
  }
  
  // 从映射表获取
  return componentPathMap[componentPath] || componentPath
}

/**
 * 检查组件是否存在于导入的模块中
 * @param {string} componentPath 组件路径
 * @param {object} viewModules import.meta.glob加载的模块
 * @returns {string|null} 匹配的模块路径或null
 */
export function findMatchingComponent(componentPath, viewModules) {
  console.log(`findMatchingComponent - 开始查找组件: ${componentPath}`)
  
  // 处理路径分隔符，确保一致性
  const normalizedComponentPath = componentPath.replace(/\\/g, '/')
  
  // 可能的路径格式
  const possiblePaths = [
    normalizedComponentPath,
    `${normalizedComponentPath}.vue`,
    `${normalizedComponentPath}/index.vue`,
    `/src/views/${normalizedComponentPath}.vue`,
    `/src/views/${normalizedComponentPath}/index.vue`,
    `@/views/${normalizedComponentPath}.vue`,
    `@/views/${normalizedComponentPath}/index.vue`,
    // 添加更多可能的格式
    normalizedComponentPath.replace(/^\//, ''),
    normalizedComponentPath.replace(/\.vue$/, ''),
    `views/${normalizedComponentPath}`,
    `views/${normalizedComponentPath}.vue`
  ]
  
  console.log(`findMatchingComponent - 尝试匹配以下路径:`, possiblePaths)
  
  // 先尝试直接匹配
  const moduleKeys = Object.keys(viewModules)
  for (const key of moduleKeys) {
    const cleanKey = key.toLowerCase().replace(/\\/g, '/')
    const cleanPath = normalizedComponentPath.toLowerCase().replace(/\\/g, '/')
    
    if (cleanKey.endsWith(`/${cleanPath}.vue`) || 
        cleanKey.endsWith(`/${cleanPath}/index.vue`)) {
      console.log(`findMatchingComponent - 直接匹配成功: ${key}`)
      return key
    }
  }
  
  // 然后尝试各种格式的路径
  for (const key of moduleKeys) {
    const cleanKey = key.toLowerCase().replace(/\\/g, '/')
    
    for (const possiblePath of possiblePaths) {
      const cleanPath = possiblePath.toLowerCase().replace(/\\/g, '/')
      
      // 不同匹配策略
      if (cleanKey.includes(cleanPath) || 
          cleanKey.endsWith(cleanPath) ||
          cleanKey.includes(`/${normalizedComponentPath.toLowerCase()}.vue`) ||
          cleanKey.includes(`/${normalizedComponentPath.toLowerCase()}/index.vue`)) {
        console.log(`findMatchingComponent - 匹配成功: ${key} 匹配模式: ${possiblePath}`)
        return key
      }
    }
  }
  
  // 最后使用模糊匹配
  for (const key of moduleKeys) {
    const parts = normalizedComponentPath.split('/')
    const lastPart = parts[parts.length - 1].toLowerCase()
    
    if (key.toLowerCase().includes(`/${lastPart}.vue`) ||
        key.toLowerCase().includes(`/${lastPart}/index.vue`)) {
      console.log(`findMatchingComponent - 模糊匹配成功: ${key} 匹配部分: ${lastPart}`)
      return key
    }
  }
  
  console.log(`findMatchingComponent - 所有匹配尝试失败`)
  return null
}

export default {
  componentPathMap,
  getComponentPath,
  findMatchingComponent
} 