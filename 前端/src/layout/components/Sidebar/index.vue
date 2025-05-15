<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :unique-opened="true"
    :collapse-transition="false"
    mode="vertical"
    background-color="#001529"
    text-color="hsla(0,0%,100%,.65)"
    active-text-color="#ffffff"
    class="sidebar-menu"
  >
    <!-- 首页菜单项，放在顶部 -->
    <el-menu-item index="/dashboard" @click="navigateTo('/dashboard')">
      <el-icon><HomeFilled /></el-icon>
      <template #title>
        <span>首页</span>
      </template>
    </el-menu-item>
    
    <!-- 分隔线 -->
    <div class="menu-divider"></div>
    
    <!-- 其他菜单项 -->
    <sidebar-item
      v-for="route in filteredRoutes"
      :key="route.path"
      :item="route"
      :base-path="route.path"
      :is-collapse="isCollapse"
    />
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'
import SidebarItem from './SidebarItem.vue'
import { HomeFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

// 侧边栏折叠状态
const isCollapse = computed(() => !appStore.sidebar.opened)

// 路由列表，过滤掉首页路由
const filteredRoutes = computed(() => {
  // 过滤掉首页和其他不需要在侧边栏显示的项
  const routes = permissionStore.routes.filter(route => {
    return !(route.path === '/' || route.path === '/dashboard') && !route.hidden
  })
  
  console.log('侧边栏过滤后的路由:', routes)
  
  // 打印每个路由及其子路由路径，便于检查
  routes.forEach(route => {
    console.log(`路由: ${route.path}, 名称: ${route.name || '无名称'}, 子路由数: ${route.children?.length || 0}`)
    if (route.children) {
      route.children.forEach(child => {
        console.log(`  子路由: ${child.path}, 完整路径: ${route.path}/${child.path}, 名称: ${child.name || '无名称'}`)
      })
    }
  })
  
  return routes
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  
  // 如果设置了activeMenu，优先使用
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  
  return path
})

// 跳转到指定路由
const navigateTo = (path) => {
  router.push(path)
}
</script> 

<style lang="scss" scoped>
.sidebar-menu {
  height: 100%;
  border-right: none !important;
  
  .el-menu-item {
    &:hover, &.is-active {
      color: #ffffff !important;
      background-color: #1890ff !important;
    }
  }
  
  .menu-divider {
    height: 1px;
    background-color: hsla(0,0%,100%,.1);
    margin: 4px 20px;
  }
}
</style> 