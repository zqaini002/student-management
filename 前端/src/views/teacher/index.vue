<template>
  <div class="teacher-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="教师编号" prop="teacherNo">
          <el-input id="search-teacherNo" v-model="queryParams.teacherNo" placeholder="请输入教师编号" clearable />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input id="search-name" v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="院系" prop="departmentId">
          <el-select id="search-departmentId" v-model="queryParams.departmentId" placeholder="请选择院系" clearable>
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select id="search-title" v-model="queryParams.title" placeholder="请选择职称" clearable>
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select id="search-status" v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="在职" :value="0" />
            <el-option label="离职" :value="1" />
            <el-option label="退休" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="primary" plain @click="toggleMoreSearch">
            {{ showMoreSearch ? '收起' : '展开' }}
            <el-icon><el-icon-arrow-down v-if="!showMoreSearch" /><el-icon-arrow-up v-else /></el-icon>
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 高级搜索区域 -->
      <div v-if="showMoreSearch" class="more-search">
        <el-form :model="queryParams" :inline="true">
          <el-form-item label="邮箱" prop="email">
            <el-input id="search-email" v-model="queryParams.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input id="search-phone" v-model="queryParams.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item label="出生日期" prop="birthdayRange">
            <el-date-picker
              v-model="birthdayRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">教师信息列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd" v-hasPerms="['education:teacher:add']">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button 
              type="danger" 
              :disabled="selection.length === 0" 
              @click="handleBatchDelete"
              v-hasPerms="['education:teacher:delete']"
            >
              批量删除
            </el-button>
            <el-dropdown @command="handleBatchCommand" v-if="hasPermission('education:teacher:edit')">
              <el-button type="primary" plain :disabled="selection.length === 0">
                批量操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="setStatusActive">设为在职</el-dropdown-item>
                  <el-dropdown-item command="setStatusInactive">设为离职</el-dropdown-item>
                  <el-dropdown-item command="setStatusRetired">设为退休</el-dropdown-item>
                  <el-dropdown-item divided command="resetPwd">重置密码</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-upload
              class="upload-demo"
              :show-file-list="false"
              :before-upload="handleImport"
              v-hasPerms="['education:teacher:import']"
            >
              <el-button type="primary" :loading="importLoading" plain>
                <el-icon><Upload /></el-icon>导入
              </el-button>
            </el-upload>
            <el-button 
              type="primary" 
              plain
              @click="handleExport"
              :loading="exportLoading"
              v-hasPerms="['education:teacher:export']"
            >
              <el-icon><Download /></el-icon>导出
            </el-button>
            <el-button 
              type="primary" 
              plain
              @click="handleDownloadTemplate"
              v-hasPerms="['education:teacher:import']"
            >
              下载模板
            </el-button>
            <el-tooltip content="设置显示列">
              <el-button type="primary" plain @click="showColumnSettings = true">
                <el-icon><setting /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="teacherList"
        stripe
        border
        @selection-change="handleSelectionChange"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="teacherNo" label="教师编号" width="120" v-if="columnSettings.teacherNo" />
        <el-table-column prop="name" label="姓名" width="100" v-if="columnSettings.name" />
        <el-table-column prop="genderDesc" label="性别" width="60" align="center" v-if="columnSettings.gender" />
        <el-table-column prop="departmentName" label="院系" width="180" v-if="columnSettings.departmentName" />
        <el-table-column prop="title" label="职称" width="100" v-if="columnSettings.title" />
        <el-table-column prop="phone" label="手机号码" width="130" v-if="columnSettings.phone" />
        <el-table-column prop="email" label="邮箱" width="180" v-if="columnSettings.email" />
        <el-table-column prop="entryDate" label="入职日期" width="120" align="center" v-if="columnSettings.entryDate" />
        <el-table-column prop="birthday" label="出生日期" width="120" align="center" v-if="columnSettings.birthday" />
        <el-table-column prop="statusDesc" label="状态" width="80" align="center" v-if="columnSettings.status">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="handleUpdate(scope.row)" 
              v-hasPerms="['education:teacher:edit']"
            >
              编辑
            </el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleViewDetail(scope.row)"
              v-hasPerms="['education:teacher:query']"
            >
              详情
            </el-button>
            <el-dropdown @command="handleCommand(scope.row, $event)">
              <el-button link type="primary">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="viewCourses">查看课程</el-dropdown-item>
                  <el-dropdown-item command="viewSchedule">查看课表</el-dropdown-item>
                  <el-dropdown-item command="viewEvaluations">教学评价</el-dropdown-item>
                  <el-dropdown-item command="resetPassword" v-if="hasPermission('education:teacher:resetPwd')">重置密码</el-dropdown-item>
                  <el-dropdown-item divided command="delete" class="text-danger" v-if="hasPermission('education:teacher:delete')">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 新增/编辑教师对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="800px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="teacherFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="教师编号" prop="teacherNo">
                  <el-input 
                    id="form-teacherNo"
                    v-model="form.teacherNo" 
                    placeholder="请输入教师编号" 
                    :disabled="dialog.type === 'update'"
                    @input="handleTeacherNoChange"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input id="form-name" v-model="form.name" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group id="form-gender" v-model="form.gender">
                    <el-radio :value="0">男</el-radio>
                    <el-radio :value="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="院系" prop="departmentId">
                  <el-select id="form-departmentId" v-model="form.departmentId" placeholder="请选择院系" style="width: 100%">
                    <el-option
                      v-for="item in departmentList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-select id="form-title" v-model="form.title" placeholder="请选择职称" style="width: 100%">
                    <el-option label="教授" value="教授" />
                    <el-option label="副教授" value="副教授" />
                    <el-option label="讲师" value="讲师" />
                    <el-option label="助教" value="助教" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期" prop="entryDate">
                  <el-date-picker
                    id="form-entryDate"
                    v-model="form.entryDate"
                    type="date"
                    placeholder="请选择入职日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input id="form-idCard" v-model="form.idCard" placeholder="请输入身份证号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthday">
                  <el-date-picker
                    id="form-birthday"
                    v-model="form.birthday"
                    type="date"
                    placeholder="请选择出生日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select id="form-status" v-model="form.status" placeholder="请选择状态" style="width: 100%">
                    <el-option label="在职" :value="0" />
                    <el-option label="离职" :value="1" />
                    <el-option label="退休" :value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="账号信息" name="account">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input id="form-username" v-model="form.username" placeholder="请输入用户名" :disabled="dialog.type === 'update'" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password" v-if="dialog.type === 'add'">
                  <el-input id="form-password" v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input id="form-email" v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input id="form-phone" v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 列设置对话框 -->
    <el-dialog
      title="表格列设置"
      v-model="showColumnSettings"
      width="400px"
      append-to-body
    >
      <el-checkbox-group v-model="checkedColumns">
        <el-row :gutter="20">
          <el-col :span="12" v-for="item in allColumns" :key="item.prop">
            <el-checkbox :label="item.prop">{{ item.label }}</el-checkbox>
          </el-col>
        </el-row>
      </el-checkbox-group>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showColumnSettings = false">取消</el-button>
          <el-button type="primary" @click="applyColumnSettings">确定</el-button>
      </div>
      </template>
    </el-dialog>
    
    <!-- 教师详情对话框 -->
    <el-dialog
      title="教师详情"
      v-model="detailDialog.visible"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="教师编号">{{ teacherDetail.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ teacherDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ teacherDetail.gender === 0 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ teacherDetail.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ teacherDetail.title }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ teacherDetail.entryDate }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ teacherDetail.idCard }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ teacherDetail.birthday }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(teacherDetail.status)">
            {{ ['在职', '离职', '退休'][teacherDetail.status] || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ teacherDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ teacherDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ teacherDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="用户名" :span="2">{{ teacherDetail.username }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialog.visible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Plus,
  Upload,
  Download,
  ArrowDown,
  Setting
} from '@element-plus/icons-vue'
import { 
  getTeacherList, 
  getTeacherDetail, 
  addTeacher, 
  updateTeacher, 
  deleteTeacher,
  resetTeacherPassword,
  importTeacher,
  exportTeacher,
  downloadTeacherTemplate,
  updateTeacherStatus,
  getTeacherCourses,
  getTeacherSchedule,
  getTeacherEvaluations
} from '@/api/teacher'
import { getAllDepartments } from '@/api/department'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 创建路由实例
const router = useRouter()

// 数据列表
const teacherList = ref([])
const total = ref(0)
const loading = ref(false)
const importLoading = ref(false)
const exportLoading = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  teacherNo: '',
  name: '',
  departmentId: null,
  title: '',
  status: null
})

