import request from '@/utils/request'

// 获取成绩统计数据
export function getGradeStats(query) {
  return request({
    url: '/grade/stats',
    method: 'get',
    params: query
  })
}

// 导出成绩统计报告
export function exportGradeReport(query) {
  return request({
    url: '/grade/export-report',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获取课程选项
export function getCourseOptions() {
  return request({
    url: '/grade/course-options',
    method: 'get'
  })
}

// 获取班级选项
export function getClassOptions() {
  return request({
    url: '/grade/class-options',
    method: 'get'
  })
}

// 获取学期列表
export function getSemesterList() {
  return request({
    url: '/grade/semester-list',
    method: 'get'
  })
}

// 获取学生成绩列表
export function getStudentGradesList(params) {
  console.log('API函数 getStudentGradesList 被调用，参数:', params)
  return request({
    url: '/grade/student-list',
    method: 'get',
    params
  })
}

// 批量提交学生成绩
export function batchSubmitGrades(data) {
  return request({
    url: '/grade/submit-grades',
    method: 'post',
    data
  })
}

/**
 * 获取学生个人成绩列表
 * @param {Number} studentId 学生ID
 * @param {Object} params 查询参数
 * @returns 学生成绩列表分页数据
 */
export function getStudentGrades(studentId, params) {
  return request({
    url: `/student/${studentId}/grades`,
    method: 'get',
    params
  })
}

/**
 * 获取学生成绩统计数据
 * @param {Number} studentId 学生ID
 * @returns 学生成绩统计数据
 */
export function getStudentGradeStats(studentId) {
  return request({
    url: `/student/${studentId}/grade-stats`,
    method: 'get'
  })
}

/**
 * 获取学生信息
 * @param {Number} studentId 学生ID
 * @returns 学生详细信息
 */
export function getStudentInfo(studentId) {
  return request({
    url: `/student/${studentId}/info`,
    method: 'get'
  })
}

/**
 * 获取学生选课记录列表（包含未公布成绩的课程）
 * @param {Number} studentId 学生ID
 * @param {Object} params 查询参数
 * @returns 学生选课记录列表分页数据
 */
export function getStudentCourseSelections(studentId, params) {
  return request({
    url: `/student/${studentId}/course-selections`,
    method: 'get',
    params
  })
}