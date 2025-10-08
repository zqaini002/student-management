<template>
  <div class="student-container">
    <template v-if="isLoading">
      <div class="loading-container">
        <el-card class="loading-card">
          <div class="loading-content">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <div>正在加载学生信息，请稍候...</div>
            <el-button @click="forceReload" style="margin-top: 15px">重新加载</el-button>
          </div>
        </el-card>
      </div>
    </template>
    <template v-else-if="hasError">
      <div class="error-container">
        <el-card class="error-card">
          <div class="error-content">
            <el-icon class="error-icon"><WarningFilled /></el-icon>
            <div>加载学生信息时出错</div>
            <div class="error-message">{{ errorMessage }}</div>
            <div class="action-buttons">
              <el-button @click="backToHome">返回首页</el-button>
              <el-button type="primary" @click="forceReload">重试</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </template>
    <template v-else>
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable style="width: 100%">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
            <template #empty>
              <div class="empty-data-tip">
                <span v-if="classList.length === 0">暂无班级数据</span>
                <span v-else>加载班级数据中...</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="queryParams.gender" placeholder="请选择性别" clearable style="width: 100%">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="majorId">
          <el-select v-model="queryParams.majorId" placeholder="请选择专业" clearable style="width: 100%">
            <el-option
              v-for="item in majorList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
            <template #empty>
              <div class="empty-data-tip">
                <span v-if="majorList.length === 0">暂无专业数据</span>
                <span v-else>加载专业数据中...</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="院系" prop="departmentId">
          <el-select v-model="queryParams.departmentId" placeholder="请选择院系" clearable style="width: 100%">
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
            <template #empty>
              <div class="empty-data-tip">
                <span v-if="departmentList.length === 0">暂无院系数据</span>
                <span v-else>加载院系数据中...</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100%">
            <el-option label="在读" :value="0" />
            <el-option label="毕业" :value="1" />
            <el-option label="休学" :value="2" />
            <el-option label="退学" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="入学时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
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
          <span class="header-title">学生信息列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd" v-hasPerms="['education:student:add']">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button type="success" @click="handleImport" v-hasPerms="['education:student:import']">
              <el-icon><Upload /></el-icon>导入
            </el-button>
            <el-button @click="handleExport" v-hasPerms="['education:student:export']">
              <el-icon><Download /></el-icon>导出
            </el-button>
            <el-button type="info" @click="toggleStatistics" v-hasPerms="['education:student:list']">
              <el-icon><DataAnalysis /></el-icon>统计
            </el-button>
            <el-dropdown @command="handleColumnsVisibility" split-button type="info">
              <el-icon><Setting /></el-icon> 列设置
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="col in columnOptions" :key="col.prop" :command="col.prop">
                    <el-checkbox v-model="col.visible">{{ col.label }}</el-checkbox>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <!-- 批量操作按钮区域 -->
      <div class="batch-actions" v-if="selectedRows.length > 0">
        <el-alert
          title="已选择"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <span>已选择 <b>{{ selectedRows.length }}</b> 条记录</span>
            <el-button link @click="clearSelection">取消选择</el-button>
          </template>
        </el-alert>
        <div class="batch-buttons">
          <el-button 
            type="danger" 
            @click="handleBatchDelete" 
            v-hasPerms="['education:student:remove']"
          >
            批量删除
          </el-button>
          <el-dropdown @command="handleBatchStatusChange" split-button type="primary">
            批量修改状态
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="0">设为在读</el-dropdown-item>
                <el-dropdown-item :command="1">设为毕业</el-dropdown-item>
                <el-dropdown-item :command="2">设为休学</el-dropdown-item>
                <el-dropdown-item :command="3">设为退学</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="success" @click="handleExportSelected">导出选中记录</el-button>
        </div>
      </div>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="studentList"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        @selection-change="handleSelectionChange"
        ref="studentTable"
      >
        <el-table-column type="selection" width="55" align="center" fixed="left" />
        <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
        <el-table-column prop="studentNo" label="学号" width="120" fixed="left" v-if="getColumnVisible('studentNo')" />
        <el-table-column prop="name" label="姓名" width="100" fixed="left" v-if="getColumnVisible('name')" />
        <el-table-column prop="genderDesc" label="性别" width="60" align="center" v-if="getColumnVisible('gender')" />
        <el-table-column prop="className" label="班级" width="180" v-if="getColumnVisible('class')" />
        <el-table-column prop="majorName" label="专业" width="180" v-if="getColumnVisible('major')" />
        <el-table-column prop="departmentName" label="院系" width="150" v-if="getColumnVisible('department')" />
        <el-table-column prop="admissionDate" label="入学日期" width="120" align="center" v-if="getColumnVisible('admissionDate')" />
        <el-table-column prop="graduationDate" label="预计毕业" width="120" align="center" v-if="getColumnVisible('graduationDate')" />
        <el-table-column prop="statusDesc" label="状态" width="80" align="center" v-if="getColumnVisible('statusDesc')">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号码" width="130" v-if="getColumnVisible('phone')" />
        <el-table-column prop="email" label="邮箱" width="180" v-if="getColumnVisible('email')" />
        <el-table-column label="操作" min-width="170" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="handleUpdate(scope.row)" 
              v-hasPerms="['education:student:edit']"
            >
              编辑
            </el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleViewDetail(scope.row)"
              v-hasPerms="['education:student:query']"
            >
              详情
            </el-button>
            <el-dropdown>
              <el-button link type="primary">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleViewCourses(scope.row)">查看课程</el-dropdown-item>
                  <el-dropdown-item @click="handleViewGrades(scope.row)">查看成绩</el-dropdown-item>
                  <el-dropdown-item @click="handleViewAttendance(scope.row)">查看考勤</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(scope.row)" class="text-danger">删除</el-dropdown-item>
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

    <!-- 数据统计卡片区域 -->
    <el-row :gutter="20" class="statistics-row" v-if="showStatistics">
      <el-col :span="24">
        <el-card class="statistics-card" shadow="hover">
          <template #header>
            <div class="statistics-header">
              <span>学生数据统计</span>
              <el-button @click="showStatistics = false" link>关闭</el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div ref="statusChartRef" class="chart-container"></div>
            </el-col>
            <el-col :span="12">
              <div ref="departmentChartRef" class="chart-container"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

      <!-- 查看详情对话框 -->
    <el-dialog
      title="学生详情"
      v-model="detailDialog.visible"
      width="700px"
      append-to-body
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ studentDetail.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ studentDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ studentDetail.genderDesc }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ studentDetail.className }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ studentDetail.majorName }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ studentDetail.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="入学日期">{{ studentDetail.admissionDate }}</el-descriptions-item>
        <el-descriptions-item label="毕业日期">{{ studentDetail.graduationDate }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ studentDetail.idCard }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ studentDetail.birthday }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ studentDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ studentDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ studentDetail.address }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(studentDetail.status)">{{ studentDetail.statusDesc }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialog.visible = false">关闭</el-button>
          <el-button type="primary" @click="handleUpdate(studentDetail)" v-hasPerms="['education:student:edit']">编辑</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog
        title="导入学生数据"
      v-model="importDialog.visible"
        width="500px"
      append-to-body
    >
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :before-upload="beforeUpload"
        :file-list="importDialog.fileList"
        :multiple="false"
      >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
              只能上传Excel文件，且不超过10MB
              <el-link type="primary" @click="downloadTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="submitImport">确定</el-button>
        </div>
      </template>
    </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, onErrorCaptured, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Plus, 
  Upload, 
  Download,
  UploadFilled,
  Loading,
  WarningFilled,
  Setting,
  DataAnalysis,
  ArrowDown
} from '@element-plus/icons-vue'
import { 
  getStudentList, 
  getStudentDetail, 
  deleteStudent,
  exportStudent,
  downloadStudentTemplate,
  importStudent,
  getStudentCourses,
  getStudentGrades,
  getStudentAttendance,
  getStudentStatistics,
  updateBatchStudentStatus
} from '@/api/student'
import { getAllClasses } from '@/api/class'
import { getAllMajors } from '@/api/major'
import { getAllDepartments } from '@/api/department'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { 
  TitleComponent, 
  TooltipComponent, 
  GridComponent, 
  LegendComponent 
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册ECharts组件
echarts.use([
  TitleComponent, 
  TooltipComponent, 
  GridComponent, 
  LegendComponent,
  BarChart,
  PieChart,
  CanvasRenderer
])

const router = useRouter()
const userStore = useUserStore()

// 页面加载状态
const isLoading = ref(true)
const hasError = ref(false)
const errorMessage = ref('')
const loadRetries = ref(0)

// 错误捕获
onErrorCaptured((err, instance, info) => {
  console.error('学生组件捕获到错误:', err)
  console.error('错误详情:', info)
  
  isLoading.value = false
  hasError.value = true
  errorMessage.value = `${err.message || '未知错误'}`
  
  return false // 阻止错误继续传播
})

// 重试加载
const forceReload = () => {
  if (loadRetries.value >= 3) {
    ElMessage.warning('多次重试失败，请检查网络或联系管理员')
    return
  }
  
  loadRetries.value++
  isLoading.value = true
  hasError.value = false
  errorMessage.value = ''
  
  console.log(`第${loadRetries.value}次尝试加载学生列表`)
  
  // 延迟执行以避免同时发生多个错误
  setTimeout(() => {
    initPageData()
  }, 500)
}

// 返回首页
const backToHome = () => {
  router.push('/dashboard')
}

// 数据列表
const studentList = ref([])
const total = ref(0)
const loading = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  studentNo: '',
  name: '',
  classId: null,
  status: null,
  gender: null,
  majorId: null,
  departmentId: null,
  phone: '',
  email: '',
  startDate: '',
  endDate: ''
})

