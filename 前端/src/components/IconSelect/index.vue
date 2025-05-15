<template>
  <div class="icon-select">
    <el-input
      v-model="iconName"
      placeholder="请选择图标"
      readonly
      clearable
      @clear="clearIcon"
      @click="showDialog"
    >
      <template #prepend>图标</template>
      <template #append>
        <el-button @click="showDialog">
          <el-icon><Search /></el-icon>
        </el-button>
      </template>
      <template #prefix>
        <el-icon v-if="iconName">
          <component :is="iconName" />
        </el-icon>
      </template>
    </el-input>

    <el-dialog
      v-model="dialogVisible"
      title="选择图标"
      width="800px"
      :before-close="closeDialog"
    >
      <div class="icon-search">
        <el-input
          v-model="searchKey"
          placeholder="请输入图标名称搜索"
          clearable
          @clear="searchIcons"
          @input="searchIcons"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="icon-container">
        <div v-if="filteredIcons.length === 0" class="icon-empty">
          <el-empty description="暂无匹配图标" />
        </div>
        <div
          v-for="(icon, index) in filteredIcons"
          :key="index"
          class="icon-item"
          :class="{ 'is-active': icon === iconName }"
          @click="selectIcon(icon)"
        >
          <el-icon>
            <component :is="icon" />
          </el-icon>
          <span class="icon-name">{{ icon }}</span>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="confirmSelect">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  // 初始图标值
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 图标名称
const iconName = ref(props.modelValue)
// 对话框可见性
const dialogVisible = ref(false)
// 搜索关键字
const searchKey = ref('')
// 临时选择的图标
const tempSelectedIcon = ref('')

// 所有图标列表
const allIcons = ref([])

// 获取所有Element Plus图标
onMounted(() => {
  allIcons.value = Object.keys(ElementPlusIconsVue)
    .filter(key => key !== 'default')
    .sort()
})

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!searchKey.value) {
    return allIcons.value
  }
  
  return allIcons.value.filter(icon => 
    icon.toLowerCase().includes(searchKey.value.toLowerCase())
  )
})

// 清除图标
const clearIcon = () => {
  iconName.value = ''
  emit('update:modelValue', '')
  emit('change', '')
}

// 打开对话框
const showDialog = () => {
  tempSelectedIcon.value = iconName.value
  dialogVisible.value = true
  searchKey.value = ''
}

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  searchKey.value = ''
}

// 搜索图标
const searchIcons = () => {
  // 过滤器已经在计算属性中实现
}

// 选择图标
const selectIcon = (icon) => {
  tempSelectedIcon.value = icon
}

// 确认选择
const confirmSelect = () => {
  iconName.value = tempSelectedIcon.value
  emit('update:modelValue', tempSelectedIcon.value)
  emit('change', tempSelectedIcon.value)
  closeDialog()
}
</script>

<style scoped>
.icon-select {
  width: 100%;
}

.icon-search {
  margin-bottom: 16px;
}

.icon-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-height: 460px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  width: 100px;
  height: 80px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
  border-color: #409eff;
}

.icon-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
  border-color: #409eff;
}

.icon-item .el-icon {
  font-size: 20px;
  margin-bottom: 5px;
}

.icon-name {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  text-align: center;
}

.icon-empty {
  width: 100%;
  padding: 20px 0;
}
</style> 