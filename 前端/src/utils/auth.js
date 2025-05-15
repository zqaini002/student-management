import Cookies from 'js-cookie'

const TokenKey = 'Student-Management-Token'

/**
 * 获取token
 * @returns {String}
 */
export function getToken() {
  return Cookies.get(TokenKey) || localStorage.getItem(TokenKey)
}

/**
 * 设置token
 * @param {String} token
 * @returns {String}
 */
export function setToken(token) {
  localStorage.setItem(TokenKey, token)
  return Cookies.set(TokenKey, token)
}

/**
 * 移除token
 * @returns {void}
 */
export function removeToken() {
  localStorage.removeItem(TokenKey)
  return Cookies.remove(TokenKey)
} 

