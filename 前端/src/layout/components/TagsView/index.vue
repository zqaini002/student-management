<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper" ref="scrollbarRef" wrap-class="scrollbar-wrapper" :native="false">
      <div class="tags-view-item" :class="{ active: isActive('/dashboard') }">
        <router-link to="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </router-link>
      </div>
      <div
        v-for="tag in visitedViews"
        :key="tag.path"
        :class="['tags-view-item', { active: isActive(tag.path) }]"
        @click="handleClick(tag)"
        @contextmenu.prevent="openMenu($event, tag)"
      >
        <router-link :to="{ path: tag.path }">
          <span>{{ tag.title }}</span>
          <el-icon v-if="!isAffix(tag)" @click.stop.prevent="closeSelectedTag(tag)" class="close-icon">
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>
    
    <!-- 右键菜单 -->
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">刷新</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">关闭</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭所有</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const scrollbarRef = ref(null)

// 已访问的视图
const visitedViews = computed(() => appStore.visitedViews)

// 路由匹配
const isActive = (path) => {
  return route.path === path
}

// 是否为固定标签
const isAffix = (tag) => {
  return tag.meta && tag.meta.affix
}

// 添加标签
const addTags = () => {
  const { name, path, meta } = route
  if (name) {
    appStore.addVisitedView(route)
    appStore.addCachedView(route)
    nextTick(() => {
      // 滚动到当前激活的标签
      moveToCurrentTag()
    })
  }
}

// 滚动到当前激活的标签
const moveToCurrentTag = () => {
  if (scrollbarRef.value) {
    const activeTag = document.querySelector('.tags-view-item.active')
    if (activeTag) {
      // 获取标签容器宽度
      const containerWidth = scrollbarRef.value.$el.offsetWidth
      // 获取所有标签
      const tags = document.querySelectorAll('.tags-view-item')
      
      let totalWidth = 0
      let currentWidth = 0
      
      // 计算当前激活标签前面所有标签的总宽度
      for (let i = 0; i < tags.length; i++) {
        totalWidth += tags[i].offsetWidth
        if (tags[i] === activeTag) {
          break
        }
        currentWidth += tags[i].offsetWidth
      }
      
      // 如果标签总宽度大于容器宽度，需要滚动
      if (totalWidth > containerWidth) {
        // 滚动到当前标签的位置
        scrollbarRef.value.setScrollLeft(currentWidth - containerWidth / 2 + activeTag.offsetWidth / 2)
      }
    }
  }
}

// 处理标签点击
const handleClick = (tag) => {
  if (tag.path === route.path) return
  router.push(tag.path).catch(err => {
    console.error('导航失败:', err)
    // 导航失败时默认返回首页
    router.push('/dashboard')
  })
}

// 右键菜单
const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref({})

// 打开右键菜单
const openMenu = (e, tag) => {
  const menuMinWidth = 105
  const offsetLeft = e.target.getBoundingClientRect().left
  const offsetWidth = e.target.offsetWidth
  const maxLeft = window.innerWidth - menuMinWidth
  const leftValue = offsetLeft + offsetWidth / 2
  
  left.value = leftValue > maxLeft ? maxLeft : leftValue
  top.value = e.target.getBoundingClientRect().top + 20
  visible.value = true
  selectedTag.value = tag
}

// 关闭右键菜单
const closeMenu = () => {
  visible.value = false
}

// 刷新选中标签
const refreshSelectedTag = (tag) => {
  appStore.delCachedView(tag)
  router.replace({ path: '/redirect' + tag.path })
}

