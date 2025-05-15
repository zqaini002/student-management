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
        </div>
      </template>

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
        <el-table-column prop="studentName" label="姓名" width="100" align="center" />
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
              <el-radio-button label="present">出勤</el-radio-button>
              <el-radio-button label="late">迟到</el-radio-button>
              <el-radio-button label="absent">缺勤</el-radio-button>
              <el-radio-button label="leave">请假</el-radio-button>
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
                :type="getAttendanceTagType(item.status)"
                class="attendance-tag"
                @click="handleViewAttendanceDetail(item)"
              >
                {{ formatDate(item.date) }}: {{ translateAttendanceStatus(item.status) }}
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
              <el-tag :type="getAttendanceTagType(scope.row.status)">
                {{ translateAttendanceStatus(scope.row.status) }}
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
            <el-radio label="present">出勤</el-radio>
            <el-radio label="late">迟到</el-radio>
            <el-radio label="absent">缺勤</el-radio>
            <el-radio label="leave">请假</el-radio>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, VideoPlay, Check, Close, Back, Download } from '@element-plus/icons-vue'
import { getTeacherCourseStudents, submitStudentAttendance } from '@/api/teacher'
import { useUserStore } from '@/stores/user'
import { formatDate as formatDateUtil } from '@/utils/date'

// 路由
const router = useRouter()
const route = useRoute()
const courseId = computed(() => route.query.courseId)
const courseOfferingId = computed(() => route.query.courseOfferingId)
const studentId = computed(() => route.query.studentId) // 用于单个学生视图

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
const isSingleStudentView = computed(() => Boolean(studentId.value)) // 是否是单个学生的考勤详细视图

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
  status: 'present',
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
  await fetchTeacherId()
  initQueryParams()
  fetchStudentList()
})

// 获取教师ID
async function fetchTeacherId() {
  try {
    // 使用userStore中的userInfo属性，而不是调用getUserInfo方法
    const userInfo = userStore.userInfo
    if (userInfo && userInfo.teacherId) {
      teacherId.value = userInfo.teacherId
    } else {
      ElMessage.warning('获取教师信息失败，请重新登录')
    }
  } catch (error) {
    console.error('获取教师ID失败:', error)
    ElMessage.error('获取教师信息失败')
  }
}

// 初始化查询参数
function initQueryParams() {
  queryParams.courseId = courseId.value
  queryParams.courseOfferingId = courseOfferingId.value
  
  if (isSingleStudentView) {
    queryParams.studentId = studentId.value
  }
}

// 获取学生列表
function fetchStudentList() {
  if (!teacherId.value || !courseId.value) {
    ElMessage.warning('参数不完整，无法获取学生列表')
    return
  }
  
  loading.value = true
  getTeacherCourseStudents(teacherId.value, courseId.value, queryParams)
    .then(res => {
      if (isSingleStudentView) {
        // 单个学生的考勤记录视图
        const student = res.data.student || {}
        studentList.value = student.attendanceRecords || []
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
      console.error('获取数据失败:', err)
      ElMessage.error('获取数据失败')
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

// 格式化日期
function formatDate(dateStr) {
  return formatDateUtil(dateStr, 'YYYY-MM-DD')
}

// 获取考勤标签类型
function getAttendanceTagType(status) {
  const statusMap = {
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
      student.attendanceStatus = 'present'
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
        status: student.attendanceStatus,
        remark: student.remark || ''
      }))
      }

    submitStudentAttendance(teacherId.value, courseId.value, attendanceData)
      .then(() => {
        ElMessage.success('考勤记录保存成功')
        recording.value = false
        fetchStudentList() // 刷新数据
      })
      .catch(err => {
        console.error('考勤记录保存失败:', err)
        ElMessage.error('考勤记录保存失败')
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
  router.push({
    name: 'AttendanceRecord',
    query: { 
      courseId: courseId.value,
      courseOfferingId: courseOfferingId.value,
      studentId: row.studentId 
    }
  })
}

// 查看考勤记录详情
function handleViewAttendanceDetail(record) {
  // 这里可以显示一个弹窗，展示更详细的考勤记录
  ElMessage.info(`${formatDate(record.date)}: ${translateAttendanceStatus(record.status)}${record.remark ? ', 备注: ' + record.remark : ''}`)
}

// 单独考勤
function handleEditAttendance(row) {
  attendanceDialog.title = '记录考勤'
  attendanceDialog.visible = true
  
  attendanceForm.id = row.id
  attendanceForm.studentId = row.studentId
  attendanceForm.studentName = row.studentName
  attendanceForm.studentNo = row.studentNo
  attendanceForm.date = queryParams.attendanceDate
  attendanceForm.status = 'present'
  attendanceForm.remark = ''
}

// 提交单独考勤
function submitAttendance() {
  attendanceFormRef.value?.validate(valid => {
    if (!valid) return
    
    submitLoading.value = true
    const submitData = {
      date: attendanceForm.date,
      records: [{
        studentId: attendanceForm.studentId,
        status: attendanceForm.status,
        remark: attendanceForm.remark
      }]
    }
    
    submitStudentAttendance(teacherId.value, courseId.value, submitData)
      .then(() => {
        ElMessage.success('考勤记录提交成功')
        attendanceDialog.visible = false
        fetchStudentList()
      })
      .catch(err => {
        console.error('考勤记录提交失败:', err)
        ElMessage.error('考勤记录提交失败')
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
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
</style> 