// 日期范围
const dateRange = ref([])

// 选中的行
const selectedRows = ref([])

// 班级列表
const classList = ref([])
// 专业列表
const majorList = ref([])
// 院系列表
const departmentList = ref([])

// 表格列显示控制
const columnOptions = ref([
  { prop: 'studentNo', label: '学号', visible: true },
  { prop: 'name', label: '姓名', visible: true },
  { prop: 'gender', label: '性别', visible: true },
  { prop: 'class', label: '班级', visible: true },
  { prop: 'major', label: '专业', visible: true },
  { prop: 'department', label: '院系', visible: true },
  { prop: 'admissionDate', label: '入学日期', visible: true },
  { prop: 'graduationDate', label: '预计毕业', visible: false },
  { prop: 'statusDesc', label: '状态', visible: true },
  { prop: 'phone', label: '手机号码', visible: true },
  { prop: 'email', label: '邮箱', visible: false }
])

// 学生详情
const detailDialog = reactive({
  visible: false
})
const studentDetail = ref({})

// 导入相关
const importDialog = reactive({
  visible: false,
  fileList: []
})

// 统计图表相关
const showStatistics = ref(false)
const statusChartRef = ref(null)
const departmentChartRef = ref(null)
let statusChart = null
let departmentChart = null

// 表格引用
const studentTable = ref(null)

