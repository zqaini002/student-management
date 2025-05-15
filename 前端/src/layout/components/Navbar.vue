<template>
  <div class="navbar">
    <!-- 左侧区域 -->
    <div class="left-area">
      <!-- 折叠按钮 -->
      <div class="hamburger" @click="toggleSidebar">
        <el-icon :size="24" :class="{ 'is-active': sidebar.opened }">
          <component :is="sidebar.opened ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
      
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <!-- 右侧菜单区域 -->
    <div class="right-menu">
      <!-- 全屏按钮 -->
      <div class="right-menu-item" @click="toggleFullScreen">
        <el-tooltip content="全屏" placement="bottom">
          <el-icon :size="20">
            <FullScreen />
          </el-icon>
        </el-tooltip>
      </div>
      
      <!-- 系统设置 -->
      <div class="right-menu-item" @click="openSettings">
        <el-tooltip content="系统设置" placement="bottom">
          <el-icon :size="20">
            <Setting />
          </el-icon>
        </el-tooltip>
      </div>
      
      <!-- 用户下拉菜单 -->
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="32" :src="userAvatar" />
          <span class="user-name">{{ userName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <router-link to="/profile">
                <el-icon><User /></el-icon>
                个人中心
              </router-link>
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed, inject, watch, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import defaultAvatar from '@/assets/logo.png'
import { formatImageUrl } from '@/utils/image'

// 组件通信
const showSettings = inject('showSettings')

// 路由与用户状态
const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 侧边栏状态
const sidebar = computed(() => appStore.sidebar)

// 用户信息
const userName = computed(() => userStore.name || '未登录')
const userAvatar = computed(() => {
  const avatar = userStore.avatar || defaultAvatar;
  return avatar.startsWith('/src/') || avatar.startsWith('/assets/') ? avatar : formatImageUrl(avatar);
})

// 面包屑导航数据
const breadcrumbs = ref([])

// 监听路由变化生成面包屑
watch(
  () => route.path,
  () => {
    // 不在主页时才显示面包屑
    if (route.path.startsWith('/dashboard')) {
      breadcrumbs.value = []
      return
    }
    
    const matched = route.matched.filter(
      item => item.meta && item.meta.title
    )
    
    breadcrumbs.value = matched.map(item => {
      return {
        path: item.path,
        title: item.meta.title
      }
    })
  },
  { immediate: true }
)

// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

// 打开设置面板
const openSettings = () => {
  showSettings.value = true
}

// 切换全屏
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.logout()
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px; /* 减小内边距 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  width: 100%; /* 确保宽度100% */
  position: relative; /* 确保元素正确定位 */
  z-index: 10; /* 确保元素在正确的层级上 */
  
  .left-area {
    display: flex;
    align-items: center;
    
    .hamburger {
      padding-right: 20px;
      cursor: pointer;
      
      .is-active {
        transform: rotate(180deg);
      }
    }
  }
  
  .right-menu {
    display: flex;
    align-items: center;
    z-index: 11; /* 确保按钮在顶层 */
    position: relative; /* 正确定位 */
    
    .right-menu-item {
      padding: 0 12px;
      cursor: pointer;
      font-size: 18px;
      color: #666;
      vertical-align: middle;
      
      &:hover {
        color: #409EFF;
      }
    }
    
    .avatar-container {
      margin-left: 16px;
      
      .avatar-wrapper {
        display: flex;
        align-items: center;
        cursor: pointer;
        
        .user-name {
          margin: 0 8px;
          color: #666;
          font-size: 14px;
        }
      }
    }
  }
}
</style> 