// 关闭选中标签
const closeSelectedTag = (tag) => {
  // 如果要关闭的是当前激活的标签，先找到要跳转的新标签
  const isCurrent = isActive(tag.path)
  let toPath = '/dashboard'
  
  if (isCurrent) {
    // 先找到下一个要跳转的目标标签
    const nextTag = getNextRoute(visitedViews.value, tag)
    if (nextTag) {
      toPath = nextTag.path
    }
    
    // 先执行路由跳转，再删除标签
    router.push(toPath).then(() => {
      // 路由跳转成功后再删除标签
      appStore.delVisitedView(tag)
      appStore.delCachedView(tag)
    }).catch(err => {
      console.error('关闭标签导航失败:', err)
      router.push('/dashboard')
    })
  } else {
    // 不是当前标签，直接删除即可
  appStore.delVisitedView(tag)
  appStore.delCachedView(tag)
  }
}

// 获取下一个路由目标
const getNextRoute = (visitedViews, view) => {
  // 找到当前关闭标签的索引
  const index = visitedViews.findIndex(v => v.path === view.path)
  
  if (index === visitedViews.length - 1) {
    // 如果是最后一个标签，返回前一个标签
    return index > 0 ? visitedViews[index - 1] : null
  } else {
    // 否则返回后一个标签
    return visitedViews[index + 1]
  }
}

// 关闭其他标签
const closeOthersTags = () => {
  // 先跳转到选中的标签
  const toPath = selectedTag.value.path
  
  // 如果不是当前激活的标签，先跳转
  if (toPath !== route.path) {
    router.push(toPath).then(() => {
      // 路由跳转成功后再删除其他标签
      appStore.delOthersVisitedViews(selectedTag.value)
      appStore.delOthersCachedViews(selectedTag.value)
    }).catch(err => {
      console.error('关闭其他标签导航失败:', err)
      router.push('/dashboard')
    })
  } else {
    // 当前已在选中标签，直接删除其他标签
  appStore.delOthersVisitedViews(selectedTag.value)
  appStore.delOthersCachedViews(selectedTag.value)
  }
}

// 关闭所有标签
const closeAllTags = () => {
  // 先跳转到首页
  router.push('/dashboard').then(() => {
    // 路由跳转成功后再删除所有标签
  appStore.delAllVisitedViews()
  appStore.delAllCachedViews()
  }).catch(err => {
    console.error('关闭所有标签导航失败:', err)
  })
}

// 监听路由变化添加标签
watch(route, (newRoute) => {
  if (newRoute.path.startsWith('/redirect/')) return
  addTags()
}, { immediate: true })

// 监听点击事件关闭菜单
onMounted(() => {
  document.addEventListener('click', closeMenu)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', closeMenu)
})
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 40px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden; /* 防止内容溢出 */
  position: relative; /* 确保元素正确定位 */
  z-index: 9; /* 确保标签在正确层级 */
  
  .tags-view-wrapper {
    width: 100%;
    height: 100%;
    
    :deep(.scrollbar-wrapper) {
      white-space: nowrap;
      padding: 0 4px;
      height: 40px;
      display: flex;
      align-items: center;
      overflow-x: auto;
    }
    
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 6px;
      font-size: 12px;
      margin-right: 3px;
      border-radius: 3px;
      z-index: 1; /* 确保标签项可点击 */
      flex-shrink: 0; /* 防止标签被压缩 */
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      
      &:first-of-type {
        margin-left: 4px;
      }
      
      &.active {
        background-color: #1890ff;
        color: #fff;
        border-color: #1890ff;
        
        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 4px;
        }
        
        a {
          color: #fff;
        }
      }
      
      a {
        display: flex;
        align-items: center;
        color: #495060;
        text-decoration: none;
        max-width: 100%;
        overflow: hidden;
        
        span {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .el-icon {
          margin-left: 2px;
          font-size: 12px;
          width: 14px;
          height: 14px;
          vertical-align: middle;
          flex-shrink: 0;
          
          &:hover {
            border-radius: 50%;
            background: rgba(0, 0, 0, 0.1);
          }
        }
        
        .close-icon {
          padding: 2px;
          margin-left: 3px;
          border-radius: 50%;
          
          &:hover {
            background-color: #ff4949;
            color: white;
          }
        }
      }
    }
  }
  
  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.1);
    
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      
      &:hover {
        background: #f5f7fa;
      }
    }
  }
}
</style> 