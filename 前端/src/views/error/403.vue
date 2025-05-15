<template>
  <div class="error-page">
    <div class="error-code">403</div>
    <div class="error-desc">抱歉，你无权访问该页面</div>
    <div class="actions">
      <el-button type="primary" @click="goHome">返回首页</el-button>
      <el-button @click="goBack">返回上一页</el-button>
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

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 500px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-desc {
  font-size: 24px;
  color: #606266;
  margin-bottom: 30px;
}

.actions {
  display: flex;
  gap: 20px;
}
</style>
