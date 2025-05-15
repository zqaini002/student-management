<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-image">
        <img src="@/assets/error/404.svg" alt="404" />
      </div>
      <div class="error-content">
        <h1 class="error-title">404</h1>
        <h2 class="error-subtitle">页面不存在</h2>
        <p class="error-desc">抱歉，您访问的页面不存在或已被删除</p>
        <div class="error-actions">
          <el-button type="primary" @click="goHome">返回首页</el-button>
          <el-button @click="goBack">返回上一页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f7fa;
  
  .error-container {
    display: flex;
    align-items: center;
    background-color: #fff;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    padding: 40px;
    max-width: 800px;
    width: 90%;
    
    .error-image {
      flex: 1;
      margin-right: 40px;
      
      img {
        width: 100%;
        max-width: 300px;
      }
    }
    
    .error-content {
      flex: 1;
      
      .error-title {
        font-size: 72px;
        font-weight: 700;
        color: #409EFF;
        margin: 0;
        line-height: 1;
      }
      
      .error-subtitle {
        font-size: 24px;
        font-weight: 600;
        margin: 16px 0;
        color: #303133;
      }
      
      .error-desc {
        font-size: 16px;
        color: #606266;
        margin-bottom: 30px;
      }
      
      .error-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .error-page {
    .error-container {
      flex-direction: column;
      text-align: center;
      padding: 30px 20px;
      
      .error-image {
        margin-right: 0;
        margin-bottom: 20px;
      }
      
      .error-content {
        .error-actions {
          justify-content: center;
        }
      }
    }
  }
}
</style> 