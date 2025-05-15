import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 请求前缀
  timeout: 30000, // 请求超时时间
})

console.log('API请求配置信息:', {
  baseURL: service.defaults.baseURL,
  timeout: service.defaults.timeout
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 请求前处理，添加token等
    const token = getToken()
    
    // 调试信息
    console.log('======== API请求开始 ========')
    console.log('请求URL:', config.url)
    console.log('请求方法:', config.method)
    
    // 请求参数日志
    if (config.method === 'get' || config.method === 'delete') {
      console.log('查询参数:', config.params)
      // 检查必填参数
      if (config.url === '/grade/student-list') {
        console.log('成绩查询接口请求:')
        console.log('- courseId:', config.params?.courseId, '类型:', typeof config.params?.courseId)
        console.log('- semester:', config.params?.semester, '类型:', typeof config.params?.semester)
        
        // 检查必填参数
        if (!config.params?.courseId) {
          console.warn('⚠️ 缺少必填参数: courseId')
        }
        if (!config.params?.semester) {
          console.warn('⚠️ 缺少必填参数: semester')
        }
      }
    } else {
      console.log('提交数据:', config.data)
    }
    
    if (token) {
      // 添加token到请求头
      config.headers.Authorization = 'Bearer ' + token
      console.log('认证头:', 'Bearer ' + token.substring(0, 10) + '...')
    } else {
      console.warn('⚠️ 未找到认证令牌，请求可能会被拒绝')
    }
    
    console.log('完整请求URL:', service.defaults.baseURL + config.url)
    console.log('======== 请求配置结束 ========')
    
    return config
  },
  error => {
    console.error('❌ 请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果是下载文件直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    console.log('响应状态码:', response.status)
    console.log('响应数据:', response.data)
    
    // 根据约定的错误码判断请求是否成功
    if (res.code !== 200) {
      // 显示错误信息
      ElMessage({
        message: res.message || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      console.error('请求失败，错误码:', res.code, '错误信息:', res.message)
      
      // 处理令牌过期和无效的情况
      if (res.code === 401) {
        console.warn('登录状态已过期，需要重新登录')
        // 弹窗提示重新登录
        ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          console.log('用户确认重新登录，暂时不执行自动退出以便调试')
          // 调试期间禁用自动退出
          // const userStore = useUserStore()
          // userStore.logout().then(() => {
          //   window.location.reload()
          // })
        })
      }
      
      return Promise.reject(new Error(res.message || '系统错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      console.error('错误状态码:', error.response.status)
      console.error('错误响应头:', error.response.headers)
      console.error('错误响应数据:', error.response.data)
    }
    
    if (error.request) {
      console.error('请求配置:', error.request)
    }
    
    if (error.config) {
      console.error('请求URL:', error.config.url)
      console.error('请求方法:', error.config.method)
      console.error('请求参数:', error.config.params || error.config.data)
    }
    
    let message = error.message
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求错误'
          break
        case 401:
          message = '未授权，请重新登录'
          console.error('收到401未授权响应，但暂时不执行自动退出以便调试')
          // 调试期间禁用自动退出
          // const userStore = useUserStore()
          // userStore.logout().then(() => {
          //   window.location.reload()
          // })
          break
        case 403:
          message = '拒绝访问'
          console.error('收到403拒绝访问响应')
          break
        case 404:
          message = '请求地址错误'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data.message || '系统错误'
      }
    } else {
      message = '连接服务器失败'
    }
    
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

// 封装GET请求
export function get(url, params) {
  return service({
    url,
    method: 'get',
    params
  })
}

// 封装POST请求
export function post(url, data) {
  return service({
    url,
    method: 'post',
    data
  })
}

// 封装PUT请求
export function put(url, data) {
  return service({
    url,
    method: 'put',
    data
  })
}

// 封装DELETE请求
export function del(url, params) {
  return service({
    url,
    method: 'delete',
    params
  })
}

export default service 