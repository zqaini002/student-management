<template>
  <div class="setting-container">
    <h2 class="setting-title">系统设置</h2>
    
    <!-- 主题设置 -->
    <div class="setting-item">
      <div class="setting-item-title">系统主题</div>
      <div class="theme-picker">
        <div
          v-for="item in themeOptions" 
          :key="item.value"
          :class="['theme-item', { active: theme === item.value }]"
          :style="{ backgroundColor: item.color }"
          @click="changeTheme(item.value)"
        >
          <el-icon v-if="theme === item.value"><Check /></el-icon>
        </div>
      </div>
    </div>
    
    <!-- 侧边栏设置 -->
    <div class="setting-item">
      <div class="setting-item-title">侧边栏</div>
      <el-switch
        v-model="sidebarOpened"
        inline-prompt
        active-text="开启"
        inactive-text="关闭"
        @change="toggleSidebar"
      />
    </div>
    
    <!-- 标签栏设置 -->
    <div class="setting-item">
      <div class="setting-item-title">标签栏</div>
      <el-switch
        v-model="tagsView"
        inline-prompt
        active-text="开启"
        inactive-text="关闭"
      />
    </div>
    
    <!-- 系统大小设置 -->
    <div class="setting-item">
      <div class="setting-item-title">系统大小</div>
      <el-radio-group v-model="size" @change="changeSize">
        <el-radio-button label="large">大</el-radio-button>
        <el-radio-button label="default">中</el-radio-button>
        <el-radio-button label="small">小</el-radio-button>
      </el-radio-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

// 主题选项
const themeOptions = [
  { value: 'default', color: '#1890ff', text: '默认蓝' },
  { value: 'green', color: '#52c41a', text: '清新绿' },
  { value: 'purple', color: '#722ed1', text: '优雅紫' },
  { value: 'pink', color: '#eb2f96', text: '魅力粉' },
  { value: 'orange', color: '#fa8c16', text: '活力橙' },
  { value: 'red', color: '#f5222d', text: '热情红' }
]

// 侧边栏状态
const sidebarOpened = computed({
  get: () => appStore.sidebar.opened,
  set: (val) => {
    if (val) {
      appStore.toggleSidebar()
    } else {
      appStore.closeSidebar({ withoutAnimation: false })
    }
  }
})

// 主题
const theme = computed({
  get: () => appStore.theme,
  set: (val) => appStore.setTheme(val)
})

// 系统大小
const size = computed({
  get: () => appStore.size,
  set: (val) => appStore.setSize(val)
})

// 标签栏
const tagsView = ref(true)

// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

// 切换主题
const changeTheme = (val) => {
  theme.value = val
}

// 切换大小
const changeSize = (val) => {
  size.value = val
}
</script>

<style lang="scss" scoped>
.setting-container {
  padding: 20px;
  
  .setting-title {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 18px;
    font-weight: 600;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 10px;
  }
  
  .setting-item {
    margin-bottom: 30px;
    
    .setting-item-title {
      font-size: 15px;
      margin-bottom: 12px;
      color: #303133;
      font-weight: 500;
    }
    
    .theme-picker {
      display: flex;
      flex-wrap: wrap;
      
      .theme-item {
        width: 40px;
        height: 40px;
        margin-right: 10px;
        margin-bottom: 10px;
        border-radius: 4px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 16px;
        
        &.active {
          box-shadow: 0 0 0 2px #fff, 0 0 0 4px #1890ff;
        }
      }
    }
  }
}
</style> 