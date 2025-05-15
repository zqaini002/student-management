import request from '@/utils/request'

export function getCourseOfferingList(params) {
  console.log('查询课程开设列表，参数:', params);
  return request({
    url: '/courseOffering/list',
    method: 'get',
    params
  }).catch(error => {
    console.error('查询课程开设列表失败:', error);
    throw error;
  });
}

export function addCourseOffering(data) {
  console.log('新增课程开设请求数据:', data);
  return request({
    url: '/courseOffering',
    method: 'post',
    data
  }).catch(error => {
    console.error('新增课程开设失败:', error);
    throw error;
  });
}

export function updateCourseOffering(data) {
  console.log('修改课程开设请求数据:', data);
  return request({
    url: '/courseOffering',
    method: 'put',
    data
  }).catch(error => {
    console.error('修改课程开设失败:', error);
    throw error;
  });
}

export function deleteCourseOffering(id) {
  console.log('删除课程开设，ID:', id);
  return request({
    url: `/courseOffering/${id}`,
    method: 'delete'
  }).catch(error => {
    console.error('删除课程开设失败:', error);
    throw error;
  });
} 