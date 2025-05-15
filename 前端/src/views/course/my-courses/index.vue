<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>

      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="queryParams.courseName"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="课程编码" prop="courseCode">
          <el-input
            v-model="queryParams.courseCode"
            placeholder="请输入课程编码"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="queryParams.semester" placeholder="请选择学期" clearable>
            <el-option
              v-for="dict in semesterOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 课程统计 -->
      <el-row :gutter="20" class="mb20">
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">已选课程总数</div>
            <div class="statistic-number">{{ courseStats.total }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">本学期课程</div>
            <div class="statistic-number">{{ courseStats.current }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">学分总数</div>
            <div class="statistic-number">{{ courseStats.totalCredits }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">课时总数</div>
            <div class="statistic-number">{{ courseStats.totalHours }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="courseList">
        <el-table-column label="课程名称" align="center" prop="courseName" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="课程编码" align="center" prop="courseCode" width="120" />
        <el-table-column label="学分" align="center" prop="credits" width="80" />
        <el-table-column label="课时" align="center" prop="courseDuration" width="80" />
        <el-table-column label="授课教师" align="center" prop="teacherName" width="100" />
        <el-table-column label="上课地点" align="center" prop="location" width="120" :show-overflow-tooltip="true" />
        <el-table-column label="上课时间" align="center" prop="courseTime" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="学期" align="center" prop="semester" width="100" />
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 0 ? 'info' : 'danger'">
              {{ scope.row.status === 1 ? '进行中' : scope.row.status === 0 ? '未开始' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              type="primary"
              link
              icon="View"
              @click="handleDetail(scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog title="课程详情" v-model="dialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ courseDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程编码">{{ courseDetail.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ courseDetail.credits }}</el-descriptions-item>
        <el-descriptions-item label="课时">{{ courseDetail.courseDuration }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ courseDetail.departmentName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="课程容量">
          {{ courseDetail.selectedCount || 0 }}/{{ courseDetail.capacity || '不限' }}
        </el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ courseDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="教师联系方式">{{ courseDetail.teacherContact }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ courseDetail.location }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ courseDetail.courseTime }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ courseDetail.semester }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="courseDetail.status === 1 ? 'success' : courseDetail.status === 0 ? 'info' : 'danger'">
            {{ courseDetail.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="课程简介" :span="2">{{ courseDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="考核方式" :span="2">{{ courseDetail.assessmentMethod }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentOwnCourses, getCourseDetail } from '@/api/course'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const studentId = ref(userStore.userId)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const courseList = ref([])
const dialogVisible = ref(false)
const courseDetail = ref({})

// 学期选项
const semesterOptions = ref([
  { value: '2024-2025-1', label: '2024-2025学年第一学期' },
  { value: '2024-2025-2', label: '2024-2025学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' },
  { value: '2023-2024-2', label: '2023-2024学年第二学期' }
])

// 课程统计数据
const courseStats = ref({
  total: 0,
  current: 0,
  totalCredits: 0,
  totalHours: 0
})

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  courseName: undefined,
  courseCode: undefined,
  semester: undefined
})

/** 查询我的课程列表 */
function getList() {
  loading.value = true
  getStudentOwnCourses(studentId.value, queryParams.value).then(response => {
    // 检查响应是否成功
    if (response && response.code === 200 && response.data) {
      // 修复数据解析，正确处理后端返回的数据结构
      courseList.value = response.data.rows || response.data.list || []
      total.value = parseInt(response.data.total || 0)
      
      // 处理字段映射，确保前端显示正确的字段
      courseList.value = courseList.value.map(item => {
        return {
          ...item,
          // 确保关键字段映射正确
          credits: item.credit || 0,
          courseDuration: item.hours || 0,
          courseTime: item.classTime || '',
          status: item.status !== undefined ? item.status : 0
        }
      })
      
    calculateCourseStats()
    } else {
      ElMessage.error('获取课程列表失败')
      courseList.value = []
      total.value = 0
    }
    loading.value = false
  }).catch((error) => {
    console.error('获取课程列表错误:', error)
    ElMessage.error('获取课程列表失败: ' + (error.message || '未知错误'))
    courseList.value = []
    total.value = 0
    loading.value = false
  })
}

/** 计算课程统计数据 */
function calculateCourseStats() {
  // 获取当前学期
  const currentSemester = '2023-2024-1' // 修改为与数据库一致的学期值
  
  courseStats.value.total = courseList.value.length
  courseStats.value.current = courseList.value.filter(item => item.semester === currentSemester).length
  
  // 计算总学分和总课时
  courseStats.value.totalCredits = courseList.value.reduce((sum, item) => sum + (item.credits || item.credit || 0), 0)
  courseStats.value.totalHours = courseList.value.reduce((sum, item) => sum + (item.courseDuration || item.hours || 0), 0)
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
    courseName: undefined,
    courseCode: undefined,
    semester: undefined
  }
  handleQuery()
}

/** 查看课程详情 */
function handleDetail(row) {
  dialogVisible.value = true
  getCourseDetail(row.courseId).then(response => {
    if (response && response.code === 200 && response.data) {
      console.log('课程详情原始数据:', response.data)
      // 处理字段映射，确保前端显示正确的字段
      courseDetail.value = {
        ...response.data,
        // 确保关键字段映射正确
        courseName: response.data.name || response.data.courseName || '',
        courseCode: response.data.code || response.data.courseCode || '',
        credits: response.data.credit || response.data.credits || 0,
        courseDuration: response.data.hours || response.data.courseDuration || 0,
        // 直接使用后端返回的教师信息
        teacherName: response.data.teacherName || '',
        teacherContact: response.data.teacherContact || '暂无',
        // 直接使用后端返回的位置和时间信息
        location: response.data.location || '',
        courseTime: response.data.classTime || '',
        semester: response.data.semester || '',
        // 状态信息处理
        status: response.data.status !== undefined ? response.data.status : 0,
        statusText: response.data.statusDesc || (
          response.data.status === 1 ? '进行中' : 
          response.data.status === 0 ? '未开始' : 
          response.data.status === 2 ? '已结束' : '未知状态'
        ),
        description: response.data.description || '暂无描述',
        assessmentMethod: response.data.assessmentMethod || '暂无信息',
        // 额外信息
        departmentName: response.data.departmentName || '',
        capacity: response.data.capacity || 0,
        selectedCount: response.data.selectedCount || 0
      }
    } else {
      ElMessage.error('获取课程详情失败')
      courseDetail.value = {}
    }
  }).catch(error => {
    console.error('获取课程详情错误:', error)
    ElMessage.error('获取课程详情失败: ' + (error.message || '未知错误'))
    courseDetail.value = {}
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.statistic-card {
  text-align: center;
  padding: 10px;
  border-radius: 8px;
}

.statistic-title {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
}

.statistic-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.mb20 {
  margin-bottom: 20px;
}

.empty-data {
  padding: 30px 0;
}
</style> 