// 院系列表
const departmentList = ref([])

// 获取院系列表
const getDepartmentList = async () => {
  try {
    const res = await getAllDepartments()
    if (res && res.data) {
      departmentList.value = res.data
    }
  } catch (err) {
      console.error('获取院系列表失败:', err)
      ElMessage.error('获取院系列表失败')
  }
}

// 表单相关
const activeTab = ref('basic')
const teacherFormRef = ref(null)
const queryForm = ref(null)
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})
const form = reactive({
  id: undefined,
  teacherNo: '',
  name: '',
  departmentId: null,
  title: '',
  gender: 0,
  idCard: '',
  birthday: '',
  entryDate: '',
  status: 0,
  username: '',
  password: '123456',
  email: '',
  phone: ''
})

// 表单验证规则
const rules = {
  teacherNo: [
    { required: true, message: '请输入教师编号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '教师编号只能包含字母和数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  entryDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
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

// 教师详情
const detailDialog = reactive({
  visible: false
})
const teacherDetail = ref({})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (e) {
    return dateStr
  }
}

// 初始化数据
onMounted(() => {
  getList()
  getDepartmentList()
  
  // 处理标签关联
  nextTick(() => {
    setupLabelForAttributes()
  })
})

// 设置label的for属性
const setupLabelForAttributes = () => {
  // 搜索表单标签处理
  document.querySelectorAll('.teacher-container .el-form-item').forEach(item => {
    const label = item.querySelector('.el-form-item__label')
    const input = item.querySelector('input, select, textarea')
    
    // 忽略daterange类型的日期选择器，它们需要特殊处理
    const isDateRangePicker = item.querySelector('.el-date-editor--daterange')
    
    if (label && input && input.id && !isDateRangePicker) {
      label.setAttribute('for', input.id)
    }
  })
}

// 获取教师列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getTeacherList(queryParams)
    teacherList.value = res.data.list.map(item => ({
          ...item,
          genderDesc: item.gender === 0 ? '男' : '女',
      statusDesc: ['在职', '离职', '退休'][item.status] || '未知'
    }))
    total.value = res.data.total
  } catch (err) {
      console.error('获取教师列表失败:', err)
      ElMessage.error('获取教师列表失败')
  } finally {
      loading.value = false
  }
}

