<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="8" :md="6" :lg="5" :xl="4">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-avatar">
            <el-avatar :size="100" :src="avatarUrl" />
            <div class="profile-avatar-upload">
              <el-upload
                class="avatar-uploader"
                action="#"
                :http-request="handleAvatarUpload"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
              >
                <el-icon class="upload-icon"><Camera /></el-icon>
              </el-upload>
            </div>
          </div>
          <h2 class="profile-name">{{ userInfo.name }}</h2>
          <div class="profile-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ userTypeText }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>{{ userInfo.email || '未设置邮箱' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>{{ userInfo.phone || '未设置手机' }}</span>
            </div>
          </div>
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">12</div>
              <div class="stat-label">我的课程</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">89%</div>
              <div class="stat-label">出勤率</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">28</div>
              <div class="stat-label">通知</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="16" :md="18" :lg="19" :xl="20">
        <el-card shadow="hover">
          <template #header>
            <div class="profile-tabs-header">
              <el-tabs v-model="activeTab">
                <el-tab-pane label="基本资料" name="basic">
                  <template #label>
                    <span><el-icon><Edit /></el-icon> 基本资料</span>
                  </template>
                </el-tab-pane>
                <el-tab-pane label="修改密码" name="password">
                  <template #label>
                    <span><el-icon><Lock /></el-icon> 修改密码</span>
                  </template>
                </el-tab-pane>
                <el-tab-pane label="账号绑定" name="binding">
                  <template #label>
                    <span><el-icon><Connection /></el-icon> 账号绑定</span>
                  </template>
                </el-tab-pane>
                <el-tab-pane label="消息通知" name="notification">
                  <template #label>
                    <span><el-icon><Bell /></el-icon> 消息通知</span>
                  </template>
                </el-tab-pane>
              </el-tabs>
            </div>
          </template>

          <!-- 基本资料 -->
          <div v-if="activeTab === 'basic'">
            <el-form
              ref="userFormRef"
              :model="userForm"
              :rules="userRules"
              label-width="80px"
              class="profile-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input 
                  v-model="userForm.username" 
                  disabled
                  placeholder="用户登录名"
                >
                  <template #append>
                    <el-tooltip content="用户名是系统内部标识，不可修改" placement="top">
                      <el-icon><InfoFilled /></el-icon>
                    </el-tooltip>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <el-input v-model="userForm.name" placeholder="请输入姓名" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitUserForm" :loading="loading">保存</el-button>
                <el-button @click="resetUserForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 修改密码 -->
          <div v-else-if="activeTab === 'password'">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="120px"
              class="profile-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
                <template #append>
                  <el-tooltip content="密码必须包含大小写字母和数字，长度不少于6位" placement="top">
                    <el-icon><InfoFilled /></el-icon>
                  </el-tooltip>
                </template>
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitPasswordForm" :loading="passwordLoading">保存</el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 账号绑定 -->
          <div v-else-if="activeTab === 'binding'" class="profile-binding">
            <el-divider>社交账号绑定</el-divider>
            <div class="binding-list">
              <div class="binding-item">
                <div class="binding-info">
                  <div class="binding-icon wx-icon">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <div class="binding-detail">
                    <div class="binding-name">微信</div>
                    <div class="binding-status">未绑定</div>
                  </div>
                </div>
                <el-button type="success" size="small">绑定</el-button>
              </div>
              <div class="binding-item">
                <div class="binding-info">
                  <div class="binding-icon qq-icon">
                    <el-icon><Service /></el-icon>
                  </div>
                  <div class="binding-detail">
                    <div class="binding-name">QQ</div>
                    <div class="binding-status">未绑定</div>
                  </div>
                </div>
                <el-button type="success" size="small">绑定</el-button>
              </div>
              <div class="binding-item">
                <div class="binding-info">
                  <div class="binding-icon wb-icon">
                    <el-icon><Grid /></el-icon>
                  </div>
                  <div class="binding-detail">
                    <div class="binding-name">微博</div>
                    <div class="binding-status">未绑定</div>
                  </div>
                </div>
                <el-button type="success" size="small">绑定</el-button>
              </div>
            </div>
          </div>

          <!-- 消息通知 -->
          <div v-else-if="activeTab === 'notification'" class="profile-notification">
            <el-divider>消息通知设置</el-divider>
            <div class="notification-item">
              <div class="notification-info">
                <div class="notification-title">系统通知</div>
                <div class="notification-desc">系统更新、维护等通知</div>
              </div>
              <el-switch
                v-model="notificationSettings.system"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </div>
            <div class="notification-item">
              <div class="notification-info">
                <div class="notification-title">课程通知</div>
                <div class="notification-desc">课程变更、考试等通知</div>
              </div>
              <el-switch
                v-model="notificationSettings.course"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </div>
            <div class="notification-item">
              <div class="notification-info">
                <div class="notification-title">考勤通知</div>
                <div class="notification-desc">考勤记录、异常提醒等通知</div>
              </div>
              <el-switch
                v-model="notificationSettings.attendance"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </div>
            <div class="notification-item">
              <div class="notification-info">
                <div class="notification-title">邮件通知</div>
                <div class="notification-desc">通过邮件接收通知</div>
              </div>
              <el-switch
                v-model="notificationSettings.email"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </div>
            <div class="notification-item">
              <div class="notification-info">
                <div class="notification-title">短信通知</div>
                <div class="notification-desc">通过短信接收通知</div>
              </div>
              <el-switch
                v-model="notificationSettings.sms"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </div>
            <div class="notification-action">
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import {
  User,
  Message,
  Phone,
  Edit,
  Lock,
  Connection,
  Bell,
  ChatDotRound,
  Service,
  Grid,
  Camera,
  InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { uploadAvatar } from '@/api/user'
import { updateUserPassword, updateUserProfile, getUserInfo } from '@/api/user'
import defaultAvatar from '@/assets/default-avatar.png'
import { isEmail, isPhone } from '@/utils/validate'
import { formatImageUrl } from '@/utils/image'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

// 用户类型文本
const userTypeText = computed(() => {
  switch (userInfo.value.userType) {
    case 0:
      return '管理员'
    case 1:
      return '教师'
    case 2:
      return '学生'
    default:
      return '未知'
  }
})

// 当前激活的标签页
const activeTab = ref('basic')

// 基本资料表单
const userFormRef = ref(null)
const userForm = reactive({
  username: '',
  name: '',
  email: '',
  phone: ''
})

// 基本资料表单验证规则
const userRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { pattern: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 修改密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 消息通知设置
const notificationSettings = reactive({
  system: true,
  course: true,
  attendance: true,
  email: false,
  sms: false
})

// 表单加载状态
const loading = ref(false)
// 密码表单加载状态
const passwordLoading = ref(false)

// 用户头像
const avatarUrl = computed(() => {
  const url = userInfo.value.avatar || defaultAvatar;
  return url.startsWith('/src/') || url.startsWith('/assets/') ? url : formatImageUrl(url);
})

// 初始化数据
onMounted(async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载用户信息...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    console.log('用户信息:', userInfo.value)
    
    // 如果用户信息不完整，尝试重新获取
    if (!userInfo.value?.username) {
      console.log('用户信息不完整，尝试重新获取')
      await userStore.getInfo()
      console.log('重新获取的用户信息:', userStore.userInfo)
    }
    
    await initUserForm()
  } catch (error) {
    console.error('初始化用户数据失败:', error)
    ElMessage.error('获取用户信息失败，请刷新页面重试')
  } finally {
    loading.close()
  }
})

