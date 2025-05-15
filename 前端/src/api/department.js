import request from '@/utils/request'

/**
 * 获取院系列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getDepartmentList(params) {
  return request({
    url: '/education/department/list',
    method: 'get',
    params
  })
}

/**
 * 获取院系详情
 * @param {Number} id 院系ID
 * @returns {Promise}
 */
export function getDepartmentDetail(id) {
  return request({
    url: `/education/department/${id}`,
    method: 'get'
  })
}

/**
 * 添加院系
 * @param {Object} data 院系数据
 * @returns {Promise}
 */
export function addDepartment(data) {
  return request({
    url: '/education/department',
    method: 'post',
    data
  })
}

/**
 * 更新院系
 * @param {Object} data 院系数据
 * @returns {Promise}
 */
export function updateDepartment(data) {
  return request({
    url: '/education/department',
    method: 'put',
    data
  })
}

/**
 * 删除院系
 * @param {Number} id 院系ID
 * @returns {Promise}
 */
export function deleteDepartment(id) {
  return request({
    url: `/education/department/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有院系（不分页）
 * @returns {Promise}
 */
export function getAllDepartments() {
  return request({
    url: '/education/department/all',
    method: 'get'
  })
} 