// 重写查询处理，考虑日期范围
const handleQuery = () => {
  queryParams.pageNum = 1;
  
  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.entryDateStart = dateRange.value[0];
    queryParams.entryDateEnd = dateRange.value[1];
  } else {
    queryParams.entryDateStart = undefined;
    queryParams.entryDateEnd = undefined;
  }
  
  // 处理出生日期范围
  if (birthdayRange.value && birthdayRange.value.length === 2) {
    queryParams.birthdayStart = birthdayRange.value[0];
    queryParams.birthdayEnd = birthdayRange.value[1];
  } else {
    queryParams.birthdayStart = undefined;
    queryParams.birthdayEnd = undefined;
  }
  
  getList();
};

// 重写重置查询
const resetQuery = () => {
  queryForm.value?.resetFields();
  dateRange.value = [];
  birthdayRange.value = [];
  handleQuery();
};

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
  dialog.title = '新增教师'
  dialog.type = 'add'
  
  nextTick(() => {
    teacherFormRef.value?.clearValidate()
    setupLabelForAttributes()
  })
}

// 编辑按钮
const handleUpdate = (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '编辑教师'
  dialog.type = 'update'
  detailDialog.visible = false // 关闭详情对话框（如果有打开）
  
  // 获取教师详情
  getTeacherDetail(row.id)
    .then(res => {
      const data = res.data
      form.id = data.id
      form.teacherNo = data.teacherNo
      form.name = data.name
      form.departmentId = data.departmentId
      form.title = data.title
      form.gender = data.gender
      form.idCard = data.idCard
      form.birthday = data.birthday ? new Date(data.birthday) : null
      form.entryDate = data.entryDate ? new Date(data.entryDate) : null
      form.status = data.status
      form.username = data.username
      form.email = data.email
      form.phone = data.phone
      
      nextTick(() => {
        setupLabelForAttributes()
      })
    })
    .catch(err => {
      console.error('获取教师详情失败:', err)
      ElMessage.error('获取教师详情失败')
    })
  
  nextTick(() => {
    teacherFormRef.value?.clearValidate()
    })
}

// 查看详情
const handleViewDetail = (row) => {
  detailDialog.visible = true
  getTeacherDetail(row.id)
    .then(res => {
      // 格式化日期字段
      const data = {...res.data}
      data.birthday = formatDate(data.birthday)
      data.entryDate = formatDate(data.entryDate)
      data.createTime = formatDate(data.createTime)
      teacherDetail.value = data
    })
    .catch(err => {
      console.error('获取教师详情失败:', err)
      ElMessage.error('获取教师详情失败')
    })
}

