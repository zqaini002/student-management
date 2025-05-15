<template>
  <div class="teacher-courses-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="queryParams.semester" placeholder="请输入学期" clearable />
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

    <!-- 课程列表 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">我的授课列表</span>
          <div class="header-buttons">
            <el-button 
              type="primary"
              @click="exportList"
              :loading="exportLoading"
              v-hasPerms="['course:teaching:list']"
            >
              <el-icon><Download /></el-icon>导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="courseList"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="courseCode" label="课程编码" width="120" show-overflow-tooltip />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="semester" label="学期" width="120" show-overflow-tooltip />
        <el-table-column prop="classTime" label="上课时间" width="150" show-overflow-tooltip />
        <el-table-column prop="location" label="上课地点" width="150" show-overflow-tooltip />
        <el-table-column prop="selectedCount" label="选课人数" width="100" align="center" />
        <el-table-column prop="capacity" label="课程容量" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? '正常' : '已结课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleManageStudents(scope.row)">
              学生名单
            </el-button>
            <el-button type="primary" link @click="handleGradeInput(scope.row)">
              成绩录入
            </el-button>
            <el-button type="primary" link @click="handleAttendance(scope.row)">
              考勤管理
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import { getTeacherCourses } from '@/api/teacher'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()

// 用户信息
const userStore = useUserStore()
const teacherId = ref(null)

// 列表数据
const loading = ref(false)
const exportLoading = ref(false)
const courseList = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  courseName: '',
  semester: '',
  pageNum: 1,
  pageSize: 10
})

// 初始化
onMounted(async () => {
  await fetchTeacherId()
  fetchCourseList()
})

// 获取教师ID
async function fetchTeacherId() {
  const userInfo = await userStore.getUserInfo()
  if (userInfo && userInfo.teacherId) {
    teacherId.value = userInfo.teacherId
  } else {
    ElMessage.warning('获取教师信息失败，请重新登录')
  }
}

// 获取课程列表
function fetchCourseList() {
  if (!teacherId.value) {
    ElMessage.warning('教师ID不存在，无法获取课程列表')
    return
  }
  
  loading.value = true
  getTeacherCourses(teacherId.value, queryParams)
    .then(res => {
      courseList.value = res.data.list || []
      total.value = res.data.total || 0
    })
    .catch(err => {
      console.error('获取教师课程列表失败:', err)
      ElMessage.error('获取教师课程列表失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 查询
function handleQuery() {
  queryParams.pageNum = 1
  fetchCourseList()
}

// 重置
function resetQuery() {
  queryParams.courseName = ''
  queryParams.semester = ''
  handleQuery()
}

// 分页
function handleSizeChange(size) {
  queryParams.pageSize = size
  fetchCourseList()
}

function handleCurrentChange(page) {
  queryParams.pageNum = page
  fetchCourseList()
}

// 操作方法
function handleManageStudents(row) {
  router.push({
    name: 'TeacherCourseStudents',
    params: { id: row.id, courseId: row.courseId }
  })
}

function handleGradeInput(row) {
  router.push({
    name: 'GradeInput',
    query: { courseId: row.courseId, courseOfferingId: row.id }
  })
}

function handleAttendance(row) {
  router.push({
    name: 'AttendanceRecord',
    query: { courseId: row.courseId, courseOfferingId: row.id }
  })
}

function exportList() {
  ElMessage.success('导出功能待实现')
}
</script>

<style scoped>
.teacher-courses-container {
  padding: 10px;
}

.search-card {
  margin-bottom: 15px;
}

.table-card {
  margin-bottom: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style> 