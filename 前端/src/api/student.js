import request from '@/utils/request'

/**
 * 获取学生列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getStudentList(query) {
  console.log('调用学生列表API, 参数:', query)
  return request({
    url: '/student/page',
    method: 'get',
    params: query
  }).then(res => {
    console.log('获取学生列表成功, 返回数据:', res)
    return res
  }).catch(err => {
    console.error('获取学生列表失败, 错误:', err)
    return Promise.reject(err)
  })
}

/**
 * 获取学生详情
 * @param {Number} studentId 学生ID
 * @returns {Promise}
 */
export function getStudentDetail(studentId) {
  console.log('调用学生详情API, ID:', studentId)
  return request({
    url: `/student/${studentId}`,
    method: 'get'
  }).then(res => {
    console.log('获取学生详情成功, 返回数据:', res)
    return res
  }).catch(err => {
    console.error('获取学生详情失败, 错误:', err)
    return Promise.reject(err)
  })
}

/**
 * 添加学生
 * @param {Object} data 学生数据
 * @returns {Promise}
 */
export function addStudent(data) {
  return request({
    url: '/student',
    method: 'post',
    data: data
  })
}

/**
 * 更新学生
 * @param {Object} data 学生数据
 * @returns {Promise}
 */
export function updateStudent(data) {
  return request({
    url: '/student',
    method: 'put',
    data: data
  })
}

/**
 * 删除学生
 * @param {Number} studentId 学生ID
 * @returns {Promise}
 */
export function deleteStudent(studentId) {
  return request({
    url: `/student/${studentId}`,
    method: 'delete'
  })
}

/**
 * 批量更新学生状态
 * @param {Array} ids 学生ID数组
 * @param {Number} status 状态值
 * @returns {Promise}
 */
export function updateBatchStudentStatus(ids, status) {
  return request({
    url: '/student/batch/status',
    method: 'put',
    data: {
      ids: ids,
      status: status
    }
  })
}

/**
 * 获取学生课程列表
 * @param {Number} studentId 学生ID
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getStudentCourses(studentId, query) {
  return request({
    url: `/student/${studentId}/courses`,
    method: 'get',
    params: query
  })
}

/**
 * 导入学生信息
 * @param {FormData} data 表单数据
 * @returns {Promise}
 */
export function importStudent(data) {
  return request({
    url: '/student/import',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出学生信息
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportStudent(query) {
  return request({
    url: '/student/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

/**
 * 获取学生成绩列表
 * @param {Number} studentId 学生ID
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getStudentGrades(studentId, query) {
  return request({
    url: `/student/${studentId}/grades`,
    method: 'get',
    params: query
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
 * 获取学生统计数据
 * @returns {Promise}
 */
export function getStudentStatistics() {
  return request({
    url: '/student/statistics',
    method: 'get'
  })
}

/**
 * 导入学生信息模板下载
 * @returns {Promise}
 */
export function downloadStudentTemplate() {
  return request({
    url: '/student/template',
    method: 'get',
    responseType: 'blob'
  })
} 