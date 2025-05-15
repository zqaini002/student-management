<template>
  <div v-if="!item.hidden">
    <!-- 没有子菜单 -->
    <template v-if="hasNoChild(item)">
      <app-link v-if="item.meta" :to="resolvePath(item.path)">
        <el-menu-item :index="resolvePath(item.path)">
          <el-icon v-if="item.meta.icon">
            <component :is="item.meta.icon" />
          </el-icon>
          <template #title>
            <span>{{ item.meta.title }}</span>
          </template>
        </el-menu-item>
      </app-link>
    </template>
    
    <!-- 有子菜单 -->
    <el-sub-menu v-else :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta ? item.meta.title : '' }}</span>
      </template>
      
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-collapse="isCollapse"
        :item="child"
        :base-path="resolvePath(item.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { isExternal } from '@/utils/validate'
import AppLink from './Link.vue'
import path from 'path-browserify'

const props = defineProps({
  // 菜单项
  item: {
    type: Object,
    required: true
  },
  // 基础路径
  basePath: {
    type: String,
    default: ''
  },
  // 是否折叠
  isCollapse: {
    type: Boolean,
    default: false
  }
})

// 判断是否没有子菜单
const hasNoChild = (item) => {
  return !item.children || item.children.length === 0 || 
         (item.children.length === 1 && item.children[0].hidden)
}

// 解析路径
const resolvePath = (routePath) => {
  // 处理外部链接
  if (isExternal(routePath)) {
    return routePath
  }
  
  // 如果basePath是外部链接，直接返回
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  
  // 记录路径解析过程
  console.log('解析路径 - 基础路径:', props.basePath, '路由路径:', routePath)
  
  // 特殊处理，避免重复路径
  // 如果子路由以/开头，表示是绝对路径，直接使用
  if (routePath.startsWith('/')) {
    console.log('子路由是绝对路径，直接使用:', routePath)
    return routePath
  }
  
  // 如果父路由路径以/结尾，或子路由路径为空，不需要额外添加/
  let resolved
  if (props.basePath.endsWith('/') || routePath === '') {
    resolved = props.basePath + routePath
  } else {
    resolved = `${props.basePath}/${routePath}`
  }
  
  // 规范化路径，去除多余的斜杠
  resolved = resolved.replace(/\/+/g, '/')
  
  console.log('解析结果:', resolved)
  return resolved
}
</script> 