// 删除按钮
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除教师 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeacher(row.id)
        ElMessage.success('删除成功')
        getList()
    } catch (err) {
        console.error('删除教师失败:', err)
        ElMessage.error('删除教师失败')
    }
  }).catch(() => {})
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(`确定要重置教师 ${row.name} 的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetTeacherPassword(row.id)
      ElMessage.success('密码重置成功')
    } catch (err) {
      console.error('密码重置失败:', err)
      ElMessage.error('密码重置失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!teacherFormRef.value) return
  
  await teacherFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        const saveFunc = dialog.type === 'add' ? addTeacher : updateTeacher
        await saveFunc(form)
        ElMessage.success(dialog.type === 'add' ? '新增成功' : '修改成功')
          dialog.visible = false
          getList()
      } catch (err) {
        console.error(dialog.type === 'add' ? '新增失败' : '修改失败', err)
        ElMessage.error(err.response?.data?.message || (dialog.type === 'add' ? '新增失败' : '修改失败'))
      }
    } else {
      console.log('表单校验不通过', fields)
      // 切换到错误所在的标签页
      if (Object.keys(fields).some(key => ['username', 'password', 'email', 'phone'].includes(key))) {
        activeTab.value = 'account'
      } else {
        activeTab.value = 'basic'
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.teacherNo = ''
  form.name = ''
  form.departmentId = null
  form.title = ''
  form.gender = 0
  form.idCard = ''
  form.birthday = ''
  form.entryDate = ''
  form.status = 0
  form.username = ''
  form.password = '123456'
  form.email = ''
  form.phone = ''
  activeTab.value = 'basic'
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'success'
    case 1:
      return 'danger'
    case 2:
      return 'info'
    default:
      return 'warning'
  }
}

// 监听教师编号变化自动填充用户名
const handleTeacherNoChange = () => {
  if (form.teacherNo && !form.username) {
    form.username = form.teacherNo
  }
}

// 导入教师数据
const handleImport = (uploadFile) => {
  importLoading.value = true
  
  importTeacher(uploadFile.raw).then(res => {
    ElMessage.success('导入成功')
    getList()
  }).catch(err => {
    console.error('导入失败', err)
    ElMessage.error('导入失败')
  }).finally(() => {
    importLoading.value = false
  })
}

// 导出教师数据
const handleExport = () => {
  exportLoading.value = true
  
  exportTeacher(queryParams).then(res => {
    // 处理文件下载
    const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
    const fileName = `教师列表_${new Date().getTime()}.xlsx`
    
    if (navigator.msSaveBlob) {
      // IE
      navigator.msSaveBlob(blob, fileName)
    } else {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = fileName
      link.click()
      URL.revokeObjectURL(link.href)
    }
    
    ElMessage.success('导出成功')
  }).catch(err => {
    console.error('导出失败', err)
    ElMessage.error('导出失败')
  }).finally(() => {
    exportLoading.value = false
  })
}

// 下载模板
const handleDownloadTemplate = () => {
  downloadTeacherTemplate().then(res => {
    // 处理文件下载
    const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
    const fileName = '教师导入模板.xlsx'
    
    if (navigator.msSaveBlob) {
      // IE
      navigator.msSaveBlob(blob, fileName)
    } else {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = fileName
      link.click()
      URL.revokeObjectURL(link.href)
    }
    
    ElMessage.success('模板下载成功')
  }).catch(err => {
    console.error('模板下载失败', err)
    ElMessage.error('模板下载失败')
  })
}

// 多选框选中数据
const selection = ref([]);

// 高级搜索开关
const showMoreSearch = ref(false);

// 日期范围选择
const dateRange = ref([]);
const birthdayRange = ref([]);

// 列设置
const showColumnSettings = ref(false);
const columnSettings = reactive({
  teacherNo: true,
  name: true,
  gender: true,
  departmentName: true,
  title: true,
  phone: true,
  email: true,
  entryDate: true,
  birthday: true,
  status: true
});
const allColumns = [
  { prop: 'teacherNo', label: '教师编号' },
  { prop: 'name', label: '姓名' },
  { prop: 'gender', label: '性别' },
  { prop: 'departmentName', label: '院系' },
  { prop: 'title', label: '职称' },
  { prop: 'phone', label: '手机号码' },
  { prop: 'email', label: '邮箱' },
  { prop: 'entryDate', label: '入职日期' },
  { prop: 'birthday', label: '出生日期' },
  { prop: 'status', label: '状态' }
];
const checkedColumns = ref(Object.keys(columnSettings).filter(key => columnSettings[key]));

// 切换高级搜索
const toggleMoreSearch = () => {
  showMoreSearch.value = !showMoreSearch.value;
};

// 多选框选中数据
const handleSelectionChange = (val) => {
  selection.value = val;
};

// 批量删除
const handleBatchDelete = () => {
  if (selection.value.length === 0) {
    ElMessage.warning('请至少选择一条记录');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selection.value.length} 名教师吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 执行批量删除
      // 实际项目中可能需要调用批量删除接口
      const promises = selection.value.map(item => deleteTeacher(item.id));
      await Promise.all(promises);
      
      ElMessage.success('批量删除成功');
      getList();
    } catch (err) {
      console.error('批量删除失败', err);
      ElMessage.error('批量删除失败');
    }
  }).catch(() => {
    // 取消删除
  });
};

// 查看课程
const handleViewCourses = (row) => {
  // 后端API尚未实现完整的课程路由，使用消息提示
  ElMessage.info(`将显示教师 ${row.name} 的课程信息`);
  // 在实际项目中，可以使用以下代码替换消息提示
  // 或者打开一个对话框来显示教师的课程列表
  // router.push(`/teacher/courses/${row.id}`);
  
  ElMessageBox.confirm(`是否查看教师 ${row.name} 的课程信息？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
  }).catch(() => {})
};

