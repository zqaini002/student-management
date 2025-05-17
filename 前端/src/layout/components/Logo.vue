<template>
  <div class="logo-container">
    <router-link to="/" class="logo-link">
      <img src="@/assets/logo.png" class="logo-image" alt="系统logo">
      <h1 class="logo-title" v-if="!isCollapsed">高校智能管理平台</h1>
    </router-link>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
const { sidebar } = appStore

// 计算侧边栏是否折叠
const isCollapsed = computed(() => !sidebar.opened)
</script>

<style lang="scss" scoped>
.logo-container {
  height: 60px;
  background: #002140;
  overflow: hidden;
  
  .logo-link {
    display: flex;
    align-items: center;
    height: 100%;
    padding: 0 16px;
    text-decoration: none;
    overflow: hidden;
    
    .logo-image {
      width: 28px;
      height: 28px;
      flex-shrink: 0;
    }
    
    .logo-title {
      display: inline-block;
      margin: 0 0 0 10px;
      color: #fff;
      font-weight: 600;
      font-size: 16px;
      white-space: nowrap;
      line-height: 1.2;
      overflow: hidden;
      text-overflow: ellipsis;
      flex: 1;
    }
  }
}
</style> 