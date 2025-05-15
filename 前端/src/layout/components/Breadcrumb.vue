<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span v-if="index === breadcrumbs.length - 1 || !item.redirect" class="no-redirect">
          {{ item.meta.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { compile } from 'path-to-regexp'

const route = useRoute()
const router = useRouter()
const breadcrumbs = ref([])

// 监听路由变化，更新面包屑
watch(
  () => route.path,
  () => {
    breadcrumbs.value = getBreadcrumbs()
  },
  { immediate: true }
)

/**
 * 获取面包屑数据
 * @returns {Array}
 */
function getBreadcrumbs() {
  const matched = route.matched.filter(
    item => item.meta && item.meta.title && item.meta.breadcrumb !== false
  )
  
  // 如果第一个不是首页，则添加首页
  const first = matched[0]
  if (first && first.path !== '/dashboard') {
    matched.unshift({
      path: '/dashboard',
      meta: { title: '首页' }
    })
  }
  
  return matched
}

/**
 * 处理面包屑项点击
 * @param {Object} item 面包屑项
 */
function handleLink(item) {
  const { redirect, path } = item
  if (redirect) {
    router.push(redirect)
    return
  }
  router.push(pathCompile(path))
}

/**
 * 路径编译
 * @param {String} path 路径
 * @returns {String}
 */
function pathCompile(path) {
  const { params } = route
  const toPath = compile(path)
  return toPath(params)
}
</script>

<style lang="scss" scoped>
.app-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 60px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}

/* 面包屑动画 */
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-leave-active {
  position: absolute;
}
</style> 