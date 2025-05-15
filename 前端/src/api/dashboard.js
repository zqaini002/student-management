import request from '@/utils/request'

/**
 * 获取仪表盘统计数据
 * @returns {Promise}
 */
export function getStatistics() {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

/**
 * 获取待办事项列表
 * @returns {Promise}
 */
export function getTodoList() {
  return request({
    url: '/dashboard/todo',
    method: 'get'
  })
}

/**
 * 获取系统公告列表
 * @returns {Promise}
 */
export function getNoticeList() {
  return request({
    url: '/dashboard/notice',
    method: 'get'
  })
}

/**
 * 获取院系分布数据
 * @param {String} year 年份筛选，可选值：今年, 去年, 全部
 * @returns {Promise}
 */
export function getDepartmentDistribution(year = '今年') {
  return request({
    url: '/dashboard/department/distribution',
    method: 'get',
    params: { year }
  })
}

/**
 * 获取学生按学期分布数据
 * @param {String} timeRange 时间范围，可选值：近半年, 近一年, 全部
 * @returns {Promise}
 */
export function getStudentSemesterData(timeRange = '近半年') {
  return request({
    url: '/dashboard/student/semester',
    method: 'get',
    params: { timeRange }
  })
}

/**
 * 获取所有待办事项列表
 * @returns {Promise}
 */
export function getAllTodoList() {
  return request({
    url: '/todo/my',
    method: 'get'
  })
}

/**
 * 获取所有已发布公告列表
 * @returns {Promise}
 */
export function getAllNoticeList() {
  return request({
    url: '/notice/published',
    method: 'get'
  })
} 

/**
 * 获取学生个人学习统计数据
 * @returns {Promise}
 */
export function getStudentPersonalStats() {
  return request({
    url: '/dashboard/student/personal/stats',
    method: 'get'
  })
}

/**
 * 获取学生成绩分布数据
 * @returns {Promise}
 */
export function getStudentGradeDistribution() {
  return request({
    url: '/dashboard/student/grade/distribution',
    method: 'get'
  })
}

/**
 * 获取学生专用的院系分布数据
 * @returns {Promise}
 */
export function getStudentDepartmentDistribution() {
  return request({
    url: '/dashboard/student/department/distribution',
    method: 'get'
  })
}

/**
 * 获取学生专用的学期数据
 * @returns {Promise}
 */
export function getStudentSemesterData2() {
  return request({
    url: '/dashboard/student/semester/data',
    method: 'get'
  })
} 