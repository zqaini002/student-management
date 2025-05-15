import request from '@/utils/request'

/**
 * 获取班级列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getClassList(params) {
  return request({
    url: '/student/class/list',
    method: 'get',
    params
  })
}

/**
 * 获取班级详情
 * @param {Number} id 班级ID
 * @returns {Promise}
 */
export function getClassDetail(id) {
  return request({
    url: `/student/class/${id}`,
    method: 'get'
  })
}

/**
 * 添加班级
 * @param {Object} data 班级数据
 * @returns {Promise}
 */
export function addClass(data) {
  return request({
    url: '/student/class',
    method: 'post',
    data
  })
}

/**
 * 更新班级
 * @param {Object} data 班级数据
 * @returns {Promise}
 */
export function updateClass(data) {
  return request({
    url: '/student/class',
    method: 'put',
    data
  })
}

/**
 * 删除班级
 * @param {Number} id 班级ID
 * @returns {Promise}
 */
export function deleteClass(id) {
  return request({
    url: `/student/class/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有班级（不分页）
 * @returns {Promise}
 */
export function getAllClasses() {
  console.log('调用获取所有班级API');
  return request({
    url: '/student/class/all',
    method: 'get'
  })
} 