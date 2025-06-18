/**
 * 导入工具函数
 * 用于调试和改进组件动态导入
 */
import { h } from 'vue'

/**
 * 获取所有视图组件并打印详细信息
 * 这个函数用于调试，不应在生产环境使用
 */
export function loadAndDebugComponents() {
  // 加载所有视图组件
  const modules = import.meta.glob('../views/**/*.vue')
  console.log('===== 组件加载调试 =====')
  console.log(`总共找到 ${Object.keys(modules).length} 个组件`)
  
  // 按目录分类组件
  const componentsByDir = {}
  Object.keys(modules).forEach(path => {
    // 提取目录名
    const match = path.match(/\.\.\/views\/([^/]+)/)
    if (match && match[1]) {
      const dirName = match[1]
      componentsByDir[dirName] = componentsByDir[dirName] || []
      componentsByDir[dirName].push(path)
    }
  })
  
  // 打印各分类组件
  Object.keys(componentsByDir).forEach(dir => {
    console.log(`${dir}相关组件 (${componentsByDir[dir].length}):`, componentsByDir[dir])
  })
  
  // 分析目录结构模式
  console.log('===== 目录结构分析 =====')
  const directFileCount = Object.keys(modules).filter(path => path.match(/\.\.\/views\/[^/]+\/[^/]+\.vue$/)).length
  const indexFileCount = Object.keys(modules).filter(path => path.match(/\.\.\/views\/[^/]+\/[^/]+\/index\.vue$/)).length
  
  console.log(`直接文件模式数量 (xxx.vue): ${directFileCount}`)
  console.log(`索引文件模式数量 (xxx/index.vue): ${indexFileCount}`)
  console.log(`推荐的结构模式: ${directFileCount >= indexFileCount ? '直接文件模式' : '索引文件模式'}`)
  
  // 测试加载不同路径格式
  const testPaths = [
    'course/add',
    'student/add',
    'teacher/add',
    'attendance/record',
    'system/user'
  ]
  
  console.log('===== 路径加载测试 =====')
  testPaths.forEach(testPath => {
    // 尝试不同的加载路径
    const possiblePaths = [
      `../views/${testPath}.vue`,
      `../views/${testPath}/index.vue`
    ]
    
    console.log(`测试路径 "${testPath}" 可能的实际路径:`)
    possiblePaths.forEach(path => {
      console.log(`- ${path}: ${modules[path] ? '存在' : '不存在'}`)
    })
    
    // 使用统一加载方式尝试
    const component = findComponentModule(testPath)
    console.log(`使用统一加载器结果: ${component ? '成功' : '失败'}`)
  })
  
  return modules
}

/**
 * 规范化组件路径，处理各种可能的路径格式
 * @param {string} path 组件路径
 * @returns {string} 规范化后的路径
 */
export function normalizePath(path) {
  if (!path) return ''
  
  // 移除开头的 './' 或 '/'
  let normalized = path.replace(/^(\.\/|\/)/g, '')
  
  // 移除.vue后缀
  normalized = normalized.replace(/\.vue$/, '')
  
  // 移除/index结尾
  normalized = normalized.replace(/\/index$/, '')
  
  // 标准化Windows路径分隔符
  normalized = normalized.replace(/\\/g, '/')
  
  console.log(`规范化路径: ${path} -> ${normalized}`)
  
  return normalized
}

/**
 * 根据组件路径查找真实模块
 * @param {string} componentPath 组件路径
 * @returns {Function|null} 组件加载函数或null
 */
export function findComponentModule(componentPath) {
  // 获取所有视图组件
  const modules = import.meta.glob('../views/**/*.vue')
  console.log(`查找组件: ${componentPath}, 可用组件数: ${Object.keys(modules).length}`)
  
  // 规范化搜索路径
  const normalizedPath = normalizePath(componentPath)
  
  // 特殊处理message相关路径
  if (componentPath.includes('message')) {
    console.log('检测到message相关路径，打印所有可能的相关组件:')
    Object.keys(modules).filter(path => path.includes('message')).forEach(path => {
      console.log(`- ${path}`)
    })
  }
  
  // 尝试不同的路径格式（优先级从高到低）
  const searchPaths = [
    `../views/${normalizedPath}.vue`,          // 直接文件模式
    `../views/${normalizedPath}/index.vue`,    // 索引文件模式
  ]
  
  // 添加备用搜索路径
  // 处理子路径，例如 'course/offering' 可能是 'course/offering/index.vue'
  const pathParts = normalizedPath.split('/')
  if (pathParts.length > 1) {
    // 检查最后一段是否也可以是目录
    const parentPath = pathParts.slice(0, -1).join('/')
    const lastPart = pathParts[pathParts.length - 1]
    searchPaths.push(`../views/${parentPath}/${lastPart}/index.vue`)
  }
  
  // 打印所有可能的路径，帮助调试
  console.log('尝试查找的所有可能路径:', searchPaths)
  
  // 优先尝试直接匹配
  for (const searchPath of searchPaths) {
    if (modules[searchPath]) {
      console.log(`组件路径匹配成功: ${searchPath}`)
      return modules[searchPath]
    }
  }
  
  console.log(`未找到直接匹配，尝试模糊匹配...`)
  
  // 如果直接匹配失败，对每个可能的路径进行简化，去掉前缀
  for (const searchPath of searchPaths) {
    // 获取不包含../views/的路径
    const simplePath = searchPath.replace('../views/', '')
    
    // 查找所有模块中可能匹配的项
    for (const modulePath of Object.keys(modules)) {
      if (modulePath.endsWith(simplePath)) {
        console.log(`简化路径匹配成功: ${modulePath} -> ${simplePath}`)
        return modules[modulePath]
      }
    }
  }
  
  // 如果以上匹配都失败，尝试寻找任何包含路径最后部分的模块
  if (pathParts.length > 0) {
    const lastPart = pathParts[pathParts.length - 1]
    console.log(`尝试匹配路径的最后部分: ${lastPart}`)
    
    // 特殊处理message下的组件
    if (normalizedPath.startsWith('message/')) {
      console.log('特殊处理message路径:', normalizedPath)
      // 尝试特定的路径组合
      const messageComponent = modules[`../views/message/${lastPart}/index.vue`]
      if (messageComponent) {
        console.log(`找到message组件: ../views/message/${lastPart}/index.vue`)
        return messageComponent
      }
    }
    
    for (const modulePath of Object.keys(modules)) {
      const cleanModulePath = modulePath.toLowerCase()
      if (
        cleanModulePath.includes(`/${lastPart.toLowerCase()}.vue`) || 
        cleanModulePath.includes(`/${lastPart.toLowerCase()}/index.vue`)
      ) {
        // 额外检查，确保匹配的是正确的组件
        if (pathParts.length > 1) {
          // 检查父路径是否也匹配
          const parentPart = pathParts[pathParts.length - 2].toLowerCase()
          if (!cleanModulePath.includes(`/${parentPart}/`)) {
            continue // 父路径不匹配，继续查找
          }
        }
        
        console.log(`最后部分匹配成功: ${modulePath}`)
        return modules[modulePath]
      }
    }
  }
  
  // 所有匹配策略都失败，记录详细信息
  console.error(`找不到匹配的组件: ${componentPath}`)
  console.log('规范化后的路径:', normalizedPath)
  console.log('尝试过的搜索路径:', searchPaths)
  console.log('可用的前10个模块:', Object.keys(modules).slice(0, 10))
  
  return null
}

