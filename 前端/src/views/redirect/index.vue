<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const { query } = route
  const { path } = query
  
  // 重定向到指定路径
  if (path) {
    console.log('重定向组件: 重定向到', path)
    router.replace({ path: path })
  } else {
    // 如果没有指定路径，则返回首页
    console.log('重定向组件: 未指定路径，返回首页')
    router.replace({ path: '/' })
  }
})
</script>

<template>
  <div class="redirect-container">
    <div class="loading">
      <span>正在跳转...</span>
    </div>
  </div>
</template>

<style scoped>
.redirect-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.loading {
  font-size: 20px;
  color: #409eff;
}
</style> 