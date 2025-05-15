import request from '@/utils/request'

// 查询班级列表
export function getClassList(query) {
  return request({
    url: '/student/class/list',
    method: 'get',
    params: query
  })
}

// 查询班级详细信息
export function getClassDetail(id) {
  return request({
    url: `/student/class/${id}`,
    method: 'get'
  })
}

// 新增班级
export function addClass(data) {
  return request({
    url: '/student/class',
    method: 'post',
    data: data
  })
}

// 修改班级
export function updateClass(data) {
  return request({
    url: '/student/class',
    method: 'put',
    data: data
  })
}

// 删除班级
export function deleteClass(id) {
  return request({
    url: `/student/class/${id}`,
    method: 'delete'
  })
}

// 获取所有班级
export function getAllClasses() {
  return request({
    url: '/student/class/all',
    method: 'get'
  })
} 