// 初始化页面数据
const initPageData = () => {
  console.log('------初始化学生列表页面数据------')
  console.log('当前用户信息:', userStore.name, '角色:', userStore.roles)
  
  if (!userStore.token) {
    console.error('未检测到有效的用户Token')
    hasError.value = true
    errorMessage.value = '登录已过期，请重新登录'
    isLoading.value = false
    return
  }

  try {
    // 判断用户是否有权限
    const hasPermission = userStore.roles.some(role => 
      ['admin', 'teacher', 'student'].includes(role)
    )
    
    if (!hasPermission) {
      console.error('用户无权访问学生列表')
      hasError.value = true
      errorMessage.value = '您没有权限访问此页面'
      isLoading.value = false
      return
    }

    // 先加载班级、专业和院系数据
    Promise.allSettled([
      getClassListData(),
      getMajorListData(),
      getDepartmentListData()
    ])
    .then(results => {
      const errors = results.filter(r => r.status === 'rejected');
      if (errors.length > 0) {
        console.warn(`${errors.length}个基础数据加载失败，但将继续加载学生列表`);
      }
      
      // 无论前面的基础数据加载是否成功，都尝试加载学生列表
      return getStudentListData();
    })
    .then(() => {
      isLoading.value = false;
      console.log('学生列表页面数据加载完成');
      // 本地存储中恢复列设置
      restoreColumnSettings();
    })
    .catch(err => {
      console.error('加载学生数据时出错:', err)
      hasError.value = true
      errorMessage.value = err.message || '加载学生数据失败'
      isLoading.value = false
    });
  } catch (error) {
    console.error('初始化页面时出错:', error)
    hasError.value = true
    errorMessage.value = error.message || '初始化页面失败'
    isLoading.value = false
  }
}

