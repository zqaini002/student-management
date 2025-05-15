import request from '@/utils/request'

// 查询选课列表
export function listSelections(query) {
  return request({
    url: '/selection/manage/list',
    method: 'get',
    params: query
  })
}

// 获取选课详细信息
export function getSelection(id) {
  return request({
    url: '/selection/manage/' + id,
    method: 'get'
  })
}

// 修改选课信息
export function updateSelection(data) {
  console.log('发送更新选课请求，数据:', data);
  return request({
    url: '/selection/manage',
    method: 'put',
    data: data
  })
}

// 退课操作
export function withdrawCourse(id) {
  return request({
    url: '/selection/manage/withdraw/' + id,
    method: 'put'
  })
}

// 批量退课操作
export function batchWithdrawCourses(ids) {
  return request({
    url: '/selection/manage/batch-withdraw',
    method: 'put',
    data: ids
  })
}

// 导出选课数据
export function exportSelections(query) {
  return request({
    url: '/selection/manage/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获取选课统计数据
export function getSelectionStatistics() {
  return request({
    url: '/selection/manage/statistics',
    method: 'get'
  })
}

// 获取课程类型选课比例数据
export function getCourseTypeStatistics() {
  return request({
    url: '/selection/manage/statistics/course-type',
    method: 'get'
  })
}

// 获取学期选课统计数据
export function getSemesterStatistics() {
  return request({
    url: '/selection/manage/statistics/semester',
    method: 'get'
  })
}

// 获取学期列表
export function listSemesters() {
  return request({
    url: '/selection/manage/semesters',
    method: 'get'
  })
}

// 生成选课报表
export function generateSelectionReport(data) {
  return request({
    url: '/selection/manage/report',
    method: 'post',
    data: data
  })
} 