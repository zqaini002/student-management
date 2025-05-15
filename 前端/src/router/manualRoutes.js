/**
 * 手动定义的路由映射
 * 用于解决动态路由加载组件失败的情况
 */
import Layout from '@/layout/index.vue'

// 直接导入特定页面
import CourseAdd from '@/views/course/add.vue'
import CourseList from '@/views/course/index.vue'
import TeacherAdd from '@/views/teacher/add.vue'
import TeacherList from '@/views/teacher/index.vue'
import DashboardIndex from '@/views/dashboard/index.vue'
import AttendanceRecord from '@/views/attendance/record/index.vue'
import GradeInput from '@/views/grade/input/index.vue'

export const manualComponentMap = {
  // 仪表盘组件
  'dashboard': DashboardIndex,
  'dashboard/index': DashboardIndex,
  
  // 课程管理组件
  'course/add': CourseAdd,
  'course/add.vue': CourseAdd,
  'course/index': CourseList,
  'course/index.vue': CourseList,
  'course/list': CourseList,
  
  // 教师管理组件
  'teacher/add': TeacherAdd,
  'teacher/add.vue': TeacherAdd,
  'teacher/add/index': TeacherAdd,
  'teacher/index': TeacherList,
  'teacher/index.vue': TeacherList,
  'teacher/list': TeacherList,
  
  // 考勤管理组件
  'attendance/record/index': AttendanceRecord,
  'attendance/record': AttendanceRecord,
  
  // 成绩管理组件
  'grade/input': GradeInput,
  'grade/input/index': GradeInput
}

/**
 * 获取手动映射的组件
 * @param {string} path 组件路径
 * @returns {Component|null} 组件或null
 */
export function getManualComponent(path) {
  if (!path) return null
  
  // 标准化路径
  const normalizedPath = path.replace(/\.vue$/, '')
  
  // 特殊处理dashboard路径
  if (normalizedPath === 'dashboard' || normalizedPath === 'dashboard/index') {
    console.log(`[手动映射] 特殊处理 dashboard 路径`)
    return manualComponentMap['dashboard'] || null
  }
  
  // 特殊处理teacher/add路径
  if (normalizedPath === 'teacher/add/index' || normalizedPath === 'teacher/add') {
    console.log(`[手动映射] 特殊处理 teacher/add 路径`)
    return manualComponentMap['teacher/add'] || null
  }
  
  return manualComponentMap[normalizedPath] || 
         manualComponentMap[path] || 
         null
}

export default {
  manualComponentMap,
  getManualComponent
} 