// 获取班级列表
const getClassListData = () => {
  console.log('开始获取班级列表...')
  
  return getAllClasses()
    .then(res => {
      console.log('班级列表获取成功:', res.data)
      classList.value = res.data || []
    })
    .catch(err => {
      console.error('获取班级列表失败:', err)
      console.error('错误详情:', err.response || err.message || err)
      return Promise.reject(new Error('获取班级列表失败'))
    })
}

// 获取专业列表
const getMajorListData = () => {
  console.log('开始获取专业列表...')
  
  return getAllMajors()
    .then(res => {
      console.log('专业列表获取成功:', res.data)
      if (Array.isArray(res.data)) {
        majorList.value = res.data
      } else {
        console.warn('专业列表数据格式异常:', res.data)
        majorList.value = []
      }
    })
    .catch(err => {
      console.error('获取专业列表失败:', err)
      ElMessage.warning('获取专业列表失败，搜索功能可能受限')
      majorList.value = []
      return Promise.resolve() // 不阻止其他数据加载
    })
}

// 获取院系列表
const getDepartmentListData = () => {
  console.log('开始获取院系列表...')
  
  return getAllDepartments()
    .then(res => {
      console.log('院系列表获取成功:', res.data)
      if (Array.isArray(res.data)) {
        departmentList.value = res.data
      } else {
        console.warn('院系列表数据格式异常:', res.data)
        departmentList.value = []
      }
    })
    .catch(err => {
      console.error('获取院系列表失败:', err)
      ElMessage.warning('获取院系列表失败，搜索功能可能受限')
      departmentList.value = []
      return Promise.resolve() // 不阻止其他数据加载
    })
}

// 获取学生列表
const getStudentListData = () => {
  console.log('开始获取学生列表...')
  loading.value = true
  
  return getStudentList(queryParams)
    .then(res => {
      console.log('获取学生列表成功:', res)
      studentList.value = res.data.list
      total.value = res.data.total
      loading.value = false

      // 如果显示统计，则更新统计数据
      if (showStatistics.value) {
        refreshStatistics()
      }
    })
    .catch(err => {
      console.error('获取学生列表失败:', err)
      console.error('错误详情:', err.response || err.message || err)
      loading.value = false
      return Promise.reject(new Error('获取学生列表失败'))
    })
}

// 初始化数据
onMounted(() => {
  console.log('------学生列表组件onMounted开始------')
  console.log('学生列表组件已加载')
  console.log('当前路由信息:', router.currentRoute.value)
  console.log('当前用户信息:', userStore.name, '角色:', userStore.roles, 'Token是否存在:', !!userStore.token)
  
  // 初始化页面数据
  initPageData()
  
  console.log('------学生列表组件onMounted结束------')
})

// 组件卸载
onUnmounted(() => {
  console.log('学生列表组件已卸载')
  // 销毁图表实例
  if (statusChart) {
    statusChart.dispose()
    statusChart = null
  }
  if (departmentChart) {
    departmentChart.dispose()
    departmentChart = null
  }
})

// 存储列设置到本地
const saveColumnSettings = () => {
  localStorage.setItem('student_column_settings', JSON.stringify(columnOptions.value))
}

