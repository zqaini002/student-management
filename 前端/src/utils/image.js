/**
 * 图片工具类 - 用于处理图片URL和路径
 */

// 判断是否为相对路径
const isRelativePath = (url) => {
  return url && url.startsWith('/') && !url.startsWith('//') && !url.startsWith('/api') && !url.startsWith('http');
};

// 判断是否是默认资源路径
const isAssetPath = (url) => {
  return url && (url.startsWith('/src/') || url.startsWith('/assets/'));
};

/**
 * 格式化图片URL，确保能正确访问
 * @param {String} url 原始URL
 * @returns {String} 处理后的URL
 */
export function formatImageUrl(url) {
  // 如果没有URL或者是空字符串，返回空
  if (!url) return '';
  
  // 如果是HTTP(S)链接，则不做处理
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是资源路径，不处理
  if (isAssetPath(url)) {
    return url;
  }
  
  // 如果是相对路径，添加API前缀
  if (isRelativePath(url)) {
    // 确保URL以/开头但不重复添加
    const path = url.startsWith('/') ? url : `/${url}`;
    return `/api${path}`;
  }
  
  // 其他情况直接返回
  return url;
}

export default {
  formatImageUrl
}; 