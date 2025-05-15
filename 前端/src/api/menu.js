import request from '@/utils/request'

/**
 * 获取菜单列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

/**
 * 获取菜单详情
 * @param {Number} menuId 菜单ID
 * @returns {Promise}
 */
export function getMenuDetail(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'get'
  })
}

/**
 * 添加菜单
 * @param {Object} data 菜单数据
 * @returns {Promise}
 */
export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 * @param {Object} data 菜单数据
 * @returns {Promise}
 */
export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 * @param {Number} menuId 菜单ID
 * @returns {Promise}
 */
export function deleteMenu(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'delete'
  })
}

/**
 * 获取菜单下拉树结构
 * @returns {Promise}
 */
export function getMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get'
  })
}

/**
 * 获取角色菜单树
 * @param {Number} roleId 角色ID
 * @returns {Promise}
 */
export function getRoleMenuTree(roleId) {
  return request({
    url: `/system/menu/roleMenuTree/${roleId}`,
    method: 'get'
  })
} 