// 从本地恢复列设置
const restoreColumnSettings = () => {
  const savedSettings = localStorage.getItem('student_column_settings')
  if (savedSettings) {
    try {
      const settings = JSON.parse(savedSettings)
      // 只更新已存在的列的可见性
      settings.forEach(saved => {
        const found = columnOptions.value.find(col => col.prop === saved.prop)
        if (found) {
          found.visible = saved.visible
        }
      })
    } catch (e) {
      console.error('恢复列设置失败:', e)
    }
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getStudentListData()
}

// 重置查询
const resetQuery = () => {
  queryParams.studentNo = ''
  queryParams.name = ''
  queryParams.classId = null
  queryParams.gender = null
  queryParams.majorId = null
  queryParams.departmentId = null
  queryParams.phone = ''
  queryParams.status = null
  queryParams.startDate = ''
  queryParams.endDate = ''
  dateRange.value = []
  handleQuery()
}

// 处理页码改变
const handleCurrentChange = (pageNum) => {
  queryParams.pageNum = pageNum
  getStudentListData()
}

// 处理每页条数改变
const handleSizeChange = (pageSize) => {
  queryParams.pageSize = pageSize
  getStudentListData()
}

// 新增按钮
const handleAdd = () => {
  router.push('/student-add')
}

// 编辑按钮
const handleUpdate = (row) => {
  try {
    console.log('编辑学生信息:', row.id)
    // 使用正确的路由路径格式
    router.push(`/student-edit/${row.id}`)
    
    // 如果上面的路由跳转不生效，使用完整配置格式
    /*
    router.push({
      name: 'StudentEdit',
      params: { id: row.id }
    })
    */
  } catch (error) {
    console.error('跳转到编辑页面失败:', error)
    ElMessage.error('跳转失败，请刷新页面后重试')
  }
}

// 查看详情
const handleViewDetail = (row) => {
  detailDialog.visible = true
  getStudentDetail(row.id)
    .then(res => {
      studentDetail.value = res.data
    })
    .catch(err => {
      console.error('获取学生详情失败:', err)
      ElMessage.error('获取学生详情失败')
    })
}

// 删除按钮
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteStudent(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getStudentListData()
      })
      .catch(err => {
        console.error('删除学生失败:', err)
        ElMessage.error('删除学生失败')
      })
  }).catch(() => {})
}

// 导出按钮
const handleExport = () => {
  exportStudent(queryParams)
    .then(res => {
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = '学生信息.xlsx'
      link.click()
      URL.revokeObjectURL(link.href)
      ElMessage.success('导出成功')
    })
    .catch(err => {
      console.error('导出学生信息失败:', err)
      ElMessage.error('导出学生信息失败')
    })
}

// 导入按钮
const handleImport = () => {
  importDialog.fileList = []
  importDialog.visible = true
}

// 下载模板
const downloadTemplate = () => {
  downloadStudentTemplate()
    .then(res => {
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = '学生信息导入模板.xlsx'
      link.click()
      URL.revokeObjectURL(link.href)
    })
    .catch(err => {
      console.error('下载模板失败:', err)
      ElMessage.error('下载模板失败')
    })
}

// 上传前校验
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.ms-excel' || file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  if (!isExcel) {
    ElMessage.error('只能上传Excel文件!')
    return false
  }
  return true
}

// 文件改变时的钩子
const handleFileChange = (uploadFile, uploadFiles) => {
  // Element Plus的on-change回调参数：(uploadFile, uploadFiles)
  // uploadFile是当前改变的文件，uploadFiles是文件列表
  importDialog.fileList = uploadFiles
}

// 确认导入
const submitImport = () => {
  if (importDialog.fileList.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  const formData = new FormData()
  // Element Plus的文件对象中，raw属性是原始文件对象
  const file = importDialog.fileList[0].raw || importDialog.fileList[0]
  
  console.log('准备导入文件:', {
    name: file.name,
    size: file.size,
    type: file.type
  })
  
  if (!file || file.size === 0) {
    ElMessage.error('文件为空，请选择有效的Excel文件')
    return
  }
  
  formData.append('file', file)

  importStudent(formData)
    .then(res => {
      console.log('导入结果:', res)
      const data = res.data || {}
      const total = data.total || 0
      const success = data.success || 0
      const fail = data.fail || 0
      
      if (total === 0) {
        ElMessage.warning('Excel文件中没有数据')
      } else if (fail === 0) {
        ElMessage.success(`导入成功！共导入${success}条数据`)
      } else {
        ElMessage.warning(`导入完成！成功${success}条，失败${fail}条`)
        // 如果有失败数据，可以显示详情
        if (data.failList && data.failList.length > 0) {
          console.error('导入失败的数据:', data.failList)
        }
      }
      
      importDialog.visible = false
      importDialog.fileList = [] // 清空文件列表
      getStudentListData()
    })
    .catch(err => {
      console.error('导入学生信息失败:', err)
      ElMessage.error(err.message || '导入学生信息失败')
    })
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'success'
    case 1:
      return ''
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

// 获取列可见性
const getColumnVisible = (prop) => {
  const found = columnOptions.value.find(col => col.prop === prop)
  return found ? found.visible : true
}

// 处理列可见性
const handleColumnsVisibility = (prop) => {
  const col = columnOptions.value.find(col => col.prop === prop)
  if (col) {
    col.visible = !col.visible
    saveColumnSettings()
  }
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const ids = selectedRows.value.map(row => row.id)
    // 假设有一个批量删除API，如果没有，可以使用Promise.all进行多个删除请求
    Promise.all(ids.map(id => deleteStudent(id)))
      .then(() => {
        ElMessage.success('批量删除成功')
        clearSelection()
        getStudentListData()
      })
      .catch(err => {
        console.error('批量删除失败:', err)
        ElMessage.error('批量删除失败')
      })
  }).catch(() => {})
}

