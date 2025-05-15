import request from '@/utils/request'

/**
 * 获取课程列表
 * @param {*} params 查询参数
 * @returns 课程列表分页数据
 */
export function listCourses(params) {
  return request({
    url: '/course/list',
    method: 'get', 
    params
  })
}

/**
 * 获取课程详情
 * @param {*} id 课程ID
 * @returns 课程详情
 */
export function getCourseDetail(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

/**
 * 新增课程
 * @param {*} data 课程数据
 * @returns 操作结果
 */
export function addCourse(data) {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

/**
 * 修改课程
 * @param {*} data 课程数据
 * @returns 操作结果
 */
export function updateCourse(data) {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

/**
 * 删除课程
 * @param {*} id 课程ID
 * @returns 操作结果
 */
export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

/**
 * 导出课程数据
 * @param {*} params 查询参数
 */
export function exportCourses(params) {
  return request({
    url: '/course/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 根据院系ID获取课程列表
 * @param {*} departmentId 院系ID
 * @returns 课程列表
 */
export function getCoursesByDepartment(departmentId) {
  return request({
    url: `/course/department/${departmentId}`,
    method: 'get'
  })
}

/**
 * 获取课程统计数据
 * @returns 课程统计数据
 */
export function getCourseStats() {
  return request({
    url: '/course/stats',
    method: 'get'
  })
}

/**
 * 获取考勤记录可用的课程列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getCourseListForAttendance(query) {
  return request({
    url: '/course/listForAttendance',
    method: 'get',
    params: query
  })
}

/**
 * 获取所有课程（用于下拉框）
 */
export function getAllCourses() {
  return request({
    url: '/course/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 1000 }
  })
} 

/**
 * 获取学生已选课程列表
 * @param {Number} studentId 学生ID
 * @param {Object} params 查询参数
 * @returns 学生选课列表分页数据
 */
export function getStudentSelectedCourses(studentId, params) {
  return request({
    url: `/student/${studentId}/courses`,
    method: 'get',
    params
  })
}

/**
 * 获取学生自己的课程列表（学生专用）
 * @param {Number} studentId 学生ID
 * @param {Object} params 查询参数
 * @returns 学生选课列表分页数据
 */
export function getStudentOwnCourses(studentId, params) {
  return request({
    url: `/student/${studentId}/my-courses`,
    method: 'get',
    params
  })
}

/**
 * 获取学生可选课程列表
 * @param {Number} studentId 学生ID
 * @param {Object} params 查询参数
 * @returns 可选课程列表分页数据
 */
export function getAvailableCourses(studentId, params) {
  return request({
    url: `/student/${studentId}/available-courses`,
    method: 'get',
    params
  })
}

/**
 * 获取当前登录学生的可选课程列表（学生专用）
 * @param {Object} params 查询参数
 * @returns 可选课程列表分页数据
 */
export function getCurrentStudentAvailableCourses(params) {
  return request({
    url: '/student/current/available-courses',
    method: 'get',
    params
  })
}

/**
 * 获取当前登录学生的已选课程列表（学生专用）
 * @param {Object} params 查询参数
 * @returns 已选课程列表分页数据
 */
export function getCurrentStudentSelectedCourses(params) {
  return request({
    url: '/student/current/selected-courses',
    method: 'get',
    params
  })
}

/**
 * 学生选课
 * @param {Number} studentId 学生ID
 * @param {Number} courseId 课程ID
 * @returns 操作结果
 */
export function selectCourse(studentId, courseId) {
  return request({
    url: `/student/${studentId}/select-course/${courseId}`,
    method: 'post'
  })
}

/**
 * 当前登录学生选课（学生专用）
 * @param {Number} courseId 课程ID
 * @returns 操作结果
 */
export function currentStudentSelectCourse(courseId) {
  return request({
    url: `/student/current/select-course/${courseId}`,
    method: 'post'
  })
}

/**
 * 学生退选课程
 * @param {Number} studentId 学生ID
 * @param {Number} courseId 课程ID
 * @returns 操作结果
 */
export function withdrawCourse(studentId, courseId) {
  return request({
    url: `/student/${studentId}/withdraw-course/${courseId}`,
    method: 'delete'
  })
}

/**
 * 当前登录学生退选课程（学生专用）
 * @param {Number} courseId 课程ID
 * @returns 操作结果
 */
export function currentStudentWithdrawCourse(courseId) {
  return request({
    url: `/student/current/withdraw-course/${courseId}`,
    method: 'delete'
  })
}

/**
 * 获取选课设置
 * @returns 选课设置信息
 */
export function getSelectionSettings() {
  return request({
    url: '/student/course/selection/settings',
    method: 'get'
  })
}

/**
 * 检查课程时间冲突
 * @param {Object} data 包含课程时间的对象 {courseTime1, courseTime2}
 * @returns 是否冲突
 */
export function checkTimeConflict(data) {
  return request({
    url: '/course/check-conflict',
    method: 'post',
    data: {
      courseTime1: data.courseTime1 || '',
      courseTime2: data.courseTime2 || ''
    }
  })
}

/**
 * 获取课程教材信息
 * @param {Number} courseId 课程ID
 * @returns 教材信息
 */
export function getCourseMaterials(courseId) {
  return request({
    url: `/course/${courseId}/materials`,
    method: 'get'
  })
} 