// 初始化表单数据
const initUserForm = async () => {
  console.log('初始化表单数据，用户信息:', userInfo.value)
  
  // 确保从用户信息中获取用户名，如果为空就从store中直接获取
  let username = userInfo.value.username || userStore.userInfo.username || ''
  
  // 如果用户名仍然为空，尝试从后端获取
  if (!username) {
    console.log('用户名为空，尝试从后端获取')
    const userInfoData = await fetchCurrentUserInfo()
    if (userInfoData) {
      username = userInfoData.username || ''
    }
  }
  
  console.log('最终获取到的用户名:', username)
  
  userForm.username = username
  userForm.name = userInfo.value.name || ''
  userForm.email = userInfo.value.email || ''
  userForm.phone = userInfo.value.phone || ''
  console.log('初始化后的表单数据:', userForm)
}

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传头像
const handleAvatarUpload = (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  const loading = ElLoading.service({
    lock: true,
    text: '上传头像中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  uploadAvatar(formData)
    .then(res => {
      if (res.code === 200) {
        const avatarUrl = res.data
        // 更新本地存储的用户信息的头像
        userStore.updateUserInfo({ avatar: avatarUrl })
        ElMessage.success('头像上传成功')
      } else {
        ElMessage.error(res.message || '头像上传失败')
      }
    })
    .catch(err => {
      console.error('上传头像失败:', err)
      ElMessage.error(err.response?.data?.message || '头像上传失败')
    })
    .finally(() => {
      loading.close()
    })
}

// 提交用户信息表单
const submitUserForm = () => {
  userFormRef.value.validate((valid) => {
    if (valid) {
      // 再次确保用户名不为空
      if (!userForm.username) {
        // 如果表单中没有用户名，尝试从store获取
        userForm.username = userStore.userInfo.username
      }

      // 如果仍然为空，显示错误
      if (!userForm.username) {
        ElMessage.error('用户名信息缺失，请刷新页面重试')
        return
      }
      
      // 调用更新用户信息接口
      const userData = {
        username: userForm.username,
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone
      }
      
      loading.value = true
      updateUserProfile(userData)
        .then(() => {
          // 更新本地存储的用户信息
          userStore.updateUserInfo(userData)
          
          ElMessage.success('个人信息修改成功')
        })
        .catch(err => {
          console.error('修改个人信息失败:', err)
          ElMessage.error('修改个人信息失败: ' + (err.response?.data?.message || err.message))
        })
        .finally(() => {
          loading.value = false
        })
    }
  })
}

// 重置用户信息表单
const resetUserForm = () => {
  userFormRef.value?.resetFields()
  initUserForm()
  ElMessage.info('已重置为原始信息')
}

// 提交密码表单
const submitPasswordForm = () => {
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      // 添加加载状态
      passwordLoading.value = true
      
      // 正确传递两个独立参数，而不是一个对象
      updateUserPassword(
        passwordForm.oldPassword,
        passwordForm.newPassword
      )
        .then(() => {
          ElMessage.success('密码修改成功')
          resetPasswordForm()
        })
        .catch(err => {
          console.error('修改密码失败:', err)
          ElMessage.error('修改密码失败: ' + (err.response?.data?.message || err.message))
        })
        .finally(() => {
          passwordLoading.value = false
        })
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 保存通知设置
const saveNotificationSettings = () => {
  // TODO: 调用保存通知设置接口
  
  ElMessage.success('通知设置已保存')
}

// 添加新方法
async function fetchCurrentUserInfo() {
  try {
    console.log('从后端获取当前用户信息')
    const response = await getUserInfo()
    if (response.data) {
      console.log('获取到的用户信息:', response.data)
      // 更新store中的用户信息
      userStore.userInfo = { ...userStore.userInfo, ...response.data }
      return response.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，请刷新页面')
  }
  return null
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 15px;
  
  .profile-card {
    text-align: center;
    
    .profile-avatar {
      margin: 20px 0;
      position: relative;
      display: inline-block;
      
      .el-avatar {
        border: 4px solid rgba(255, 255, 255, 0.2);
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
      
      .profile-avatar-upload {
        position: absolute;
        bottom: 0;
        right: 0;
        
        .upload-icon {
          background-color: #ffffff;
          border-radius: 50%;
          padding: 5px;
          color: #409EFF;
          cursor: pointer;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          font-size: 20px;
          
          &:hover {
            color: #5cadff;
          }
        }
      }
    }
    
    .profile-name {
      font-size: 20px;
      margin: 10px 0;
    }
    
    .profile-info {
      margin: 20px 0;
      text-align: left;
      padding: 0 15px;
      
      .info-item {
        margin: 10px 0;
        display: flex;
        align-items: center;
        
        .el-icon {
          margin-right: 8px;
          color: #606266;
        }
        
        span {
          font-size: 14px;
          color: #606266;
        }
      }
    }
    
    .profile-stats {
      display: flex;
      justify-content: space-around;
      margin-top: 20px;
      padding: 15px 0;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          font-size: 20px;
          font-weight: bold;
          color: #409EFF;
        }
        
        .stat-label {
          font-size: 12px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }
  
  .profile-tabs-header {
    margin-bottom: 15px;
  }
  
  .profile-form {
    max-width: 500px;
  }
  
  .profile-binding {
    .binding-list {
      margin-top: 20px;
      
      .binding-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #ebeef5;
        
        &:last-child {
          border-bottom: none;
        }
        
        .binding-info {
          display: flex;
          align-items: center;
          
          .binding-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-right: 15px;
            
            &.wx-icon {
              background-color: rgba(45, 183, 39, 0.1);
              color: #2db727;
            }
            
            &.qq-icon {
              background-color: rgba(19, 183, 237, 0.1);
              color: #13b7ed;
            }
            
            &.wb-icon {
              background-color: rgba(230, 22, 45, 0.1);
              color: #e6162d;
            }
            
            .el-icon {
              font-size: 20px;
            }
          }
          
          .binding-detail {
            .binding-name {
              font-size: 16px;
              margin-bottom: 5px;
            }
            
            .binding-status {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
    }
  }
  
  .profile-notification {
    .notification-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 0;
      border-bottom: 1px solid #ebeef5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .notification-info {
        .notification-title {
          font-size: 16px;
          margin-bottom: 5px;
        }
        
        .notification-desc {
          font-size: 12px;
          color: #909399;
        }
      }
    }
    
    .notification-action {
      margin-top: 20px;
      text-align: right;
    }
  }
}

@media (max-width: 768px) {
  .profile-container {
    .profile-card {
      margin-bottom: 20px;
    }
  }
}
</style> 