// 处理批量状态修改
const handleBatchStatusChange = (status) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }

  const statusText = ['在读', '毕业', '休学', '退学'][status]
  
  ElMessageBox.confirm(`确定要将选中的 ${selectedRows.value.length} 条记录状态修改为【${statusText}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const ids = selectedRows.value.map(row => row.id)
    // 调用批量更新状态API
    updateBatchStudentStatus(ids, status)
      .then(() => {
        ElMessage.success(`批量修改状态为【${statusText}】成功`)
        clearSelection()
        getStudentListData()
      })
      .catch(err => {
        console.error('批量修改状态失败:', err)
        ElMessage.error('批量修改状态失败')
      })
  }).catch(() => {})
}

// 处理导出选中记录
const handleExportSelected = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }

  const ids = selectedRows.value.map(row => row.id)
  const exportParams = { ids: ids.join(',') }
  
  exportStudent(exportParams)
    .then(res => {
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `学生信息_选中${ids.length}条.xlsx`
      link.click()
      URL.revokeObjectURL(link.href)
      ElMessage.success('导出选中记录成功')
    })
    .catch(err => {
      console.error('导出选中记录失败:', err)
      ElMessage.error('导出选中记录失败')
    })
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startDate = dateRange.value[0]
    queryParams.endDate = dateRange.value[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
}

// 查看学生课程
const handleViewCourses = (row) => {
  // 显示简单提示
  ElMessage.info(`将显示学生 ${row.name} 的课程信息`);
  
  // 显示确认对话框
  ElMessageBox.confirm(`是否查看学生 ${row.name} 的课程信息？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
    // 如果确实需要跳转页面，可以取消下面的注释
    // router.push(`/student/courses/${row.id}`)
  }).catch(() => {})
}

// 查看学生成绩
const handleViewGrades = (row) => {
  // 显示简单提示
  ElMessage.info(`将显示学生 ${row.name} 的成绩信息`);
  
  // 显示确认对话框
  ElMessageBox.confirm(`是否查看学生 ${row.name} 的成绩信息？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
    // 如果确实需要跳转页面，可以取消下面的注释
    // router.push(`/student/grades/${row.id}`)
  }).catch(() => {})
}

// 查看学生考勤
const handleViewAttendance = (row) => {
  // 显示简单提示
  ElMessage.info(`将显示学生 ${row.name} 的考勤信息`);
  
  // 显示确认对话框
  ElMessageBox.confirm(`是否查看学生 ${row.name} 的考勤信息？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '功能开发中，即将上线'
    })
    // 如果确实需要跳转页面，可以取消下面的注释
    // router.push(`/student/attendance/${row.id}`)
  }).catch(() => {})
}

// 清除表格选择
const clearSelection = () => {
  if (studentTable.value) {
    studentTable.value.clearSelection()
  }
  selectedRows.value = []
}

// 刷新统计数据
const refreshStatistics = () => {
  getStudentStatistics()
    .then(res => {
      const statsData = res.data
      nextTick(() => {
        initStatusChart(statsData.statusDistribution)
        initDepartmentChart(statsData.departmentDistribution)
      })
    })
    .catch(err => {
      console.error('获取统计数据失败:', err)
    })
}

