import request from '@/utils/request'

// 查询专业列表
export function listMajor(query) {
  return request({
    url: '/education/major/list',
    method: 'get',
    params: query
  })
}

// 查询专业详细信息
export function getMajor(id) {
  return request({
    url: `/education/major/${id}`,
    method: 'get'
  })
}

// 新增专业
export function addMajor(data) {
  return request({
    url: '/education/major',
    method: 'post',
    data: data
  })
}

// 修改专业
export function updateMajor(data) {
  return request({
    url: '/education/major',
    method: 'put',
    data: data
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/education/major/${id}`,
    method: 'delete'
  })
}

// 获取所有专业
export function getAllMajors() {
  return request({
    url: '/education/major/all',
    method: 'get'
  })
}

// 获取专业选项列表 (适用于下拉框)
export function getMajorOptions() {
  return request({
    url: '/education/major/options',
    method: 'get'
  })
} 