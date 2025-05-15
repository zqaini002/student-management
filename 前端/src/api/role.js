import request from '@/utils/request'

/**
 * 获取角色列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getRoleList(params) {
  // 转换分页参数名称以匹配后端接口
  const newParams = { ...params }
  if (params.pageNum !== undefined) {
    newParams.current = params.pageNum
    delete newParams.pageNum
  }
  if (params.pageSize !== undefined) {
    newParams.size = params.pageSize
    delete newParams.pageSize
  }
  
  return request({
    url: '/system/role/list',
    method: 'get',
    params: newParams
  })
}

/**
 * 获取角色详情
 * @param {Number} roleId 角色ID
 * @returns {Promise}
 */
export function getRoleDetail(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

/**
 * 添加角色
 * @param {Object} data 角色数据
 * @returns {Promise}
 */
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 * @param {Object} data 角色数据
 * @returns {Promise}
 */
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

/**
 * 删除角色
 * @param {Number} roleId 角色ID
 * @returns {Promise}
 */
export function deleteRole(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'delete'
  })
}

/**
 * 修改角色状态
 * @param {Object} data 状态数据
 * @returns {Promise}
 */
export function changeRoleStatus(data) {
  return request({
    url: '/system/role/status',
    method: 'put',
    data: {
      id: data.roleId,
      status: data.status
    }
  })
}

/**
 * 获取角色权限列表
 * @param {Number} roleId 角色ID
 * @returns {Promise}
 */
export function getRolePermissions(roleId) {
  return request({
    url: `/system/role/permissions/${roleId}`,
    method: 'get'
  })
}

/**
 * 更新角色权限
 * @param {Object} data 权限数据
 * @returns {Promise}
 */
export function updateRolePermissions(data) {
  return request({
    url: '/system/role/permissions',
    method: 'put',
    data
  })
}

/**
 * 获取所有可分配的权限列表
 * @returns {Promise}
 */
export function getAllPermissions() {
  return request({
    url: '/system/role/permissions/all',
    method: 'get'
  })
} 