// 查看课表
const handleViewSchedule = (row) => {
  // 后端API尚未实现完整的课表路由，使用消息提示
  ElMessage.info(`将显示教师 ${row.name} 的课表信息`);
  
  // 在实际项目中，可以使用以下代码替换消息提示
  // 或者打开一个对话框来显示教师的课表
  ElMessageBox.confirm(`是否查看教师 ${row.name} 的课程表？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
  }).catch(() => {})
};

// 查看教学评价
const handleViewEvaluations = (row) => {
  // 后端API尚未实现完整的教学评价路由，使用消息提示
  ElMessage.info(`将显示教师 ${row.name} 的教学评价信息`);
  
  // 在实际项目中，可以使用以下代码替换消息提示
  // 或者打开一个对话框来显示教师的教学评价结果
  ElMessageBox.confirm(`是否查看教师 ${row.name} 的教学评价？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
  }).catch(() => {})
};

// 批量操作命令处理
const handleBatchCommand = async (command) => {
  if (selection.value.length === 0) {
    ElMessage.warning('请至少选择一条记录');
    return;
  }
  
  try {
    switch (command) {
      case 'setStatusActive':
        await batchUpdateStatus(0, '批量设置为在职');
        break;
      case 'setStatusInactive':
        await batchUpdateStatus(1, '批量设置为离职');
        break;
      case 'setStatusRetired':
        await batchUpdateStatus(2, '批量设置为退休');
        break;
      case 'resetPwd':
        await batchResetPassword();
        break;
      default:
        console.warn('未知的批量操作命令:', command);
    }
  } catch (err) {
    console.error('批量操作失败', err);
    ElMessage.error('批量操作失败');
  }
};

// 批量更新状态
const batchUpdateStatus = async (status, message) => {
  // 确认提示
  await ElMessageBox.confirm(`确定要${message}吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  
  // 执行批量操作
  const promises = selection.value.map(item => updateTeacherStatus(item.id, status));
  await Promise.all(promises);
  ElMessage.success(message + '成功');
  getList();
};

// 批量重置密码
const batchResetPassword = async () => {
  // 确认提示
  await ElMessageBox.confirm(`确定要批量重置选中的 ${selection.value.length} 名教师的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  
  // 执行批量操作
  const promises = selection.value.map(item => resetTeacherPassword(item.id));
  await Promise.all(promises);
  ElMessage.success('批量重置密码成功');
};

// 应用列设置
const applyColumnSettings = () => {
  // 更新列设置
  for (const key in columnSettings) {
    columnSettings[key] = checkedColumns.value.includes(key);
  }
  showColumnSettings.value = false;
};

// 处理下拉菜单命令
const handleCommand = (row, command) => {
  switch (command) {
    case 'viewCourses':
      handleViewCourses(row);
      break;
    case 'viewSchedule':
      handleViewSchedule(row);
      break;
    case 'viewEvaluations':
      handleViewEvaluations(row);
      break;
    case 'resetPassword':
      handleResetPassword(row);
      break;
    case 'delete':
      handleDelete(row);
      break;
    default:
      console.warn('未知的命令:', command);
  }
};

// 权限检查函数
const hasPermission = (permission) => {
  // 动态获取用户权限
  const userStore = useUserStore()
  const permissions = userStore.permissions
  
  if (typeof permission === 'string') {
    return permissions.includes(permission)
  }
  
  if (permission instanceof Array && permission.length > 0) {
    return permissions.some(p => permission.includes(p))
  }
  
  return false
}
</script>

<style lang="scss" scoped>
.teacher-container {
  padding: 20px;
  
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
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.more-search {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #dcdfe6;
}
</style>