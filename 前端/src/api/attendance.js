import request from '@/utils/request'

/**
 * 获取考勤记录列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getAttendanceList(query) {
  return request({
    url: '/attendance/list',
    method: 'post',
    data: query
  })
}

/**
 * 获取考勤统计数据
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getAttendanceStatistics(query) {
  return request({
    url: '/attendance/statistics',
    method: 'post',
    data: query
  })
}

/**
 * 批量提交考勤记录
 * @param {Object} data 考勤数据
 * @returns {Promise}
 */
export function submitBatchAttendance(data) {
  return request({
    url: '/attendance/batch',
    method: 'post',
    data: data
  })
}

/**
 * 获取学生考勤记录
 * @param {Number} studentId 学生ID
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getStudentAttendance(studentId, query) {
  return request({
    url: `/student/${studentId}/attendance`,
    method: 'get',
    params: query
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
 * 导出考勤记录
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportAttendance(query) {
  return request({
    url: '/attendance/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

/**
 * 获取课程考勤统计
 * @param {Number} courseId 课程ID
 * @returns {Promise}
 */
export function getCourseAttendanceStatistics(courseId) {
  return request({
    url: `/attendance/course/${courseId}/statistics`,
    method: 'get'
  })
}

/**
 * 获取班级考勤统计
 * @param {Number} classId 班级ID
 * @returns {Promise}
 */
export function getClassAttendanceStatistics(classId) {
  return request({
    url: `/attendance/class/${classId}/statistics`,
    method: 'get'
  })
} 