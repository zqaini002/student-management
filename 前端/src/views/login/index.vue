<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 登录卡片 -->
      <el-card class="login-card">
        <!-- 标题 -->
        <div class="title-container">
          <h3 class="title">EduSmart 高校智能管理平台</h3>
        </div>
        
        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          label-position="left"
        >
          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="密码"
              :type="passwordType"
              prefix-icon="Lock"
              clearable
              @keyup.enter="handleLogin"
            >
              <template #suffix>
                <el-icon
                  class="cursor-pointer"
                  @click="togglePasswordVisible"
                >
                  <component :is="passwordType === 'password' ? 'View' : 'Hide'" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <!-- 记住密码 -->
          <div class="remember-container">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <a class="forget-link">忘记密码?</a>
          </div>
          
          <!-- 登录按钮 -->
          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>
        
        <!-- 账号提示 -->
        <div class="tips">
          <p>
            管理员账号: admin / 123456<br>
            教师账号: teacher001 / 123456<br>
            学生账号: student001 / 123456
          </p>
        </div>
      </el-card>
      
      <!-- 版权信息 -->
      <div class="copyright">
        © {{ new Date().getFullYear() }} EduSmart 高校智能管理平台
      </div>
    </div>
    
    <!-- 背景动画 -->
    <div class="bg-container">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// 路由信息
const router = useRouter()
const route = useRoute()

// 用户状态
const userStore = useUserStore()

// 登录表单
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单校验规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, message: '密码长度不能少于5个字符', trigger: 'blur' }
  ]
}

// 密码显示状态
const passwordType = ref('password')
const togglePasswordVisible = () => {
  passwordType.value = passwordType.value === 'password' ? 'text' : 'password'
}

// 加载状态
const loading = ref(false)

// 处理登录
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      await userStore.login(loginForm)
      
      // 重定向或跳转到首页
      const redirect = route.query.redirect || '/dashboard'
      router.push({ path: redirect })
      
      ElMessage.success('登录成功')
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  
  .login-box {
    position: relative;
    z-index: 2;
    width: 400px;
    
    .login-card {
      padding: 20px 35px;
      border-radius: 8px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      
      .title-container {
        margin-bottom: 30px;
        text-align: center;
        
        .title {
          margin: 0;
          font-size: 22px;
          font-weight: 600;
          color: #1890ff;
        }
      }
      
      .login-form {
        .el-form-item {
          margin-bottom: 25px;
          
          .el-input {
            height: 42px;
            
            input {
              height: 42px;
            }
          }
        }
        
        .remember-container {
          display: flex;
          justify-content: space-between;
          margin-bottom: 20px;
          
          .forget-link {
            color: #1890ff;
            cursor: pointer;
            
            &:hover {
              text-decoration: underline;
            }
          }
        }
        
        .login-button {
          width: 100%;
          height: 44px;
          font-weight: 600;
          margin-bottom: 20px;
        }
      }
      
      .tips {
        margin-top: 10px;
        font-size: 13px;
        color: #909399;
        text-align: center;
        line-height: 1.5;
      }
    }
    
    .copyright {
      margin-top: 20px;
      text-align: center;
      color: #fff;
      font-size: 14px;
      opacity: 0.8;
    }
  }
  
  .bg-container {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
    
    .circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      
      &.circle-1 {
        width: 200px;
        height: 200px;
        top: 10%;
        left: 15%;
        animation: float 6s infinite alternate;
      }
      
      &.circle-2 {
        width: 300px;
        height: 300px;
        bottom: 10%;
        right: 15%;
        animation: float 8s infinite alternate-reverse;
      }
      
      &.circle-3 {
        width: 120px;
        height: 120px;
        top: 20%;
        right: 20%;
        animation: float 5s infinite alternate;
      }
      
      &.circle-4 {
        width: 150px;
        height: 150px;
        bottom: 20%;
        left: 20%;
        animation: float 7s infinite alternate-reverse;
      }
    }
  }
}

@keyframes float {
  0% {
    transform: translateY(0) rotate(0deg);
  }
  100% {
    transform: translateY(30px) rotate(10deg);
  }
}
</style>