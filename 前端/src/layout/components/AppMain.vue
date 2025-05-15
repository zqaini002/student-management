<template>
  <section class="app-main">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="cachedViews">
          <component :is="Component" :key="key" />
        </keep-alive>
      </transition>
    </router-view>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const appStore = useAppStore()

// 缓存视图列表
const cachedViews = computed(() => appStore.cachedViews)

// 路由key，用于强制组件重渲染
const key = computed(() => {
  // 如果meta中指定了key，则使用指定的key
  return route.meta.usePathKey ? route.path : route.fullPath
})
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - 120px);
  width: 100%;
  position: relative;
  overflow: hidden;
  padding: 100px 10px 10px 10px;
  z-index: 8;
  box-sizing: border-box;
  
  .app-container {
    padding: 20px;
  }
}
</style> 