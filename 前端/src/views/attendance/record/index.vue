<template>
  <div class="attendance-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">考勤记录管理</span>
            <span class="course-info" v-if="courseInfo">
              {{ courseInfo.courseName }} - {{ courseInfo.semester }}
            </span>
          </div>
          <div v-if="!isSingleStudentView">
            <el-button @click="goBack" link>
              <el-icon><Back /></el-icon>返回
            </el-button>
          </div>
          <div v-else>
            <el-button @click="backToList" link>
              <el-icon><Back /></el-icon>返回列表
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 参数错误提示区域 -->
      <div v-if="noParams" class="error-container">
        <el-empty 
          image="error" 
          description="参数不完整，无法加载考勤记录"
          :image-size="200">
          <div class="error-message">
            <p>请从课程列表或课程开设页面进入考勤管理系统</p>
          </div>
          <el-button type="primary" @click="goBack">返回上一页</el-button>
        </el-empty>
      </div>

      <!-- 正常内容部分，当有参数时显示 -->
      <div v-else>
        <!-- 课程信息区域 -->
        <div class="course-details" v-if="courseInfo">
          <div class="details-item">
            <span class="label">课程编码:</span>
            <span class="value">{{ courseInfo.courseCode || '-' }}</span>
          </div>
          <div class="details-item">
            <span class="label">学分:</span>
            <span class="value">{{ courseInfo.credit || '-' }}</span>
          </div>
          <div class="details-item">
            <span class="label">授课教师:</span>
            <span class="value">{{ courseInfo.teacherName || '-' }}</span>
          </div>
          <div class="details-item">
            <span class="label">上课时间:</span>
            <span class="value">{{ courseInfo.classTime || '-' }}</span>
          </div>
          <div class="details-item">
            <span class="label">上课地点:</span>
            <span class="value">{{ courseInfo.location || '-' }}</span>
          </div>
        </div>

        <!-- 搜索区域 -->
        <el-form v-if="!isSingleStudentView" :model="queryParams" ref="queryForm" :inline="true" class="search-form">
          <el-form-item label="日期" prop="attendanceDate">
            <el-date-picker
              v-model="queryParams.attendanceDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled="recording"
            />
          </el-form-item>
          <el-form-item label="学号" prop="studentNo">
            <el-input
              v-model="queryParams.studentNo"
              placeholder="请输入学号"
              clearable
              :disabled="recording"
            />
          </el-form-item>
          <el-form-item label="姓名" prop="studentName">
            <el-input 
              v-model="queryParams.studentName" 
              placeholder="请输入姓名" 
              clearable
              :disabled="recording"
            />
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleQuery" 
              :disabled="recording"
            >
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button 
              @click="resetQuery" 
              :disabled="recording"
            >
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 操作按钮 -->
        <div class="operation-container" v-if="!isSingleStudentView">
            <el-button
              type="primary"
            @click="handleStartRecording" 
            v-if="!recording"
          >
            <el-icon><VideoPlay /></el-icon>开始考勤
          </el-button>
          <el-button 
            type="success" 
            @click="handleSaveRecords" 
            v-if="recording"
          >
            <el-icon><Check /></el-icon>保存考勤
          </el-button>
            <el-button
              type="danger"
            @click="handleCancelRecording" 
            v-if="recording"
          >
            <el-icon><Close /></el-icon>取消考勤
          </el-button>
            <el-button
          type="primary" 
          @click="handleExportRecords" 
          :disabled="recording || !studentList.length"
        >
          <el-icon><Download /></el-icon>导出考勤记录
        </el-button>
        </div>

        <!-- 学生列表 -->
        <el-table
          v-loading="loading"
          :data="studentList"
          stripe
          border
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column prop="studentNo" label="学号" width="120" align="center" />
          <el-table-column prop="name" label="姓名" width="100" align="center" />
          <el-table-column prop="gender" label="性别" width="60" align="center">
            <template #default="scope">
              {{ scope.row.gender === 0 ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column prop="className" label="班级" width="150" align="center" show-overflow-tooltip />
          
          <!-- 考勤状态列 - 记录模式 -->
          <el-table-column v-if="recording" label="考勤状态" width="200" align="center">
            <template #default="scope">
              <el-radio-group v-model="scope.row.attendanceStatus">
                <el-radio-button label="0">出勤</el-radio-button>
                <el-radio-button label="2">迟到</el-radio-button>
                <el-radio-button label="1">缺勤</el-radio-button>
                <el-radio-button label="4">请假</el-radio-button>
              </el-radio-group>
            </template>
          </el-table-column>
          
          <!-- 考勤记录列 - 查看模式 -->
          <el-table-column v-else label="出勤率" width="100" align="center">
            <template #default="scope">
              <el-progress 
                :percentage="scope.row.attendanceRate || 0" 
                :color="getAttendanceColor(scope.row.attendanceRate)"
                :format="(percentage) => `${percentage}%`"
                :stroke-width="10"
              />
            </template>
          </el-table-column>
          
          <!-- 考勤历史记录 -->
          <el-table-column v-if="!recording && !isSingleStudentView" label="历史考勤" min-width="250" align="center">
            <template #default="scope">
              <div class="attendance-history">
                <el-tag
                  v-for="(item, index) in scope.row.attendanceRecords" 
                  :key="index"
                  :type="getAttendanceTagType(item.status.toString())"
                  class="attendance-tag"
                  @click="handleViewAttendanceDetail(item)"
                >
                  {{ formatDate(item.date) }}: {{ translateAttendanceStatus(item.status.toString()) }}
                </el-tag>
                <span v-if="!scope.row.attendanceRecords || scope.row.attendanceRecords.length === 0" class="no-records">
                  暂无记录
                </span>
              </div>
            </template>
          </el-table-column>
          
          <!-- 单个学生考勤详情视图 -->
          <template v-if="isSingleStudentView">
            <el-table-column label="考勤日期" width="120" align="center">
              <template #default="scope">
                {{ formatDate(scope.row.date) }}
              </template>
            </el-table-column>
            <el-table-column label="考勤状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getAttendanceTagType(scope.row.status.toString())">
                  {{ translateAttendanceStatus(scope.row.status.toString()) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" min-width="180" align="center">
              <template #default="scope">
                {{ scope.row.remark || '-' }}
              </template>
            </el-table-column>
          </template>
          
          <!-- 考勤备注 -->
          <el-table-column v-if="recording" label="备注" min-width="200" align="center">
            <template #default="scope">
              <el-input 
                v-model="scope.row.remark" 
                placeholder="填写备注信息"
                clearable
              />
            </template>
          </el-table-column>
          
          <!-- 操作列 -->
          <el-table-column v-if="!recording && !isSingleStudentView" label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" link @click="handleViewAttendance(scope.row)">
                考勤详情
              </el-button>
              <el-button type="primary" link @click="handleEditAttendance(scope.row)">
                单独考勤
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页区域 -->
        <div class="pagination-container" v-if="!isSingleStudentView">
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
      </div>
    </el-card>

    <!-- 单独考勤对话框 -->
    <el-dialog v-model="attendanceDialog.visible" :title="attendanceDialog.title" width="500px" append-to-body>
      <el-form ref="attendanceFormRef" :model="attendanceForm" :rules="attendanceRules" label-width="100px">
        <el-form-item label="学生" prop="studentName">
          <el-input v-model="attendanceForm.studentName" disabled />
          </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="attendanceForm.studentNo" disabled />
          </el-form-item>
        <el-form-item label="考勤日期" prop="date">
            <el-date-picker
            v-model="attendanceForm.date"
              type="date"
              placeholder="选择日期"
            format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            style="width: 100%"
          />
          </el-form-item>
          <el-form-item label="考勤状态" prop="status">
          <el-radio-group v-model="attendanceForm.status">
            <el-radio label="0">出勤</el-radio>
            <el-radio label="1">缺勤</el-radio>
            <el-radio label="2">迟到</el-radio>
            <el-radio label="4">请假</el-radio>
          </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
          <el-input v-model="attendanceForm.remark" type="textarea" rows="3" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
          <el-button @click="attendanceDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitAttendance" :loading="submitLoading">确定</el-button>
          </div>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, VideoPlay, Check, Close, Back, Download } from '@element-plus/icons-vue'
import { getTeacherCourseStudents, submitStudentAttendance } from '@/api/teacher'
import { useUserStore } from '@/stores/user'
import { formatDate as formatDateUtil } from '@/utils/date'

// 路由
const router = useRouter()
const route = useRoute()
const courseId = ref(null)
const courseOfferingId = ref(null)
const studentId = ref(null) // 用于单个学生视图

// 用户信息
const userStore = useUserStore()
const teacherId = ref(null)

// 课程信息
const courseInfo = ref(null)

// 列表数据
const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const recording = ref(false) // 是否处于考勤记录模式
const isSingleStudentView = ref(false) // 是否是单个学生的考勤详细视图
const noParams = ref(false) // 用于显示错误状态

// 查询参数
const queryParams = reactive({
  courseId: undefined,
  courseOfferingId: undefined,
  studentId: undefined,
  attendanceDate: new Date().toISOString().split('T')[0], // 默认当天
  studentNo: '',
  studentName: '',
  pageNum: 1,
  pageSize: 10
})

// 考勤对话框
const attendanceDialog = reactive({
  visible: false,
  title: '记录考勤'
})
const attendanceForm = reactive({
  id: null,
  studentId: null,
  studentName: '',
  studentNo: '',
  date: new Date().toISOString().split('T')[0],
  status: '0',
  remark: ''
})
const attendanceRules = {
  date: [{ required: true, message: '请选择考勤日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择考勤状态', trigger: 'change' }]
}
const attendanceFormRef = ref(null)
const submitLoading = ref(false)

// 初始化
onMounted(async () => {
  console.log('路由参数:', route.query)
  // 提取路由参数
  courseId.value = Number(route.query.courseId) || null
  courseOfferingId.value = Number(route.query.courseOfferingId) || null
  studentId.value = Number(route.query.studentId) || null
  isSingleStudentView.value = !!studentId.value
  
  // 记录路由参数
  console.log('考勤记录页面初始化参数:', {
    courseId: courseId.value,
    courseOfferingId: courseOfferingId.value,
    studentId: studentId.value,
    isSingleStudentView: isSingleStudentView.value
  })
  
  // 检查必要参数
  if (!courseId.value && !courseOfferingId.value) {
    console.warn('访问考勤记录页面缺少必要参数: courseId和courseOfferingId都为空')
    noParams.value = true
    ElMessage.warning('参数不完整，请从课程列表或课程开设页面进入考勤管理')
  }
  
  // 获取教师ID
  await fetchTeacherId()
  // 获取学生列表
  if (!noParams.value) {
    fetchStudentList()
  }
})

// 获取教师ID
async function fetchTeacherId() {
  try {
    // 使用userStore中的userInfo属性
    const userInfo = userStore.userInfo
    console.log('当前用户信息:', userInfo)
    
    // 判断用户类型和权限
    const isAdmin = userInfo && userInfo.roles && userInfo.roles.some(role => role === 'admin')
    
    if (userInfo && userInfo.teacherId) {
      // 教师用户
      teacherId.value = userInfo.teacherId
      console.log('教师用户访问考勤页面，teacherId:', teacherId.value)
    } else if (isAdmin) {
      // 管理员用户 - 不需要特定的teacherId
      console.log('管理员用户访问考勤页面')
      if (!courseId.value && !courseOfferingId.value) {
        console.warn('警告：访问考勤页面但缺少courseId和courseOfferingId参数')
        ElMessage.warning('请从课程列表或课程开设页面进入考勤管理')
      }
      teacherId.value = null
    } else {
      console.warn('未知用户类型，缺少必要的角色和ID信息:', userInfo)
      ElMessage.warning('您可能没有权限访问此页面')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 获取学生列表
function fetchStudentList() {
  // 初始化参数
  initQueryParams()
  
  // 检查必要参数
  if (!queryParams.courseId && !queryParams.courseOfferingId) {
    console.error('缺少必要参数: courseId和courseOfferingId都为空')
    ElMessage.warning('请从课程列表或课程开设页面进入考勤管理')
    noParams.value = true
    loading.value = false
    return
  }
  
  // teacherId保持原样，如果是管理员用户则为null，让后端以管理员身份处理
  console.log('API请求参数:', {
    teacherId: teacherId.value,
    courseId: queryParams.courseId,
    queryParams: queryParams
  })
  
  loading.value = true
  getTeacherCourseStudents(teacherId.value, queryParams.courseId, queryParams)
    .then(res => {
      console.log('获取学生列表成功:', res)
      noParams.value = false
      if (isSingleStudentView.value && queryParams.studentId) {
        // 单个学生的考勤记录视图
        console.log('处理单个学生视图数据:', res.data)
        
        // 修正数据结构处理 - 数据可能在rows或list中的第一个元素
        const student = res.data.rows?.[0] || res.data.list?.[0] || {}
        console.log('获取到的学生数据:', student)
        
        // 保存学生基本信息，用于显示在表头或页面其他位置
        const studentInfo = {
          studentId: student.studentId,
          studentNo: student.studentNo,
          name: student.name,
          className: student.className,
          majorName: student.majorName,
          gender: student.gender,
          genderDesc: student.genderDesc,
          attendanceRate: student.attendanceRate || 0
        }
        console.log('学生基本信息:', studentInfo)
        
        if (student.attendanceRecords && student.attendanceRecords.length > 0) {
          console.log('学生考勤记录:', student.attendanceRecords)
          // 转换格式确保数据正确，同时合并学生基本信息
          studentList.value = student.attendanceRecords.map(record => ({
            // 考勤记录信息
            id: record.id,
            date: record.date,
            status: record.status,
            remark: record.remark,
            courseOfferingId: record.courseOfferingId,
            studentId: record.studentId,
            // 合并学生基本信息到每条记录
            studentNo: student.studentNo,
            name: student.name,
            className: student.className,
            majorName: student.majorName,
            gender: student.gender,
            genderDesc: student.genderDesc,
            attendanceRate: student.attendanceRate || 0
          }));
          console.log('设置学生列表为考勤记录:', studentList.value);
        } else {
          console.warn('未找到学生考勤记录或记录为空')
          studentList.value = []
          ElMessage.info('该学生暂无考勤记录')
        }
      } else {
        // 课程所有学生视图
        studentList.value = res.data.list || []
        total.value = res.data.total || 0
      }
    
      // 更新课程信息
      if (res.data.courseInfo) {
        courseInfo.value = res.data.courseInfo
      }
    })
    .catch(err => {
      console.error('获取学生列表失败:', err)
      ElMessage.error('获取数据失败: ' + (err.message || '未知错误'))
    })
    .finally(() => {
      loading.value = false
    })
}

// 查询
function handleQuery() {
  queryParams.pageNum = 1
  fetchStudentList()
}

// 重置
function resetQuery() {
  queryParams.attendanceDate = new Date().toISOString().split('T')[0]
  queryParams.studentNo = ''
  queryParams.studentName = ''
  handleQuery()
}

// 分页
function handleSizeChange(size) {
  queryParams.pageSize = size
  fetchStudentList()
}

function handleCurrentChange(page) {
  queryParams.pageNum = page
  fetchStudentList()
}

// 返回
function goBack() {
  router.go(-1)
}

// 返回列表视图
function backToList() {
  studentId.value = null
  isSingleStudentView.value = false
  queryParams.studentId = undefined
  fetchStudentList()
}

// 格式化日期
function formatDate(dateStr) {
  return formatDateUtil(dateStr, 'YYYY-MM-DD')
}

// 获取考勤标签类型
function getAttendanceTagType(status) {
  const statusMap = {
    '0': 'success', // 出勤
    '1': 'danger',  // 缺勤
    '2': 'warning', // 迟到
    '3': 'info',    // 早退
    '4': 'info',    // 请假
    'present': 'success',
    'late': 'warning',
    'absent': 'danger',
    'leave': 'info'
  }
  return statusMap[status] || 'info'
}

// 获取出勤率颜色
function getAttendanceColor(rate) {
  if (rate === null || rate === undefined) return '#909399'
  if (rate >= 90) return '#67C23A'
  if (rate >= 80) return '#E6A23C'
  return '#F56C6C'
}

// 翻译考勤状态
function translateAttendanceStatus(status) {
  const statusMap = {
    '0': '出勤',
    '1': '缺勤',
    '2': '迟到',
    '3': '早退',
    '4': '请假',
    'present': '出勤',
    'late': '迟到',
    'absent': '缺勤',
    'leave': '请假'
  }
  return statusMap[status] || '未知'
}

// 开始考勤
function handleStartRecording() {
  if (!queryParams.attendanceDate) {
    ElMessage.warning('请先选择考勤日期')
    return
  }
  
  ElMessageBox.confirm(`确定要开始 ${queryParams.attendanceDate} 的考勤记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    recording.value = true
    
    // 初始化所有学生的考勤状态为出勤
    studentList.value.forEach(student => {
      student.attendanceStatus = '0' // 出勤
      student.remark = ''
    })
    
    ElMessage.success('已进入考勤记录模式')
  }).catch(() => {
    // 取消操作，不做任何处理
  })
}

// 保存考勤记录
function handleSaveRecords() {
  if (studentList.value.length === 0) {
    ElMessage.warning('没有学生可记录考勤')
    return
  }
  
  ElMessageBox.confirm('确定要保存当前的考勤记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    submitLoading.value = true
    
    // 构建提交数据
    const attendanceData = {
      date: queryParams.attendanceDate,
      records: studentList.value.map(student => ({
        studentId: student.studentId,
        status: parseInt(student.attendanceStatus),
        remark: student.remark || ''
      }))
    }

    // 确定使用哪个ID作为API请求参数
    // 如果是管理员用户(teacherId为null)，则保持为null，让后端以管理员身份处理
    const effectiveTeacherId = teacherId.value
    
    console.log('提交考勤记录，参数:', {
      teacherId: effectiveTeacherId,
      courseId: queryParams.courseId,
      attendanceData: attendanceData
    })

    submitStudentAttendance(effectiveTeacherId, queryParams.courseId, attendanceData)
      .then(() => {
        ElMessage.success('考勤记录保存成功')
        recording.value = false
        fetchStudentList() // 刷新数据
      })
      .catch(err => {
        console.error('考勤记录保存失败:', err)
        ElMessage.error('考勤记录保存失败: ' + (err.message || '未知错误'))
      })
      .finally(() => {
        submitLoading.value = false
      })
  }).catch(() => {
    // 取消操作，不做任何处理
  })
}

// 取消考勤
function handleCancelRecording() {
  ElMessageBox.confirm('确定要取消当前的考勤记录吗？所有未保存的数据将丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    recording.value = false
    fetchStudentList() // 重新加载数据
    ElMessage.info('已取消考勤记录')
  }).catch(() => {
    // 取消操作，不做任何处理
  })
}

// 导出考勤记录
function handleExportRecords() {
  ElMessage.info('导出考勤记录功能待实现')
}

// 查看考勤详情
function handleViewAttendance(row) {
  // 使用本地状态切换到单个学生视图，而不是导航到另一个路由
  console.log('查看学生考勤详情:', row)
  
  studentId.value = row.studentId
  isSingleStudentView.value = true
  
  // 更新查询参数
  queryParams.studentId = row.studentId
  
  console.log('更新查询参数:', {
    studentId: studentId.value,
    isSingleStudentView: isSingleStudentView.value,
    queryParams: queryParams
  })
  
  // 重新加载数据
  fetchStudentList()
}

// 查看考勤记录详情
function handleViewAttendanceDetail(record) {
  // 这里可以显示一个弹窗，展示更详细的考勤记录
  ElMessage.info(`${formatDate(record.date)}: ${translateAttendanceStatus(record.status.toString())}${record.remark ? ', 备注: ' + record.remark : ''}`)
}

// 单独考勤
function handleEditAttendance(row) {
  attendanceDialog.title = '记录考勤'
  attendanceDialog.visible = true
  
  attendanceForm.id = row.id
  attendanceForm.studentId = row.studentId
  attendanceForm.studentName = row.name
  attendanceForm.studentNo = row.studentNo
  attendanceForm.date = queryParams.attendanceDate
  attendanceForm.status = '0'
  attendanceForm.remark = ''
}

// 提交单独考勤
function submitAttendance() {
  attendanceFormRef.value?.validate(valid => {
    if (!valid) return
    
    submitLoading.value = true
    
    // 将前端状态转换为后端状态
    let statusCode = attendanceForm.status;
    if (attendanceForm.status === 'present') statusCode = 0;
    else if (attendanceForm.status === 'absent') statusCode = 1;
    else if (attendanceForm.status === 'late') statusCode = 2;
    else if (attendanceForm.status === 'leave') statusCode = 4;
    
    const submitData = {
      date: attendanceForm.date,
      records: [{
        studentId: attendanceForm.studentId,
        status: statusCode,
        remark: attendanceForm.remark
      }]
    }
    
    // 确定使用哪个ID作为API请求参数
    const effectiveTeacherId = teacherId.value
    
    console.log('提交单独考勤记录，参数:', {
      teacherId: effectiveTeacherId,
      courseId: queryParams.courseId,
      submitData: submitData
    })
    
    submitStudentAttendance(effectiveTeacherId, queryParams.courseId, submitData)
      .then(() => {
        ElMessage.success('考勤记录提交成功')
        attendanceDialog.visible = false
        fetchStudentList()
      })
      .catch(err => {
        console.error('考勤记录提交失败:', err)
        ElMessage.error('考勤记录提交失败: ' + (err.message || '未知错误'))
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

// 初始化查询参数
function initQueryParams() {
  // 从路由获取参数
  queryParams.courseId = courseId.value
  queryParams.courseOfferingId = courseOfferingId.value
  
  console.log('初始化查询参数前:', {
    courseId: courseId.value,
    courseOfferingId: courseOfferingId.value
  })
  
  // 确保两个关键参数至少有一个有效值
  if (!queryParams.courseId && queryParams.courseOfferingId) {
    queryParams.courseId = queryParams.courseOfferingId
    console.log('使用courseOfferingId作为courseId:', queryParams.courseId)
  } else if (!queryParams.courseOfferingId && queryParams.courseId) {
    queryParams.courseOfferingId = queryParams.courseId
    console.log('使用courseId作为courseOfferingId:', queryParams.courseOfferingId)
  }

  // 记录查询参数
  console.log('初始化查询参数后:', JSON.stringify(queryParams))
  
  if (isSingleStudentView.value) {
    queryParams.studentId = studentId.value
  }
}
</script>

<style scoped>
.attendance-container {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  margin-right: 15px;
}

.course-info {
  font-size: 14px;
  color: #606266;
}

.course-details {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.details-item {
  margin-right: 20px;
  margin-bottom: 8px;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-right: 5px;
}

.value {
  color: #333;
}

.search-form {
  margin-bottom: 15px;
}

.operation-container {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.attendance-history {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 5px;
}

.attendance-tag {
  cursor: pointer;
}

.no-records {
  color: #909399;
  font-style: italic;
}

.error-container {
  padding: 40px 0;
  text-align: center;
}
.error-message {
  margin-bottom: 20px;
  color: #909399;
}
</style> 