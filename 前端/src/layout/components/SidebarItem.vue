<template>
  <div v-if="!item.hidden">
    <!-- 没有子菜单的情况 -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown': !isNest}">
          <el-icon v-if="onlyOneChild.meta && onlyOneChild.meta.icon">
            <component :is="onlyOneChild.meta.icon"></component>
          </el-icon>
          <template #title>
            <span v-if="onlyOneChild.meta">{{ onlyOneChild.meta.title }}</span>
          </template>
        </el-menu-item>
      </app-link>
    </template>

    <!-- 有子菜单的情况 -->
    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon">
          <component :is="item.meta.icon"></component>
        </el-icon>
        <span v-if="item.meta">{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import AppLink from './Link.vue'

const props = defineProps({
  // 路由项
  item: {
    type: Object,
    required: true
  },
  // 是否是嵌套菜单
  isNest: {
    type: Boolean,
    default: false
  },
  // 基础路径
  basePath: {
    type: String,
    default: ''
  }
})

// 唯一显示的子菜单
const onlyOneChild = ref(null)

/**
 * 判断是否只有一个子菜单需要显示
 * @param {Array} children 子菜单
 * @param {Object} parent 父菜单
 * @returns {Boolean}
 */
function hasOneShowingChild(children = [], parent) {
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // 递归处理
      onlyOneChild.value = item
      return true
    }
  })

  // 如果只有一个子路由，就显示子路由
  if (showingChildren.length === 1) {
    return true
  }

  // 如果没有子路由，就显示父路由
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

/**
 * 解析路由路径
 * @param {String} routePath 路由路径
 * @returns {String}
 */
function resolvePath(routePath) {
  if (routePath.startsWith('/')) {
    return routePath
  }
  if (!props.basePath) {
    return routePath
  }
  
  // 手动实现路径拼接逻辑，避免使用path.resolve
  let fullPath = props.basePath
  if (!fullPath.endsWith('/')) {
    fullPath += '/'
  }
  if (routePath.startsWith('/')) {
    fullPath += routePath.substring(1)
  } else {
    fullPath += routePath
  }
  
  // 处理路径中的..和.
  const segments = fullPath.split('/')
  const result = []
  for (let i = 0; i < segments.length; i++) {
    const segment = segments[i]
    if (segment === '..') {
      result.pop()
    } else if (segment && segment !== '.') {
      result.push(segment)
    }
  }
  
  return '/' + result.join('/')
}
</script> 