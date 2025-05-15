import request from '@/utils/request';
import { parseStrEmpty } from '@/utils/index';
import { downloadFile } from '@/utils/download';

// 查询成绩列表
export function listSelections(query) {
  return request({
    url: '/selection/manage/list',
    method: 'get',
    params: query
  });
}

// 查询成绩详情
export function getSelectionDetail(id) {
  return request({
    url: `/selection/manage/${parseStrEmpty(id)}`,
    method: 'get'
  });
}

// 导出成绩数据
export function exportSelections(query) {
  // 使用选课管理控制器的导出API，这个API已经在后端实现
  return request({
    url: '/selection/manage/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  });
} 