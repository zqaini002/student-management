import request from '@/utils/request';

/**
 * 查询公告列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function listNotice(query) {
  return request({
    url: '/notice/list',
    method: 'get',
    params: query
  });
}

/**
 * 查询已发布的公告列表
 * @returns {Promise}
 */
export function listPublishedNotice() {
  return request({
    url: '/notice/published',
    method: 'get'
  });
}

/**
 * 查询公告详细
 * @param {number} id 公告ID
 * @returns {Promise}
 */
export function getNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  });
}

/**
 * 新增公告
 * @param {Object} data 公告信息
 * @returns {Promise}
 */
export function addNotice(data) {
  return request({
    url: '/notice',
    method: 'post',
    data: data
  });
}

/**
 * 修改公告
 * @param {Object} data 公告信息
 * @returns {Promise}
 */
export function updateNotice(data) {
  return request({
    url: '/notice',
    method: 'put',
    data: data
  });
}

/**
 * 删除公告
 * @param {number} id 公告ID
 * @returns {Promise}
 */
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  });
}

/**
 * 批量删除公告
 * @param {Array} ids 公告ID数组
 * @returns {Promise}
 */
export function deleteNotices(ids) {
  return request({
    url: `/notice/batch/${ids}`,
    method: 'delete'
  });
}

/**
 * 发布公告
 * @param {number} id 公告ID
 * @returns {Promise}
 */
export function publishNotice(id) {
  return request({
    url: `/notice/publish/${id}`,
    method: 'put'
  });
}

/**
 * 下线公告
 * @param {number} id 公告ID
 * @returns {Promise}
 */
export function offlineNotice(id) {
  return request({
    url: `/notice/offline/${id}`,
    method: 'put'
  });
}

/**
 * 获取最新公告
 * @param {number} limit 条数限制
 * @returns {Promise}
 */
export function getLatestNotices(limit = 5) {
  return request({
    url: `/notice/latest/${limit}`,
    method: 'get'
  });
} 