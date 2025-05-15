import axios from 'axios';
import { getToken } from '@/utils/auth';
import { ElMessage } from 'element-plus';
import errorCode from '@/utils/errorCode';

/**
 * 下载文件
 * 
 * @param {string} url - 请求地址
 * @param {object} params - 请求参数
 * @param {string} filename - 文件名，如果为空则从响应头中获取
 */
export function downloadFile(url, params, filename) {
  // 创建axios实例
  const downloadAxios = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API,
    timeout: 60000, // 下载文件可能需要更长的超时时间
    responseType: 'blob', // 指定响应类型为blob
    headers: { 'Authorization': 'Bearer ' + getToken() }
  });

  // 请求拦截器
  downloadAxios.interceptors.request.use(
    config => {
      return config;
    },
    error => {
      console.log(error);
      Promise.reject(error);
    }
  );

  // 响应拦截器
  downloadAxios.interceptors.response.use(
    res => {
      // 获取响应头中的文件名
      const disposition = res.headers['content-disposition'];
      let fname = filename;
      if (!fname && disposition) {
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(disposition);
        if (matches != null && matches[1]) {
          fname = matches[1].replace(/['"]/g, '');
          // 解码文件名
          try {
            fname = decodeURIComponent(fname);
          } catch (e) {
            console.error('解码文件名失败', e);
          }
        }
      }

      // 创建Blob对象
      const blob = new Blob([res.data], { 
        type: res.headers['content-type'] 
      });

      // 检查是否为JSON错误响应
      if (res.data.type === 'application/json') {
        const reader = new FileReader();
        reader.onload = function() {
          try {
            const result = JSON.parse(reader.result);
            if (result.code && result.code !== 200) {
              ElMessage.error(errorCode[result.code] || result.msg || '下载失败');
            }
          } catch (e) {
            console.error('解析错误响应失败', e);
            ElMessage.error('下载失败');
          }
        };
        reader.readAsText(blob);
        return;
      }

      // 创建下载链接并触发下载
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = fname || '下载文件';
      link.style.display = 'none';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(link.href);
      
      ElMessage.success('下载成功');
    },
    error => {
      console.error('下载失败', error);
      ElMessage.error('下载失败：' + error.message);
    }
  );

  // 发送请求
  return downloadAxios({
    url: url,
    method: 'get',
    params: params
  });
} 