<template>
  <div class="role-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="queryParams.roleKey" placeholder="请输入权限标识" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">角色列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="roleList"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="角色ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="权限标识" width="150" />
        <el-table-column prop="roleSort" label="显示顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleUpdate(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              @click="handlePermission(scope.row)"
            >
              权限
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="roleFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      title="分配权限"
      v-model="permDialog.visible"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <div class="role-info">
        <span>角色名称：{{ permDialog.role.roleName }}</span>
        <span>权限标识：{{ permDialog.role.roleKey }}</span>
      </div>
      <el-form>
        <el-form-item label="权限列表">
          <el-tree
            ref="permTreeRef"
            :data="permissionOptions"
            :props="{ label: 'name', children: 'children' }"
            show-checkbox
            node-key="id"
            :default-checked-keys="permDialog.checkedKeys"
            :check-strictly="false"
            default-expand-all
            highlight-current
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitPermissions">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Plus 
} from '@element-plus/icons-vue'
import { 
  getRoleList, 
  getRoleDetail, 
  addRole, 
  updateRole, 
  deleteRole,
  changeRoleStatus,
  getRolePermissions,
  updateRolePermissions,
  getAllPermissions
} from '@/api/role'

// 数据列表
const roleList = ref([])
const total = ref(0)
const loading = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: null
})

// 表单相关
const roleFormRef = ref(null)
const dialog = reactive({
  visible: false,
  title: '',
  type: '' // add or update
})
const form = reactive({
  roleId: undefined,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ]
}

// 权限分配相关
const permTreeRef = ref(null)
const permissionOptions = ref([])
const permDialog = reactive({
  visible: false,
  role: {},
  checkedKeys: []
})

// 初始化数据
onMounted(() => {
  getList()
})

// 获取角色列表
const getList = () => {
  loading.value = true
  getRoleList(queryParams)
    .then(res => {
      console.log('角色列表响应:', res)
      roleList.value = res.data.records || []
      total.value = res.data.total || 0
    })
    .catch(err => {
      console.error('获取角色列表失败:', err)
      ElMessage.error('获取角色列表失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.roleKey = ''
  queryParams.status = null
  handleQuery()
}

// 处理页码改变
const handleCurrentChange = (pageNum) => {
  queryParams.pageNum = pageNum
  getList()
}

// 处理每页条数改变
const handleSizeChange = (pageSize) => {
  queryParams.pageSize = pageSize
  getList()
}

// 新增按钮
const handleAdd = () => {
  resetForm()
  dialog.visible = true
  dialog.title = '新增角色'
  dialog.type = 'add'
}

// 编辑按钮
const handleUpdate = (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '编辑角色'
  dialog.type = 'update'
  
  getRoleDetail(row.id)
    .then(res => {
      const data = res.data
      form.roleId = data.id
      form.roleName = data.roleName
      form.roleKey = data.roleKey
      form.roleSort = data.roleSort
      form.status = data.status
      form.remark = data.remark
    })
    .catch(err => {
      console.error('获取角色详情失败:', err)
      ElMessage.error('获取角色详情失败')
    })
}

// 删除按钮
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteRole(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getList()
      })
      .catch(err => {
        console.error('删除角色失败:', err)
        ElMessage.error('删除角色失败')
      })
  }).catch(() => {})
}

// 状态变更
const handleStatusChange = (row) => {
  const data = {
    roleId: row.id,
    status: row.status
  }
  
  changeRoleStatus(data)
    .then(() => {
      ElMessage.success(`已${row.status === 0 ? '启用' : '禁用'}角色`)
    })
    .catch(err => {
      console.error('修改角色状态失败:', err)
      ElMessage.error('修改角色状态失败')
      // 状态修改失败，恢复原状态
      row.status = row.status === 0 ? 1 : 0
    })
}

// 提交表单
const submitForm = () => {
  roleFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    // 创建表单数据副本
    const formData = {...form}
    // 确保数值类型字段为数值
    formData.roleSort = Number(formData.roleSort)
    formData.status = Number(formData.status)
    
    // 如果是编辑模式，将roleId字段改名为id
    if (dialog.type === 'update') {
      formData.id = formData.roleId
      delete formData.roleId
    }
    
    if (dialog.type === 'add') {
      addRole(formData)
        .then(() => {
          ElMessage.success('添加成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('添加角色失败:', err)
          ElMessage.error('添加角色失败')
        })
    } else {
      updateRole(formData)
        .then(() => {
          ElMessage.success('修改成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('修改角色失败:', err)
          ElMessage.error('修改角色失败')
        })
    }
  })
}

// 重置表单
const resetForm = () => {
  form.roleId = undefined
  form.roleName = ''
  form.roleKey = ''
  form.roleSort = 0
  form.status = 0
  form.remark = ''
}

