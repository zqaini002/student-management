<template>
  <div class="right-toolbar">
    <el-row :gutter="10">
      <el-col :span="1.5">
        <el-tooltip content="刷新" placement="top" v-if="showSearch">
          <el-button circle icon="Refresh" size="small" @click="refresh"></el-button>
        </el-tooltip>
      </el-col>
      <el-col :span="1.5">
        <el-tooltip :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
          <el-button
            circle
            :icon="showSearch ? 'Hide' : 'Search'"
            size="small"
            @click="toggleSearch"
          />
        </el-tooltip>
      </el-col>
      <el-col :span="1.5">
        <el-tooltip content="密度" placement="top">
          <el-dropdown trigger="click" @command="handleSizeCommand">
            <el-button circle icon="More" size="small" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="default">默认</el-dropdown-item>
                <el-dropdown-item command="medium">中等</el-dropdown-item>
                <el-dropdown-item command="small">紧凑</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-tooltip>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "RightToolbar",
  props: {
    showSearch: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    // 显示/隐藏搜索
    toggleSearch() {
      this.$emit('update:showSearch', !this.showSearch)
    },
    // 刷新
    refresh() {
      this.$emit('queryTable')
    },
    // 设置表格大小
    handleSizeCommand(command) {
      this.$emit('sizeChange', command)
    }
  }
}
</script>

<style scoped>
.right-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-right: 10px;
}
</style> 