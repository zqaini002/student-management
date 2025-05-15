/**
 * 日期工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date 要格式化的日期
 * @param {string} fmt 格式化模式，如 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, fmt = 'YYYY-MM-DD') {
  if (!date) {
    return '';
  }
  
  // 转换为Date对象
  let dateObj = date;
  if (typeof date === 'string') {
    // 处理ISO格式的日期字符串
    if (date.includes('T')) {
      dateObj = new Date(date);
    } else {
      // 处理普通日期字符串，如2023-01-01
      const parts = date.split(/[-/]/);
      if (parts.length >= 3) {
        dateObj = new Date(parts[0], parts[1] - 1, parts[2]);
      } else {
        dateObj = new Date(date);
      }
    }
  }
  
  if (!(dateObj instanceof Date) || isNaN(dateObj.getTime())) {
    console.warn('无效的日期:', date);
    return '';
  }
  
  const year = dateObj.getFullYear();
  const month = padZero(dateObj.getMonth() + 1);
  const day = padZero(dateObj.getDate());
  const hours = padZero(dateObj.getHours());
  const minutes = padZero(dateObj.getMinutes());
  const seconds = padZero(dateObj.getSeconds());
  
  return fmt
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 数字补零
 * @param {number} num 要补零的数字
 * @returns {string} 补零后的字符串
 */
function padZero(num) {
  return num < 10 ? `0${num}` : `${num}`;
}

/**
 * 获取今天的日期字符串，格式为YYYY-MM-DD
 * @returns {string} 今天的日期
 */
export function today() {
  return formatDate(new Date());
}

/**
 * 日期加减天数
 * @param {string|Date} date 日期
 * @param {number} days 天数，可为负数
 * @returns {string} 计算后的日期字符串
 */
export function addDays(date, days) {
  const dateObj = new Date(date);
  dateObj.setDate(dateObj.getDate() + days);
  return formatDate(dateObj);
}

/**
 * 获取两个日期之间的天数差
 * @param {string|Date} date1 日期1
 * @param {string|Date} date2 日期2
 * @returns {number} 天数差
 */
export function daysBetween(date1, date2) {
  const d1 = new Date(date1);
  const d2 = new Date(date2);
  const diffTime = Math.abs(d2 - d1);
  return Math.floor(diffTime / (1000 * 60 * 60 * 24));
}

export default {
  formatDate,
  today,
  addDays,
  daysBetween
}; 