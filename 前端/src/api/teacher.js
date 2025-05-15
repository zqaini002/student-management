import request from '@/utils/request'

/**
 * 获取教师列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getTeacherList(query) {
  return request({
    url: '/teacher/list',
    method: 'post',
    data: query
  })
}

/**
 * 获取教师详情
 * @param {Number} teacherId 教师ID
 * @returns {Promise}
 */
export function getTeacherDetail(teacherId) {
  return request({
    url: `/teacher/${teacherId}`,
    method: 'get'
  })
}

/**
 * 添加教师
 * @param {Object} data 教师数据
 * @returns {Promise}
 */
export function addTeacher(data) {
  return request({
    url: '/teacher',
    method: 'post',
    data: data
  })
}

/**
 * 修改教师
 * @param {Object} data 教师数据
 * @returns {Promise}
 */
export function updateTeacher(data) {
  return request({
    url: '/teacher',
    method: 'put',
    data: data
  })
}

/**
 * 删除教师
 * @param {Number} teacherId 教师ID
 * @returns {Promise}
 */
export function deleteTeacher(teacherId) {
  return request({
    url: `/teacher/${teacherId}`,
    method: 'delete'
  })
}

/**
 * 重置教师密码
 * @param {Number} teacherId 教师ID
 * @returns {Promise}
 */
export function resetTeacherPassword(teacherId) {
  return request({
    url: `/teacher/resetPassword/${teacherId}`,
    method: 'put'
  })
}

/**
 * 更新教师状态
 * @param {Number} teacherId 教师ID
 * @param {Number} status 状态值
 * @returns {Promise}
 */
export function updateTeacherStatus(teacherId, status) {
  return request({
    url: `/teacher/status/${teacherId}/${status}`,
    method: 'put'
  })
}

/**
 * 获取教师课程列表
 * @param {Number} teacherId 教师ID
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getTeacherCourses(teacherId, query) {
  return request({
    url: `/teacher/courses/${teacherId}`,
    method: 'post',
    data: query
  })
}

/**
 * 获取教师课程学生列表
 * @param {Number} teacherId 教师ID
 * @param {Number} courseId 课程ID
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getTeacherCourseStudents(teacherId, courseId, query) {
  return request({
    url: `/teacher/courses/${teacherId}/students/${courseId}`,
    method: 'post',
    data: query
  })
}

/**
 * 提交学生成绩
 * @param {Number} teacherId 教师ID
 * @param {Number} courseId 课程ID
 * @param {Object} data 成绩数据
 * @returns {Promise}
 */
export function submitStudentGrade(teacherId, courseId, data) {
  return request({
    url: `/teacher/courses/${teacherId}/grade/${courseId}`,
    method: 'put',
    data: data
  })
}

/**
 * 提交学生考勤
 * @param {Number} teacherId 教师ID
 * @param {Number} courseId 课程ID
 * @param {Object} data 考勤数据
 * @returns {Promise}
 */
export function submitStudentAttendance(teacherId, courseId, data) {
  return request({
    url: `/teacher/courses/${teacherId}/attendance/${courseId}`,
    method: 'put',
    data: data
  })
}

/**
 * 获取教师统计数据
 * @param {Number} teacherId 教师ID
 * @returns {Promise}
 */
export function getTeacherStatistics(teacherId) {
  return request({
    url: `/teacher/statistics/${teacherId || ''}`,
    method: 'get'
  })
}

/**
 * 导入教师数据
 * @param {File} file 教师数据文件
 * @returns {Promise}
 */
export function importTeacher(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/teacher/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出教师数据
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportTeacher(query) {
  return request({
    url: '/teacher/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

/**
 * 获取教师导入模板
 * @returns {Promise}
 */
export function downloadTeacherTemplate() {
  return request({
    url: '/teacher/template',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 获取教师课表
 * @param {Number} teacherId 教师ID
 * @param {Object} query 查询参数，如学期信息
 * @returns {Promise}
 */
export function getTeacherSchedule(teacherId, query) {
  return request({
    url: `/teacher/schedule/${teacherId}`,
    method: 'post',
    data: query
  })
}

/**
 * 获取教师教学评价
 * @param {Number} teacherId 教师ID
 * @param {Object} query 查询参数，如学期信息
 * @returns {Promise}
 */
export function getTeacherEvaluations(teacherId, query) {
  return request({
    url: `/teacher/evaluations/${teacherId}`,
    method: 'post',
    data: query
  })
}

/**
 * 获取所有院系列表（不分页）
 * @returns {Promise}
 */
export function getAllDepartments() {
  return request({
    url: '/education/department/all',
    method: 'get'
  })
}

/**
 * 获取所有教师（用于下拉框）
 */
export function getAllTeachers() {
  return request({
    url: '/teacher/list',
    method: 'post',
    data: { pageNum: 1, pageSize: 1000 }
  })
}