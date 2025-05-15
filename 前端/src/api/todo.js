import request from '@/utils/request';

/**
 * 查询待办事项列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function listTodo(query) {
  return request({
    url: '/todo/list',
    method: 'get',
    params: query
  });
}

/**
 * 获取当前用户的待办事项列表
 * @returns {Promise}
 */
export function listMyTodo() {
  return request({
    url: '/todo/my',
    method: 'get'
  });
}

/**
 * 查询待办事项详细
 * @param {number} id 待办事项ID
 * @returns {Promise}
 */
export function getTodo(id) {
  return request({
    url: `/todo/${id}`,
    method: 'get'
  });
}

/**
 * 新增待办事项
 * @param {Object} data 待办事项信息
 * @returns {Promise}
 */
export function addTodo(data) {
  return request({
    url: '/todo',
    method: 'post',
    data: data
  });
}

/**
 * 修改待办事项
 * @param {Object} data 待办事项信息
 * @returns {Promise}
 */
export function updateTodo(data) {
  return request({
    url: '/todo',
    method: 'put',
    data: data
  });
}

/**
 * 删除待办事项
 * @param {number} id 待办事项ID
 * @returns {Promise}
 */
export function deleteTodo(id) {
  return request({
    url: `/todo/${id}`,
    method: 'delete'
  });
}

/**
 * 批量删除待办事项
 * @param {Array} ids 待办事项ID数组
 * @returns {Promise}
 */
export function deleteTodos(ids) {
  return request({
    url: `/todo/batch/${ids}`,
    method: 'delete'
  });
}

/**
 * 完成待办事项
 * @param {number} id 待办事项ID
 * @returns {Promise}
 */
export function completeTodo(id) {
  return request({
    url: `/todo/complete/${id}`,
    method: 'put'
  });
}

/**
 * 获取未完成的待办事项数量
 * @returns {Promise}
 */
export function countUncompletedTodo() {
  return request({
    url: '/todo/count/uncompleted',
    method: 'get'
  });
}

/**
 * 获取即将到期的待办事项
 * @param {number} days 天数
 * @returns {Promise}
 */
export function getUpcomingTodos(days = 3) {
  return request({
    url: `/todo/upcoming/${days}`,
    method: 'get'
  });
} 