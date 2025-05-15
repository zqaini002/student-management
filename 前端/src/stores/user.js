import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { clearRouteCache } from '@/router'
import { usePermissionStore } from './permission'
import defaultAvatar from '@/assets/default-avatar.png'
import { formatImageUrl } from '@/utils/image'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: [],
    userId: null,
    userInfo: {
      avatar: defaultAvatar // 设置默认头像
    }
  }),
  getters: {
    // 获取用户角色
    userRoles: (state) => state.roles,
    // 判断是否为管理员
    isAdmin: (state) => state.roles.includes('admin'),
    // 判断是否为教师
    isTeacher: (state) => state.roles.includes('teacher'),
    // 判断是否为学生
    isStudent: (state) => state.roles.includes('student')
  },
  actions: {
    // 登录
    async login(userInfo) {
      try {
        const { username, password } = userInfo
        const res = await loginApi({ username: username.trim(), password })
        const { token } = res.data
        // 保存token
        setToken(token)
        this.token = token
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 获取用户信息
    async getInfo() {
      try {
        const res = await getUserInfo()
        console.log('获取用户信息接口返回数据:', res.data)
        
        const { roles, name, avatar, permissions, userId } = res.data
        
        // 验证角色是否有效
        if (!roles || roles.length <= 0) {
          console.error('用户角色不能为空！', roles)
          throw new Error('用户角色不能为空！')
        }
        
        // 记录原始角色名，确保与后端鉴权一致
        console.info('获取到用户角色:', roles)
        this.roles = roles
        
        this.name = name
        // 确保avatar字段正确设置，优先使用返回的头像URL，如果没有则使用默认头像
        // 格式化头像URL，确保能正确访问
        this.avatar = avatar ? formatImageUrl(avatar) : defaultAvatar
        this.permissions = permissions
        this.userId = userId
        this.userInfo = {
          ...res.data,
          avatar: avatar ? formatImageUrl(avatar) : defaultAvatar // 确保userInfo.avatar也被设置
        }
        
        return Promise.resolve(res)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return Promise.reject(error)
      }
    },
    
    // 检查用户是否有特定权限
    hasPermission(permission) {
      if (!this.permissions || this.permissions.length === 0) {
        return false
      }
      return this.permissions.includes(permission)
    },
    
    // 检查用户是否有特定角色
    hasRole(role) {
      if (!this.roles || this.roles.length === 0) {
        return false
      }
      return this.roles.includes(role)
    },
    
    // 更新用户信息
    updateUserInfo(userInfo) {
      if (userInfo.username) this.userInfo.username = userInfo.username
      if (userInfo.name) {
        this.name = userInfo.name
        this.userInfo.name = userInfo.name
      }
      if (userInfo.avatar) {
        // 格式化头像URL，确保能正确访问
        this.avatar = formatImageUrl(userInfo.avatar)
        this.userInfo.avatar = formatImageUrl(userInfo.avatar)
      }
      if (userInfo.email) this.userInfo.email = userInfo.email
      if (userInfo.phone) this.userInfo.phone = userInfo.phone
      
      // 更新完整的userInfo对象
      this.userInfo = { ...this.userInfo, ...userInfo }
      
      console.log('用户信息已更新:', this.userInfo)
    },
    
    // 退出登录
    async logout() {
      try {
        await logoutApi()
        
        const permissionStore = usePermissionStore()
        
        // 清空权限相关状态
        this.resetState()
        permissionStore.resetRoutes()
        
        // 跳转到登录页
        router.push('/login')
        
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 清空token
    resetToken() {
      removeToken()
      this.token = ''
      this.roles = []
      this.permissions = []
    },
    
    // 重置状态
    resetState() {
      this.token = ''
      this.name = ''
      this.avatar = ''
      this.roles = []
      this.permissions = []
      this.userId = null
      this.userInfo = {
        avatar: defaultAvatar // 重置为默认头像
      }
      removeToken()
      // 清除路由缓存
      clearRouteCache()
    }
  },
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'roles', 'permissions', 'userId', 'name', 'avatar', 'userInfo'] // 持久化更多关键字段
  }
}) 