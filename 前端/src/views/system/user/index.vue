<template>
  <div class="user-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 240px;">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="queryParams.userType" placeholder="请选择用户类型" clearable style="width: 240px;">
            <el-option label="管理员" :value="0" />
            <el-option label="教师" :value="1" />
            <el-option label="学生" :value="2" />
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
          <span class="header-title">用户列表</span>
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
        :data="userList"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTag(scope.row.userType)">{{ getUserTypeText(scope.row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号码" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="statusDesc" label="状态" width="120" align="center">
          <template #default="scope">
            <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
              <span>{{ scope.row.statusDesc }}</span>
              <el-switch
                v-model="scope.row.status"
                :active-value="0"
                :inactive-value="1"
                @change="handleStatusChange(scope.row)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <!-- 编辑按钮 -->
            <el-button 
              type="primary" 
              link 
              @click="handleUpdate(scope.row)" 
            >
              编辑
            </el-button>
            <!-- 重置密码按钮 -->
            <el-button 
              type="primary" 
              link 
              @click="handleResetPassword(scope.row)"
            >
              重置密码
            </el-button>
            <!-- 删除按钮 -->
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            :disabled="dialog.type === 'update'"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%;">
            <el-option label="管理员" :value="0" />
            <el-option label="教师" :value="1" />
            <el-option label="学生" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialog.type === 'add'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      title="重置密码"
      v-model="resetPwdDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="resetPwdFormRef"
        :model="resetPwdForm"
        :rules="resetPwdRules"
        label-width="80px"
      >
        <el-form-item label="用户名">
          <el-input v-model="resetPwdForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPwdDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitResetPwd">确定</el-button>
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
  getUserList, 
  getUserDetail, 
  addUser, 
  updateUser, 
  deleteUser,
  resetUserPassword,
  changeUserStatus
} from '@/api/user'

// 数据列表
const userList = ref([])
const total = ref(0)
const loading = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  name: '',
  phone: '',
  status: null
})

// 表单相关
const userFormRef = ref(null)
const dialog = reactive({
  visible: false,
  title: '',
  type: '' // add or update
})
const form = reactive({
  userId: undefined,
  username: '',
  name: '',
  userType: 0,
  email: '',
  phone: '',
  password: '',
  status: 0
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[\w.-]+@[\w.-]+\.\w+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 重置密码相关
const resetPwdFormRef = ref(null)
const resetPwdDialog = reactive({
  visible: false,
  userId: undefined
})
const resetPwdForm = reactive({
  userId: undefined,
  username: '',
  password: '',
  confirmPassword: ''
})

// 重置密码表单验证规则
const resetPwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPwdForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 初始化数据
onMounted(() => {
  getList()
})

// 获取用户列表
const getList = () => {
  loading.value = true
  getUserList(queryParams)
    .then(res => {
      userList.value = res.data.list || []
      total.value = res.data.total || 0
    })
    .catch(err => {
      console.error('获取用户列表失败:', err)
      ElMessage.error('获取用户列表失败')
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
  queryParams.username = ''
  queryParams.name = ''
  queryParams.phone = ''
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
  dialog.title = '新增用户'
  dialog.type = 'add'
}

// 编辑按钮
const handleUpdate = (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '编辑用户'
  dialog.type = 'update'
  
  getUserDetail(row.id)
    .then(res => {
      const data = res.data
      form.userId = data.id
      form.username = data.username
      form.name = data.name
      form.userType = data.userType
      form.email = data.email
      form.phone = data.phone
      form.status = data.status
    })
    .catch(err => {
      console.error('获取用户详情失败:', err)
      ElMessage.error('获取用户详情失败')
    })
}

// 删除按钮
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteUser(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getList()
      })
      .catch(err => {
        console.error('删除用户失败:', err)
        ElMessage.error('删除用户失败')
      })
  }).catch(() => {})
}

// 重置密码
const handleResetPassword = (row) => {
  resetPwdForm.userId = row.id
  resetPwdForm.username = row.username
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
  resetPwdDialog.visible = true
}

// 提交重置密码
const submitResetPwd = () => {
  resetPwdFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    const data = {
      userId: Number(resetPwdForm.userId),
      password: resetPwdForm.password
    }
    
    resetUserPassword(data)
      .then(() => {
        ElMessage.success('重置密码成功')
        resetPwdDialog.visible = false
      })
      .catch(err => {
        console.error('重置密码失败:', err)
        ElMessage.error('重置密码失败')
      })
  })
}

// 状态变更
const handleStatusChange = (row) => {
  const data = {
    userId: row.id,
    status: row.status
  }
  
  changeUserStatus(data)
    .then(() => {
      ElMessage.success(`已${row.status === 0 ? '启用' : '禁用'}用户`)
    })
    .catch(err => {
      console.error('修改用户状态失败:', err)
      ElMessage.error('修改用户状态失败')
      // 状态修改失败，恢复原状态
      row.status = row.status === 0 ? 1 : 0
    })
}

// 提交表单
const submitForm = () => {
  userFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    // 创建表单数据副本
    const formData = {...form}
    // 确保数值类型字段为数值
    formData.userType = Number(formData.userType)
    formData.status = Number(formData.status)
    
    // 如果是编辑模式，把userId改名为id
    if (dialog.type === 'update') {
      formData.id = formData.userId
      delete formData.userId
    }
    
    if (dialog.type === 'add') {
      addUser(formData)
        .then(() => {
          ElMessage.success('添加成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('添加用户失败:', err)
          ElMessage.error('添加用户失败')
        })
    } else {
      updateUser(formData)
        .then(() => {
          ElMessage.success('修改成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('修改用户失败:', err)
          ElMessage.error('修改用户失败')
        })
    }
  })
}

// 重置表单
const resetForm = () => {
  form.userId = undefined
  form.username = ''
  form.name = ''
  form.userType = 0
  form.email = ''
  form.phone = ''
  form.password = ''
  form.status = 0
}

// 获取用户类型文本
const getUserTypeText = (userType) => {
  switch (userType) {
    case 0:
      return '管理员'
    case 1:
      return '教师'
    case 2:
      return '学生'
    default:
      return '未知'
  }
}

// 获取用户类型标签样式
const getUserTypeTag = (userType) => {
  switch (userType) {
    case 0:
      return 'danger'
    case 1:
      return 'warning'
    case 2:
      return 'success'
    default:
      return 'info'
  }
}
</script>

<style lang="scss" scoped>
.user-container {
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
      
      .el-select {
        width: 240px;
      }
      
      /* 确保下拉菜单宽度足够 */
      :deep(.el-select__popper) {
        min-width: 240px !important;
      }
      
      .el-input {
        min-width: 200px;
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
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style> 