// 权限分配
const handlePermission = (row) => {
  permDialog.role = row
  permDialog.visible = true
  permDialog.checkedKeys = [] // 先清空选中项
  
  // 先获取所有权限选项，构建树结构
  getAllPermissions()
    .then(res => {
      // 将权限数组转换为树形结构
      const permissions = res.data || []
      const permTree = []
      
      // 按模块分组
      const moduleMap = {}
      permissions.forEach(perm => {
        const parts = perm.split(':')
        const module = parts[0]
        
        if (!moduleMap[module]) {
          moduleMap[module] = {
            id: module,
            name: getModuleName(module),
            children: []
          }
          permTree.push(moduleMap[module])
        }
        
        // 如果有子模块
        if (parts.length > 2) {
          const subModule = parts[0] + ':' + parts[1]
          let subModuleNode = moduleMap[module].children.find(item => item.id === subModule)
          
          if (!subModuleNode) {
            subModuleNode = {
              id: subModule,
              name: getSubModuleName(parts[1]),
              children: []
            }
            moduleMap[module].children.push(subModuleNode)
          }
          
          subModuleNode.children.push({
            id: perm,
            name: getActionName(parts[2])
          })
        } else {
          // 没有子模块
          moduleMap[module].children.push({
            id: perm,
            name: getActionName(parts[1])
          })
        }
      })
      
      permissionOptions.value = permTree
      
      // 构建树后再获取角色权限列表
      getRolePermissions(row.id)
        .then(res => {
          // 获取当前角色的权限列表
          const rolePerms = res.data || []
          console.log('角色权限列表:', rolePerms)
          permDialog.checkedKeys = rolePerms // 将后端返回的权限标识设置为选中项
          
          // 强制更新树组件的选中状态
          setTimeout(() => {
            if (permTreeRef.value) {
              permTreeRef.value.setCheckedKeys(rolePerms)
            }
          }, 100)
        })
        .catch(err => {
          console.error('获取角色权限失败:', err)
          ElMessage.error('获取角色权限失败')
        })
    })
    .catch(err => {
      console.error('获取权限选项失败:', err)
      ElMessage.error('获取权限选项失败')
    })
}

// 获取模块名称
const getModuleName = (module) => {
  const moduleNames = {
    'system': '系统管理',
    'education': '教育管理',
    'course': '课程管理',
    'attendance': '考勤管理',
    'grade': '成绩管理',
    'monitor': '系统监控',
    'tool': '系统工具',
    'job': '定时任务',
    'common': '通用功能',
    'log': '日志管理'
  }
  return moduleNames[module] || module
}

// 获取子模块名称
const getSubModuleName = (subModule) => {
  const subModuleNames = {
    'user': '用户管理',
    'role': '角色管理',
    'menu': '菜单管理',
    'permission': '权限管理',
    'dept': '部门管理',
    'department': '院系管理',
    'major': '专业管理',
    'class': '班级管理',
    'student': '学生管理',
    'teacher': '教师管理',
    'course': '课程管理',
    'offering': '课程开设',
    'selection': '选课管理',
    'score': '成绩管理',
    'record': '考勤记录',
    'stats': '统计分析',
    'setting': '系统设置',
    'dict': '字典管理',
    'config': '参数配置',
    'notice': '通知公告',
    'log': '日志管理',
    'online': '在线用户',
    'job': '定时任务',
    'druid': '数据监控',
    'server': '服务监控'
  }
  return subModuleNames[subModule] || subModule
}

// 获取操作名称
const getActionName = (action) => {
  const actionNames = {
    'list': '列表',
    'query': '查询',
    'add': '新增',
    'create': '创建',
    'edit': '编辑',
    'update': '更新',
    'delete': '删除',
    'remove': '删除',
    'export': '导出',
    'import': '导入',
    'upload': '上传',
    'download': '下载',
    'resetPwd': '重置密码',
    'operate': '操作',
    'stats': '统计',
    'view': '查看',
    'detail': '详情',
    'start': '启动',
    'stop': '停止',
    'run': '执行',
    'all': '所有'
  }
  return actionNames[action] || action
}

// 提交权限分配
const submitPermissions = () => {
  if (!permTreeRef.value) {
    ElMessage.error('权限树未初始化')
    return
  }

  if (!permDialog.role || !permDialog.role.id) {
    ElMessage.error('角色信息不完整')
    return
  }
  
  // 获取选中的权限ID列表
  const checkedKeys = permTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys()
  
  // 包含所有选中权限的完整列表
  const allSelectedKeys = [...new Set([...checkedKeys, ...halfCheckedKeys])]
  
  // 只选择包含冒号的权限标识(过滤掉模块和子模块节点)
  const permIds = allSelectedKeys
    .filter(id => typeof id === 'string' && id.includes(':'))
  
  console.log('提交的权限:', permIds)
  
  // 提交更新
  const data = {
    roleId: permDialog.role.id,
    menuIds: permIds
  }
  
  // 显示加载提示
  loading.value = true
  
  updateRolePermissions(data)
    .then(res => {
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: '权限分配成功',
          duration: 1500
        })
        permDialog.visible = false
      } else {
        ElMessage.error(res.message || '权限分配失败')
      }
    })
    .catch(err => {
      console.error('权限分配失败:', err)
      ElMessage.error('权限分配失败: ' + (err.message || '未知错误'))
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style lang="scss" scoped>
.role-container {
  padding: 10px;
  
  .search-card {
    margin-bottom: 20px;
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      
      .el-form-item {
        margin-right: 10px;
        margin-bottom: 15px;
      }
    }
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-title {
        font-size: 16px;
        font-weight: 600;
      }
      
      .header-buttons {
        display: flex;
        gap: 10px;
      }
    }
    
    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .role-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    padding: 10px;
    background-color: #f8f8f8;
    border-radius: 4px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style> 