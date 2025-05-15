<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>院系管理</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="院系名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入院系名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="院系编码" prop="code">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入院系编码"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['education:department:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['education:department:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['education:department:remove']"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 表格展示区域 -->
      <el-table v-loading="loading" :data="departmentList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="院系ID" align="center" prop="id" width="80" />
        <el-table-column label="院系名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="院系编码" align="center" prop="code" :show-overflow-tooltip="true" />
        <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              type="text"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['education:department:edit']"
            >修改</el-button>
            <el-button
              type="text"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['education:department:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 添加或修改院系对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="departmentForm" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="院系名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入院系名称" />
          </el-form-item>
          <el-form-item label="院系编码" prop="code">
            <el-input v-model="form.code" placeholder="请输入院系编码" />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入院系描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getDepartmentList, 
  getDepartmentDetail, 
  addDepartment, 
  updateDepartment, 
  deleteDepartment 
} from '@/api/department'

// 遮罩层
const loading = ref(false)
// 选中数组
const ids = ref([])
// 非单个禁用
const single = computed(() => ids.value.length !== 1)
// 非多个禁用
const multiple = computed(() => ids.value.length === 0)
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 院系表格数据
const departmentList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  code: undefined
})

// 表单参数
const form = ref({
  id: undefined,
  name: undefined,
  code: undefined,
  description: undefined
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: "院系名称不能为空", trigger: "blur" }],
  code: [{ required: true, message: "院系编码不能为空", trigger: "blur" }]
}

// 表单引用
const departmentForm = ref(null)

/** 获取院系列表 */
function getList() {
  loading.value = true
  getDepartmentList({
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    name: queryParams.value.name,
    code: queryParams.value.code
  }).then(res => {
    departmentList.value = res.data.records || []
    total.value = res.data.total || 0
    loading.value = false
  }).catch(err => {
    console.error('获取院系列表失败:', err)
    loading.value = false
    ElMessage.error('获取院系列表失败')
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    code: undefined
  }
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  title.value = "添加院系"
  open.value = true
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  
  loading.value = true
  getDepartmentDetail(id).then(res => {
    loading.value = false
    if (res.data) {
      Object.assign(form.value, res.data)
    title.value = "修改院系"
    open.value = true
    } else {
      ElMessage.warning('获取院系详情失败')
  }
  }).catch(err => {
    console.error('获取院系详情失败:', err)
    loading.value = false
    ElMessage.error('获取院系详情失败')
  })
}

/** 提交按钮 */
function submitForm() {
  departmentForm.value?.validate(valid => {
    if (valid) {
      if (form.value.id) {
        // 更新
        updateDepartment(form.value).then(res => {
          ElMessage.success("修改成功")
          open.value = false
          getList()
        }).catch(err => {
          console.error('修改院系失败:', err)
          ElMessage.error('修改院系失败')
        })
      } else {
        // 新增
        addDepartment(form.value).then(res => {
          ElMessage.success("新增成功")
      open.value = false
      getList()
        }).catch(err => {
          console.error('新增院系失败:', err)
          ElMessage.error('新增院系失败')
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const departmentIds = row.id || ids.value
  
  ElMessageBox.confirm('确认删除选中的院系信息?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // 单个删除
    if (typeof departmentIds === 'number') {
      deleteDepartment(departmentIds).then(res => {
    ElMessage.success("删除成功")
    getList()
      }).catch(err => {
        console.error('删除院系失败:', err)
        ElMessage.error('删除院系失败')
      })
    } 
    // 批量删除
    else {
      // 目前后端接口不支持批量删除，需要循环调用单个删除接口
      const deletePromises = departmentIds.map(id => deleteDepartment(id))
      Promise.all(deletePromises).then(() => {
        ElMessage.success("批量删除成功")
        getList()
      }).catch(err => {
        console.error('批量删除院系失败:', err)
        ElMessage.error('批量删除院系失败')
      })
    }
  }).catch(() => {})
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    code: undefined,
    description: undefined
  }
  departmentForm.value?.resetFields()
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

onMounted(() => {
  getList()
})
</script> 