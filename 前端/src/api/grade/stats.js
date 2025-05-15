import request from '@/utils/request';
import { downloadFile } from '@/utils/download';

// 获取课程选项
export function getCourseOptions() {
  return request({
    url: '/grade/course-options',
    method: 'get'
  });
}

// 获取班级选项
export function getClassOptions() {
  return request({
    url: '/grade/class-options',
    method: 'get'
  });
}

// 获取成绩统计数据
export function getGradeStats(query) {
  return request({
    url: '/grade/stats',
    method: 'get',
    params: query
  });
}

// 导出成绩统计报告
export function exportGradeReport(query) {
  return request({
    url: '/grade/export-report',
    method: 'get',
    params: query,
    responseType: 'blob'
  });
} 