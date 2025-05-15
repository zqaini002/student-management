/**
 * 格式化时间
 * @param {(Object|string|number)} time - 时间对象/字符串/时间戳
 * @param {string} pattern - 格式化模式，默认为 '{y}-{m}-{d} {h}:{i}:{s}'
 * @returns {string} 格式化后的时间字符串
 */
export function parseTime(time, pattern = '{y}-{m}-{d} {h}:{i}:{s}') {
  if (arguments.length === 0 || !time) {
    return null;
  }
  
  let date;
  if (typeof time === 'object') {
    date = time;
  } else {
    if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
      time = parseInt(time);
    } else if (typeof time === 'string') {
      // 支持Safari
      time = time.replace(/-/g, '/');
    }
    if (typeof time === 'number' && time.toString().length === 10) {
      time = time * 1000;
    }
    date = new Date(time);
  }
  
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  };
  
  return pattern.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key];
    // 注意：getDay() 返回 0 表示周日，1表示周一
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value];
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value;
    }
    return value || 0;
  });
}

/**
 * 格式化时间为相对时间
 * @param {(Object|string|number)} time - 时间对象/字符串/时间戳
 * @returns {string} 相对时间，如'刚刚'、'1小时前'等
 */
export function formatRelativeTime(time) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  
  const now = Date.now();
  const diff = (now - new Date(time).getTime()) / 1000;
  
  if (diff < 30) {
    return '刚刚';
  } else if (diff < 3600) {
    // 小于1小时
    return Math.floor(diff / 60) + '分钟前';
  } else if (diff < 86400) {
    // 小于1天
    return Math.floor(diff / 3600) + '小时前';
  } else if (diff < 604800) {
    // 小于7天
    return Math.floor(diff / 86400) + '天前';
  } else {
    // 超过7天，显示具体日期
    return parseTime(time, '{y}-{m}-{d}');
  }
}

/**
 * 处理空字符串
 * @param {string} val - 输入值
 * @returns {string} 如果输入为空，则返回undefined，否则返回原始值
 */
export function parseStrEmpty(val) {
  if (val === '' || val === null || val === undefined) {
    return undefined;
  }
  return val;
}

/**
 * 将对象转换为FormData格式
 * @param {Object} obj - 需要转换的对象
 * @returns {FormData} 转换后的FormData对象
 */
export function toFormData(obj) {
  const formData = new FormData();
  Object.keys(obj).forEach(key => {
    if (obj[key] !== undefined && obj[key] !== null) {
      formData.append(key, obj[key]);
    }
  });
  return formData;
}

/**
 * 添加日期范围
 * @param {number} days - 天数
 * @returns {string[]} 开始日期和结束日期组成的数组
 */
export function addDateRange(days) {
  const end = new Date();
  const start = new Date();
  start.setTime(start.getTime() - 3600 * 1000 * 24 * days);
  return [parseTime(start, '{y}-{m}-{d}'), parseTime(end, '{y}-{m}-{d}')];
}

/**
 * 构建前端路由
 * @param {Array} routes - 路由配置列表
 * @returns {Array} 处理后的路由
 */
export function filterAsyncRoutes(routes) {
  return routes.filter(route => {
    if (route.children && route.children.length) {
      route.children = filterAsyncRoutes(route.children);
    }
    return true;
  });
}

// 深拷贝对象
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') {
    return obj;
  }
  
  const clone = Array.isArray(obj) ? [] : {};
  
  Object.keys(obj).forEach(key => {
    clone[key] = deepClone(obj[key]);
  });
  
  return clone;
} 