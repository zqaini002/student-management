import request from '@/utils/request'

// 查询教师列表
export function listTeachers(query) {
  return request({
    url: '/student/class/list/teachers',
    method: 'get',
    params: query
  })
}

// 查询教师详细信息
export function getTeacher(id) {
  return request({
    url: `/student/teacher/${id}`,
    method: 'get'
  })
}

// 新增教师
export function addTeacher(data) {
  return request({
    url: '/student/teacher',
    method: 'post',
    data: data
  })
}

// 修改教师
export function updateTeacher(data) {
  return request({
    url: '/student/teacher',
    method: 'put',
    data: data
  })
}

// 删除教师
export function deleteTeacher(id) {
  return request({
    url: `/student/teacher/${id}`,
    method: 'delete'
  })
}

// 获取所有教师
export function getAllTeachers() {
  return request({
    url: '/student/class/all/teachers',
    method: 'get'
  })
} 