/**
 * 以统一的方式加载组件
 * @param {string} path 组件路径
 * @returns {Function} 组件加载函数
 */
export function loadComponent(path) {
  return () => {
    console.log(`统一加载器加载组件: ${path}`)
    
    // 对course/list路径进行特殊处理
    if (path === 'course/list' || path === '/course/list') {
      console.log('检测到course/list路径，进行特殊处理')
      const CourseList = require('@/views/course/index.vue').default
      return Promise.resolve(CourseList)
    }
    
    // 对student/edit路径进行特殊处理
    if (path.startsWith('student/edit/') || path.startsWith('/student/edit/')) {
      console.log('检测到student/edit路径，进行特殊处理')
      const StudentEdit = require('@/views/student/edit.vue').default
      return Promise.resolve(StudentEdit)
    }
    
    // 优先检查是否有手动映射的组件
    try {
      // 动态导入手动映射组件
      const { getManualComponent } = require('@/router/manualRoutes')
      const manualComponent = getManualComponent(path)
      
      if (manualComponent) {
        console.log(`找到手动映射组件: ${path}`)
        return Promise.resolve(manualComponent)
      }
    } catch (err) {
      console.error('加载手动映射组件时出错:', err)
    }
    
    // 使用明确的导入映射而不是动态字符串拼接
    // 这样Vite/Webpack可以正确解析所有可能的导入
    const modules = import.meta.glob('../views/**/*.vue')
    
    // 对路径进行规范化
    const normalizedPath = normalizePath(path)
    console.log(`加载组件，规范化后路径: ${normalizedPath}`)
    
    // 特殊处理message相关的路径
    if (path.includes('message')) {
      console.log('正在加载message相关组件:', path)
      console.log('可用的message相关模块:')
      Object.keys(modules)
        .filter(modulePath => modulePath.includes('message'))
        .forEach(modulePath => console.log(`- ${modulePath}`))
    }
    
    // 尝试直接模式 (xxx.vue)
    const directPath = `../views/${normalizedPath}.vue`
    // 尝试索引模式 (xxx/index.vue)
    const indexPath = `../views/${normalizedPath}/index.vue`
    // 尝试消息特定路径
    const messagePath = path.startsWith('message/') 
      ? `../views/message/${path.split('/')[1]}/index.vue` 
      : null
    
    console.log(`尝试加载路径: ${directPath} 或 ${indexPath}${messagePath ? ' 或 ' + messagePath : ''}`)
    console.log(`可用模块: ${Object.keys(modules).length}`)
    
    if (modules[directPath]) {
      console.log(`找到直接模式组件: ${directPath}`)
      return modules[directPath]()
    }
    
    if (modules[indexPath]) {
      console.log(`找到索引模式组件: ${indexPath}`)
      return modules[indexPath]()
    }
    
    if (messagePath && modules[messagePath]) {
      console.log(`找到消息模式组件: ${messagePath}`)
      return modules[messagePath]()
    }
    
    // 如果直接路径未找到，使用findComponentModule函数
    const module = findComponentModule(path)
    if (module) {
      console.log(`通过findComponentModule找到组件`)
      return module()
    }
    
    console.error(`组件 ${path} 未找到，尝试过的路径都不存在`)
    // 返回错误组件
    return Promise.resolve({
      render() {
        return h('div', { style: { padding: '20px' } }, [
          h('h2', '组件加载错误'),
          h('p', `无法加载组件: ${path}`),
          h('pre', { style: { color: 'red' } }, 
            `找不到路径: ${directPath} 或 ${indexPath}${messagePath ? ' 或 ' + messagePath : ''}`)
        ])
      }
    })
  }
}

export default {
  loadAndDebugComponents,
  normalizePath,
  findComponentModule,
  loadComponent
}