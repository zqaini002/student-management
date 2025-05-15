import request from '@/utils/request'

/**
 * 获取专业列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getMajorList(params) {
  return request({
    url: '/education/major/list',
    method: 'get',
    params
  })
}

/**
 * 获取专业详情
 * @param {Number} id 专业ID
 * @returns {Promise}
 */
export function getMajorDetail(id) {
  return request({
    url: `/education/major/${id}`,
    method: 'get'
  })
}

/**
 * 添加专业
 * @param {Object} data 专业数据
 * @returns {Promise}
 */
export function addMajor(data) {
  return request({
    url: '/education/major',
    method: 'post',
    data
  })
}

/**
 * 更新专业
 * @param {Object} data 专业数据
 * @returns {Promise}
 */
export function updateMajor(data) {
  return request({
    url: '/education/major',
    method: 'put',
    data
  })
}

/**
 * 删除专业
 * @param {Number} id 专业ID
 * @returns {Promise}
 */
export function deleteMajor(id) {
  return request({
    url: `/education/major/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有专业（不分页）
 * @returns {Promise}
 */
export function getAllMajors() {
  return request({
    url: '/education/major/all',
    method: 'get'
  })
}

/**
 * 获取特定院系下的专业
 * @param {Number} departmentId 院系ID
 * @returns {Promise}
 */
export function getMajorsByDepartment(departmentId) {
  return request({
    url: `/education/major/department/${departmentId}`,
    method: 'get'
  })
}