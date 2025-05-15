/**
 * 全局组件注册
 * 解决动态导入问题的备选方案
 */
import { defineAsyncComponent } from 'vue'

// 直接导入关键组件
// 注意：这些路径是相对于本文件的
import CourseAdd from '../views/course/add.vue'
import CourseIndex from '../views/course/index.vue'
import StudentAdd from '../views/student/add.vue'
import StudentIndex from '../views/student/index.vue'
import TeacherAdd from '../views/teacher/add.vue'
import TeacherIndex from '../views/teacher/index.vue'
import DashboardIndex from '../views/dashboard/index.vue'

// 全局组件映射表
export const globalComponents = {
  // 仪表盘 - 首页
  'dashboard': DashboardIndex,
  'dashboard/index': DashboardIndex,
  
  // 课程管理
  'course/add': CourseAdd,
  'course/add/index': CourseAdd, // 额外添加兼容路径
  'course/add.vue': CourseAdd,   // 额外添加兼容路径
  'course/index': CourseIndex,
  
  // 学生管理
  'student/add': StudentAdd,
  'student/add/index': StudentAdd, // 额外添加兼容路径
  'student/add.vue': StudentAdd,   // 额外添加兼容路径
  'student/index': StudentIndex,
  
  // 教师管理
  'teacher/add': TeacherAdd,
  'teacher/add/index': TeacherAdd, // 额外添加兼容路径
  'teacher/add.vue': TeacherAdd,   // 额外添加兼容路径
  'teacher/index': TeacherIndex,
  
  // 可以根据需要添加更多组件
}

/**
 * 获取全局注册的组件
 * @param {string} path 组件路径
 * @returns {Component|null} 组件或null
 */
export function getGlobalComponent(path) {
  if (!path) return null
  
  // 标准化路径
  const normalizedPath = path.replace(/\.vue$/, '')
  
  // 特殊处理dashboard路径
  if (normalizedPath === 'dashboard' || normalizedPath === 'dashboard/index') {
    console.log(`全局组件查找 - 特殊处理: ${normalizedPath}`)
    return globalComponents['dashboard'] || null
  }
  
  // 特殊处理course/add路径
  if (normalizedPath.includes('course/add')) {
    console.log(`全局组件查找 - 特殊处理: ${normalizedPath}`)
    return globalComponents['course/add'] || null
  }
  
  // 特殊处理student/add路径
  if (normalizedPath.includes('student/add')) {
    console.log(`全局组件查找 - 特殊处理: ${normalizedPath}`)
    return globalComponents['student/add'] || null
  }
  
  // 特殊处理teacher/add路径
  if (normalizedPath.includes('teacher/add')) {
    console.log(`全局组件查找 - 特殊处理: ${normalizedPath}`)
    return globalComponents['teacher/add'] || null
  }
  
  // 查找组件
  return globalComponents[normalizedPath] || null
}

/**
 * 注册全局组件
 * @param {object} app Vue应用实例
 */
export function registerGlobalComponents(app) {
  // 注册全局组件
  Object.entries(globalComponents).forEach(([name, component]) => {
    app.component(`Global${name.replace(/\//g, '')}`, component)
  })
  
  console.log('全局组件注册完成')
}

export default {
  globalComponents,
  getGlobalComponent,
  registerGlobalComponents
} 