import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data 登录数据
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: data
  })
}

/**
 * 获取用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/auth/user/info',
    method: 'get'
  })
}

/**
 * 获取用户路由菜单
 * @returns {Promise}
 */
export function getRoutes() {
  return request({
    url: '/auth/routes',
    method: 'get'
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getUserList(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取用户详情
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getUserDetail(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

/**
 * 添加用户
 * @param {Object} data 用户数据
 * @returns {Promise}
 */
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

/**
 * 更新用户
 * @param {Object} data 用户数据
 * @returns {Promise}
 */
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  })
}

/**
 * 删除用户
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function deleteUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 修改用户状态
 * @param {Object} data 状态数据
 * @returns {Promise}
 */
export function changeUserStatus(data) {
  return request({
    url: '/system/user/status',
    method: 'put',
    data
  })
}

/**
 * 重置用户密码
 * @param {Object} data 包含userId和password的对象
 * @returns {Promise}
 */
export function resetUserPassword(data) {
  return request({
    url: `/system/user/${data.userId}/password`,
    method: 'put',
    data: { 
      password: data.password 
    }
  })
}

/**
 * 修改用户密码
 * @param {String} oldPassword 旧密码
 * @param {String} newPassword 新密码
 * @returns {Promise}
 */
export function updateUserPassword(oldPassword, newPassword) {
  console.log('发送修改密码请求:', { oldPassword, newPassword })
  return request({
    url: '/system/user/password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}

/**
 * 更新用户个人信息
 * @param {Object} data 用户数据
 * @returns {Promise}
 */
export function updateUserProfile(data) {
  console.log('发送更新个人信息请求:', data)
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

/**
 * 上传用户头像
 * @param {Object} data 头像数据
 * @returns {Promise}
 */
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取用户角色列表
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getUserRoles(userId) {
  return request({
    url: `/system/user/${userId}/roles`,
    method: 'get'
  })
}

/**
 * 分配用户角色
 * @param {Object} data 包含userId和roleIds的对象
 * @returns {Promise}
 */
export function assignUserRoles(data) {
  return request({
    url: '/system/user/roles',
    method: 'put',
    data: data
  })
}

/**
 * 导出用户数据
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportUser(query) {
  return request({
    url: '/system/user/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
} 
 