// 初始化状态统计图表
const initStatusChart = (statusData) => {
  if (!statusChartRef.value) return
  
  if (statusChart) {
    statusChart.dispose()
  }
  
  statusChart = echarts.init(statusChartRef.value)
  
  // 将状态分布数据转换为图表所需格式
  const chartData = []
  if (statusData) {
    if (statusData.inSchool) chartData.push({ name: '在读', value: statusData.inSchool })
    if (statusData.graduated) chartData.push({ name: '毕业', value: statusData.graduated })
    if (statusData.leave) chartData.push({ name: '休学', value: statusData.leave })
    if (statusData.dropout) chartData.push({ name: '退学', value: statusData.dropout })
  }
  
  const option = {
    title: {
      text: '学生状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: chartData.map(item => item.name)
    },
    series: [
      {
        name: '学生状态',
        type: 'pie',
        radius: '55%',
        center: ['50%', '60%'],
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  statusChart.setOption(option)
}

// 初始化院系统计图表
const initDepartmentChart = (departmentData) => {
  if (!departmentChartRef.value) return
  
  if (departmentChart) {
    departmentChart.dispose()
  }
  
  departmentChart = echarts.init(departmentChartRef.value)
  
  const option = {
    title: {
      text: '院系学生分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: departmentData.map(item => item.name),
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '学生数量',
        type: 'bar',
        data: departmentData.map(item => item.count),
        itemStyle: {
          color: function(params) {
            const colorList = [
              '#5470c6', '#91cc75', '#fac858', '#ee6666', 
              '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
            ]
            return colorList[params.dataIndex % colorList.length]
          }
        }
      }
    ]
  }
  
  departmentChart.setOption(option)
}

// 切换统计图表显示
const toggleStatistics = () => {
  showStatistics.value = !showStatistics.value
  
  if (showStatistics.value) {
    // 获取统计数据
    refreshStatistics()
  }
}
</script>

<style lang="scss" scoped>
.student-container {
  padding: 15px;
  min-height: calc(100vh - 120px);
  
  .loading-container,
  .error-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 80vh;
    
    .loading-card,
    .error-card {
      width: 500px;
      text-align: center;
      
      .loading-content,
      .error-content {
        padding: 30px;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 15px;
        
        .loading-icon,
        .error-icon {
          font-size: 36px;
          margin-bottom: 15px;
        }
        
        .loading-icon {
          animation: rotating 2s linear infinite;
        }
        
        .error-icon {
          color: #f56c6c;
        }
        
        .error-message {
          color: #f56c6c;
          margin: 10px 0;
          padding: 10px;
          background-color: #fef0f0;
          border-radius: 4px;
          word-break: break-all;
        }
        
        .action-buttons {
          margin-top: 15px;
          display: flex;
          gap: 10px;
          justify-content: center;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 15px;
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      
      .el-form-item {
        margin-right: 10px;
        margin-bottom: 15px;
        
        &:last-child {
          margin-right: 0;
        }
      }
    }
  }
  
  .table-card {
    margin-bottom: 15px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-title {
        font-size: 16px;
        font-weight: bold;
      }
      
      .header-buttons {
        display: flex;
        gap: 10px;
      }
    }
    
    .pagination-container {
      margin-top: 15px;
      display: flex;
      justify-content: flex-end;
    }
    
    .batch-actions {
      margin-bottom: 15px;
      
      .batch-buttons {
        margin-top: 10px;
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .statistics-row {
    margin-top: 15px;
    
    .statistics-card {
      .statistics-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .chart-container {
        height: 400px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.empty-data-tip {
  text-align: center;
  color: #909399;
  padding: 15px 0;
  font-size: 14px;
}
</style>

<style lang="scss">
/* 全局样式，用于深层选择器 */
.el-form {
  .el-form-item {
    min-width: 220px;
    
    .el-form-item__label {
      width: 70px;
      text-align: right;
    }
    
    .el-input,
    .el-select {
      width: 100%;
    }
  }
}

.el-select {
  .el-input__wrapper {
    width: 100%;
  }
  
  .el-select__tags {
    max-width: calc(100% - 30px);
  }
}

.el-select-dropdown__item {
  padding: 0 10px;
  height: 34px;
  line-height: 34px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.upload-demo {
  display: flex;
  justify-content: center;
  
  .el-upload {
    width: 100%;
    
    .el-upload-dragger {
      width: 100%;
    }
  }
  
  .el-upload__tip {
    text-align: center;
    margin-top: 10px;
    
    .el-link {
      margin-left: 10px;
    }
  }
}

.el-dropdown-menu__item {
  &.text-danger {
    color: #F56C6C;
  }
}
</style> 