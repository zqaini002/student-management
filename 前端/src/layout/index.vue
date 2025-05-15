<template>
  <div 
    :class="['app-wrapper', classObj]" 
    :style="{ '--header-height': '60px', '--sidebar-width': sidebarWidth }"
  >
    <!-- 左侧边栏 -->
    <div 
      class="sidebar-container" 
      :style="{ width: sidebarWidth }"
    >
      <div class="logo-container">
        <logo />
      </div>
      <el-scrollbar class="scrollbar-wrapper">
        <sidebar />
      </el-scrollbar>
    </div>
    
    <!-- 主容器 -->
    <div 
      class="main-container" 
      :style="{ 'margin-left': sidebarWidth }"
    >
      <!-- 顶部导航栏 -->
      <div class="header">
        <div class="navbar-container">
          <navbar />
        </div>
        <div class="tags-container">
          <tags-view v-if="showTagsView" />
        </div>
      </div>
      
      <!-- 主要内容区 -->
      <app-main />
      
      <!-- 设置抽屉 -->
      <el-drawer
        v-model="showSettings"
        title="系统设置"
        direction="rtl"
        size="300px"
      >
        <settings />
      </el-drawer>
      
      <!-- 回到顶部 -->
      <el-backtop target=".main-container" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, provide } from 'vue'
import { useAppStore } from '@/stores/app'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'

import Navbar from './components/Navbar.vue'
import Sidebar from './components/Sidebar/index.vue'
import AppMain from './components/AppMain.vue'
import TagsView from './components/TagsView/index.vue'
import Settings from './components/Settings/index.vue'
import Logo from './components/Logo.vue'

// 全局状态
const appStore = useAppStore()
const { sidebar, device } = storeToRefs(appStore)

// 设置面板状态
const showSettings = ref(false)
provide('showSettings', showSettings)

// 是否显示TagsView
const showTagsView = ref(true)

// 计算样式类
const classObj = computed(() => ({
  'hide-sidebar': !sidebar.value.opened,
  'open-sidebar': sidebar.value.opened,
  'without-animation': sidebar.value.withoutAnimation,
  'mobile': device.value === 'mobile'
}))

// 侧边栏宽度
const sidebarWidth = computed(() => {
  return sidebar.value.opened ? '220px' : '60px'
})

// 判断当前路由
const route = useRoute()
provide('currentRoute', route)
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}

.app-wrapper .sidebar-container {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  transition: width 0.28s;
  background-color: #001529;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-wrapper .sidebar-container .logo-container {
  height: var(--header-height);
  padding: 0;
  overflow: hidden;
  flex-shrink: 0;
}

.app-wrapper .sidebar-container .scrollbar-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.app-wrapper .main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  position: relative;
  background-color: #f5f7fa;
}

.app-wrapper .main-container .header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 1000;
  width: calc(100% - var(--sidebar-width));
  transition: width 0.28s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  background-color: #fff;
  pointer-events: auto; /* 确保元素可点击 */
  display: flex;
  flex-direction: column;
}

.app-wrapper .main-container .header .navbar-container {
  height: 60px;
  width: 100%;
  z-index: 10;
}

.app-wrapper .main-container .header .tags-container {
  height: 40px;
  width: 100%;
  z-index: 9;
  overflow: hidden; /* 防止溢出 */
  box-sizing: border-box; /* 确保宽度计算正确 */
  padding: 0; /* 移除内边距 */
}

/* 移动端样式 */
.app-wrapper.mobile .sidebar-container {
  transition: transform 0.28s;
  width: 240px !important;
}

.app-wrapper.mobile.hide-sidebar .sidebar-container {
  transform: translate3d(-240px, 0, 0);
}

.app-wrapper.mobile.hide-sidebar .main-container {
  margin-left: 0 !important;
}

.app-wrapper.mobile.hide-sidebar .header {
  width: 100% !important;
}

.app-wrapper.mobile .main-container {
  margin-left: 0 !important;
}

.app-wrapper.mobile .header {
  width: 100% !important;
}

/* 隐藏侧边栏 */
.app-wrapper.hide-sidebar .header {
  width: calc(100% - 64px);
}
</style>