<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>专业管理</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="专业名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入专业名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="专业编码" prop="code">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入专业编码"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属院系" prop="departmentId">
          <el-select
            v-model="queryParams.departmentId"
            placeholder="请选择所属院系"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 表格展示区域 -->
      <el-table v-loading="loading" :data="majorList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="专业ID" align="center" prop="id" width="80" />
        <el-table-column label="专业名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="专业编码" align="center" prop="code" :show-overflow-tooltip="true" />
        <el-table-column label="所属院系" align="center" prop="departmentName" :show-overflow-tooltip="true" />
        <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              type="text"
              icon="Edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              type="text"
              icon="Delete"
              @click="handleDelete(scope.row)"
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

      <!-- 添加或修改专业对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="majorFormRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="专业名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入专业名称" />
          </el-form-item>
          <el-form-item label="专业编码" prop="code">
            <el-input v-model="form.code" placeholder="请输入专业编码" />
          </el-form-item>
          <el-form-item label="所属院系" prop="departmentId">
            <el-select v-model="form.departmentId" placeholder="请选择所属院系" style="width: 100%">
              <el-option
                v-for="item in departmentOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入专业描述" />
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
import { listMajor, addMajor, updateMajor, deleteMajor } from '@/api/education/major'
import { getAllDepartments } from '@/api/department'

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
// 专业表格数据
const majorList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 院系选项
const departmentOptions = ref([])

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  code: undefined,
  departmentId: undefined
})

// 表单参数
const form = ref({
  id: undefined,
  name: undefined,
  code: undefined,
  departmentId: undefined,
  description: undefined
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: "专业名称不能为空", trigger: "blur" }],
  code: [{ required: true, message: "专业编码不能为空", trigger: "blur" }],
  departmentId: [{ required: true, message: "所属院系不能为空", trigger: "change" }]
}

// 表单引用
const majorFormRef = ref(null)

/** 查询专业列表 */
function getList() {
  loading.value = true
  
  listMajor(queryParams.value).then(res => {
    if (res.code === 200) {
      majorList.value = res.data.records || res.data
      total.value = res.data.total || majorList.value.length
    } else {
      ElMessage.error(res.msg || '获取专业列表失败')
      majorList.value = []
      total.value = 0
    }
    loading.value = false
  }).catch(err => {
    console.error('获取专业列表失败:', err)
    ElMessage.error('获取专业列表失败，请稍后重试')
    loading.value = false
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
    code: undefined,
    departmentId: undefined
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
  title.value = "添加专业"
  open.value = true
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  // 模拟获取详情
  const major = majorList.value.find(item => item.id === id)
  if (major) {
    Object.assign(form.value, major)
    title.value = "修改专业"
    open.value = true
  }
}

/** 提交按钮 */
function submitForm() {
  majorFormRef.value.validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        // 更新专业
        updateMajor(form.value).then(res => {
          if (res.code === 200) {
            ElMessage.success('修改成功')
            open.value = false
            getList()
          } else {
            ElMessage.error(res.msg || '修改失败')
          }
        }).catch(err => {
          console.error('修改专业失败:', err)
          ElMessage.error('修改失败，请稍后重试')
        })
      } else {
        // 新增专业
        addMajor(form.value).then(res => {
          if (res.code === 200) {
            ElMessage.success('新增成功')
      open.value = false
      getList()
          } else {
            ElMessage.error(res.msg || '新增失败')
          }
        }).catch(err => {
          console.error('新增专业失败:', err)
          ElMessage.error('新增失败，请稍后重试')
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const majorIds = row.id || ids.value
  const majorNames = row.name || majorList.value.filter(m => ids.value.includes(m.id)).map(m => m.name).join(',')
  
  ElMessageBox.confirm(`确认删除专业 ${majorNames} 吗?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteMajor(majorIds).then(res => {
      if (res.code === 200) {
    ElMessage.success("删除成功")
    getList()
      } else {
        ElMessage.error(res.msg || "删除失败")
      }
    }).catch(err => {
      console.error("删除专业失败:", err)
      ElMessage.error("删除失败，请稍后重试")
    })
  }).catch(() => {
    // 取消删除操作
  })
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    code: undefined,
    departmentId: undefined,
    description: undefined
  }
  majorFormRef.value?.resetFields()
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 获取院系列表 */
function getDepartments() {
  getAllDepartments().then(res => {
    if (res.code === 200) {
      departmentOptions.value = res.data
    } else {
      ElMessage.error(res.msg || '获取院系列表失败')
    }
  }).catch(err => {
    console.error('获取院系列表失败:', err)
    ElMessage.error('获取院系列表失败，请稍后重试')
  })
}

onMounted(() => {
  getDepartments()
  getList()
})
</script> 