<template>
  <div class="course-students-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">课程学生名单</span>
            <span class="course-info" v-if="courseInfo">
              {{ courseInfo.courseName }} - {{ courseInfo.semester }}
            </span>
          </div>
          <el-button @click="goBack" link>
            <el-icon><Back /></el-icon>返回
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="queryParams.studentName" placeholder="请输入姓名" clearable />
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

      <!-- 操作按钮 -->
      <div class="operation-container">
        <el-button type="primary" @click="handleImportGrades" :loading="importLoading">
          <el-icon><Upload /></el-icon>导入成绩
        </el-button>
        <el-button type="primary" @click="handleExportStudents" :loading="exportLoading">
          <el-icon><Download /></el-icon>导出学生名单
        </el-button>
        <el-button type="primary" @click="handleExportTemplate">
          <el-icon><Document /></el-icon>下载成绩模板
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
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            {{ scope.row.gender === 0 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="180" />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.score !== null && scope.row.score !== undefined">
              {{ scope.row.score }}
            </span>
            <span v-else class="no-score">未录入</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="等级" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.grade" :type="getGradeTagType(scope.row.grade)">
              {{ scope.row.grade }}
            </el-tag>
            <span v-else class="no-grade">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="attendanceRate" label="出勤率" width="100" align="center">
          <template #default="scope">
            <el-progress 
              :percentage="scope.row.attendanceRate || 0" 
              :color="getAttendanceColor(scope.row.attendanceRate)"
              :format="(percentage) => `${percentage}%`"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditGrade(scope.row)">
              编辑成绩
            </el-button>
            <el-button type="primary" link @click="handleAttendance(scope.row)">
              考勤记录
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

    <!-- 成绩录入对话框 -->
    <el-dialog v-model="gradeDialog.visible" :title="gradeDialog.title" width="500px" append-to-body>
      <el-form ref="gradeFormRef" :model="gradeForm" :rules="gradeRules" label-width="80px">
        <el-form-item label="学生" prop="studentName">
          <el-input v-model="gradeForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="gradeForm.studentNo" disabled />
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input v-model="gradeForm.comment" type="textarea" rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="gradeDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitGrade" :loading="submitLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Back, Upload, Download, Document } from '@element-plus/icons-vue'
import { getTeacherCourseStudents, submitStudentGrade } from '@/api/teacher'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()
const route = useRoute()
const courseId = computed(() => route.params.courseId)
const courseOfferingId = computed(() => route.params.id)

// 用户信息
const userStore = useUserStore()
const teacherId = ref(null)

// 课程信息
const courseInfo = ref(null)

// 学生列表
const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const importLoading = ref(false)
const exportLoading = ref(false)

// 查询参数
const queryParams = reactive({
  courseId: null,
  courseOfferingId: null,
  studentNo: '',
  studentName: '',
  pageNum: 1,
  pageSize: 10
})

// 成绩对话框
const gradeDialog = reactive({
  visible: false,
  title: '录入成绩'
})
const gradeForm = reactive({
  id: null,
  studentId: null,
  studentName: '',
  studentNo: '',
  score: null,
  comment: ''
})
const gradeRules = {
  score: [
    { required: true, message: '请输入成绩', trigger: 'blur' }
  ]
}
const gradeFormRef = ref(null)
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
      studentList.value = res.data.list || []
      total.value = res.data.total || 0
      
      // 更新课程信息
      if (res.data.courseInfo) {
        courseInfo.value = res.data.courseInfo
      }
    })
    .catch(err => {
      console.error('获取课程学生列表失败:', err)
      ElMessage.error('获取课程学生列表失败')
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

// 获取等级标签类型
function getGradeTagType(grade) {
  const gradeMap = {
    'A+': 'success',
    'A': 'success',
    'B+': 'success',
    'B': 'warning',
    'C+': 'warning',
    'C': 'warning',
    'D': 'danger',
    'F': 'danger'
  }
  return gradeMap[grade] || 'info'
}

// 获取出勤率颜色
function getAttendanceColor(rate) {
  if (rate === null || rate === undefined) return '#909399'
  if (rate >= 90) return '#67C23A'
  if (rate >= 80) return '#E6A23C'
  return '#F56C6C'
}

// 编辑成绩
function handleEditGrade(row) {
  gradeDialog.title = '编辑成绩'
  gradeDialog.visible = true
  
  gradeForm.id = row.id
  gradeForm.studentId = row.studentId
  gradeForm.studentName = row.studentName
  gradeForm.studentNo = row.studentNo
  gradeForm.score = row.score
  gradeForm.comment = row.comment || ''
}

// 提交成绩
function submitGrade() {
  gradeFormRef.value?.validate(valid => {
    if (!valid) return
    
    submitLoading.value = true
    const submitData = {
      studentId: gradeForm.studentId,
      score: gradeForm.score,
      comment: gradeForm.comment
    }
    
    submitStudentGrade(teacherId.value, courseId.value, submitData)
      .then(() => {
        ElMessage.success('成绩提交成功')
        gradeDialog.visible = false
        fetchStudentList()
      })
      .catch(err => {
        console.error('成绩提交失败:', err)
        ElMessage.error('成绩提交失败')
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

// 导入成绩
function handleImportGrades() {
  ElMessage.info('导入成绩功能待实现')
}

// 导出学生名单
function handleExportStudents() {
  ElMessage.info('导出学生名单功能待实现')
}

// 下载成绩模板
function handleExportTemplate() {
  ElMessage.info('下载成绩模板功能待实现')
}

// 查看考勤记录
function handleAttendance(row) {
  router.push({
    name: 'AttendanceRecord',
    query: { 
      courseId: courseId.value,
      courseOfferingId: courseOfferingId.value,
      studentId: row.studentId 
    }
  })
}
</script>

<style scoped>
.course-students-container {
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

.no-score,
.no-grade {
  color: #909399;
}
</style> 