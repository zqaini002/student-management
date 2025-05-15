/**
 * 判断是否为外部链接
 * @param {string} path 路径
 * @returns {boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 判断是否为URL
 * @param {string} url URL地址
 * @returns {boolean}
 */
export function isValidURL(url) {
  const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

/**
 * 判断是否为小写字母
 * @param {string} str 字符串
 * @returns {boolean}
 */
export function isLowerCase(str) {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

/**
 * 判断是否为大写字母
 * @param {string} str 字符串
 * @returns {boolean}
 */
export function isUpperCase(str) {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

/**
 * 判断是否为字母
 * @param {string} str 字符串
 * @returns {boolean}
 */
export function isAlphabets(str) {
  const reg = /^[A-Za-z]+$/
  return reg.test(str)
}

/**
 * 判断是否为中文
 * @param {string} str 字符串
 * @returns {boolean}
 */
export function isChinese(str) {
  const reg = /^[\u4e00-\u9fa5]+$/
  return reg.test(str)
}

/**
 * 验证电子邮箱格式
 * @param {string} email 邮箱
 * @returns {boolean}
 */
export function isEmail(email) {
  const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(email)
}

/**
 * 验证手机号码
 * @param {string} phone 手机号码
 * @returns {boolean}
 */
export function isPhone(phone) {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}

/**
 * 验证身份证号码
 * @param {string} idCard 身份证号码
 * @returns {boolean}
 */
export function isIdCard(idCard) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(idCard)
}

/**
 * 验证密码强度
 * @param {string} password 密码
 * @returns {number} 0-4 (弱-强)
 */
export function checkPasswordStrength(password) {
  let strength = 0
  // 长度校验
  if (password.length >= 8) strength += 1
  // 字母校验
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 1
  // 数字校验
  if (/\d/.test(password)) strength += 1
  // 特殊字符校验
  if (/[~!@#$%^&*()_+`\-={}[\]:";'<>?,.\/]/.test(password)) strength += 1
  
  return strength
}