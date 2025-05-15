<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的考勤记录</span>
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
        <el-form-item label="考勤日期" prop="attendanceDate">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="考勤状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择考勤状态" clearable>
            <el-option
              v-for="dict in statusOptions"
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
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPerms="['attendance:record:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 考勤统计卡片 -->
      <el-row :gutter="20" class="mb20">
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">总考勤</div>
            <div class="statistic-number">{{ attendanceSummary.total }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card normal">
            <div class="statistic-title">出勤</div>
            <div class="statistic-number">{{ attendanceSummary.normal }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card absent">
            <div class="statistic-title">缺勤</div>
            <div class="statistic-number">{{ attendanceSummary.absent }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card late">
            <div class="statistic-title">迟到</div>
            <div class="statistic-number">{{ attendanceSummary.late }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card early-leave">
            <div class="statistic-title">早退</div>
            <div class="statistic-number">{{ attendanceSummary.earlyLeave }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="statistic-card leave">
            <div class="statistic-title">请假</div>
            <div class="statistic-number">{{ attendanceSummary.leave }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="attendanceList">
        <el-table-column label="课程名称" align="center" prop="courseName" :show-overflow-tooltip="true" />
        <el-table-column label="课程编码" align="center" prop="courseCode" />
        <el-table-column label="班级" align="center" prop="className" :show-overflow-tooltip="true" />
        <el-table-column label="考勤日期" align="center" prop="attendanceDate" width="100" />
        <el-table-column label="考勤状态" align="center" prop="statusDesc" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'danger' : scope.row.status === 2 ? 'warning' : scope.row.status === 3 ? 'info' : ''"
            >{{ scope.row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
      
      <!-- 出勤率统计图表 -->
      <el-card class="chart-card" shadow="hover" style="margin-top: 20px;">
        <template #header>
          <div class="chart-header">
            <span>考勤统计分析</span>
          </div>
        </template>
        <div class="chart-content">
          <div id="attendanceChart" class="chart" ref="attendanceChartRef"></div>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentAttendance, exportAttendance } from '@/api/attendance'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const userId = ref(userStore.userId)
const studentId = ref(userStore.userId) // 学生ID与用户ID一致

// 图表引用
const attendanceChartRef = ref(null)
let attendanceChart = null

const attendanceList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)

// 考勤统计汇总
const attendanceSummary = ref({
  total: 0,
  normal: 0,
  absent: 0,
  late: 0,
  earlyLeave: 0,
  leave: 0
})

// 状态数据字典
const statusOptions = ref([
  { value: 0, label: '出勤' },
  { value: 1, label: '缺勤' },
  { value: 2, label: '迟到' },
  { value: 3, label: '早退' },
  { value: 4, label: '请假' }
])

// 日期范围
const dateRange = ref([])

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  courseName: undefined,
  startDate: undefined,
  endDate: undefined,
  status: undefined
})

/** 查询考勤记录列表 */
function getList() {
  loading.value = true
  // 处理日期范围
  if (dateRange.value && dateRange.value.length > 0) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }

  // 使用studentId代替userId
  // 学生用户类型在后端处理，当前ID会自动转为对应的studentId
  getStudentAttendance(userStore.userId, queryParams.value).then(response => {
    if (response && response.data) {
      attendanceList.value = response.data.list || []
      total.value = parseInt(response.data.total)
      
      // 处理考勤统计数据
      calculateAttendanceSummary()
      
      // 初始化图表
      nextTick(() => {
        initAttendanceChart()
      })
    } else {
      attendanceList.value = []
      total.value = 0
      attendanceSummary.value = {
        total: 0,
        normal: 0,
        absent: 0,
        late: 0,
        earlyLeave: 0,
        leave: 0
      }
    }
    
    loading.value = false
  }).catch(error => {
    console.error("获取考勤数据失败:", error)
    attendanceList.value = []
    total.value = 0
    loading.value = false
  })
}

/** 计算考勤汇总统计 */
function calculateAttendanceSummary() {
  // 重置统计数据
  attendanceSummary.value = {
    total: attendanceList.value.length,
    normal: 0,
    absent: 0,
    late: 0,
    earlyLeave: 0,
    leave: 0
  }
  
  // 统计各状态数量
  attendanceList.value.forEach(item => {
    switch (item.status) {
      case 0:
        attendanceSummary.value.normal++
        break
      case 1:
        attendanceSummary.value.absent++
        break
      case 2:
        attendanceSummary.value.late++
        break
      case 3:
        attendanceSummary.value.earlyLeave++
        break
      case 4:
        attendanceSummary.value.leave++
        break
    }
  })
}

/** 初始化考勤统计图表 */
function initAttendanceChart() {
  if (!attendanceChartRef.value) return
  
  if (attendanceChart) {
    attendanceChart.dispose()
  }
  
  attendanceChart = echarts.init(attendanceChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom',
      data: ['出勤', '缺勤', '迟到', '早退', '请假']
    },
    series: [
      {
        name: '考勤状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: attendanceSummary.value.normal, name: '出勤' },
          { value: attendanceSummary.value.absent, name: '缺勤' },
          { value: attendanceSummary.value.late, name: '迟到' },
          { value: attendanceSummary.value.earlyLeave, name: '早退' },
          { value: attendanceSummary.value.leave, name: '请假' }
        ],
        color: ['#67C23A', '#F56C6C', '#E6A23C', '#909399', '#409EFF']
      }
    ]
  }
  
  attendanceChart.setOption(option)
  
  window.addEventListener('resize', () => {
    attendanceChart && attendanceChart.resize()
  })
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    courseName: undefined,
    startDate: undefined,
    endDate: undefined,
    status: undefined
  }
  handleQuery()
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 导出按钮操作 */
function handleExport() {
  // 处理日期范围
  const exportParams = { 
    ...queryParams.value,
    studentId: studentId.value,
    export: true
  }
  
  if (dateRange.value && dateRange.value.length > 0) {
    exportParams.startDate = dateRange.value[0]
    exportParams.endDate = dateRange.value[1]
  }

  exportAttendance(exportParams).then(response => {
    const blob = new Blob([response.data])
    const fileName = `我的考勤记录_${new Date().getTime()}.xlsx`
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName
    link.click()
    URL.revokeObjectURL(link.href)
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb20 {
  margin-bottom: 20px;
}

.statistic-card {
  text-align: center;
  padding: 10px;
}

.statistic-title {
  font-size: 14px;
  color: #606266;
}

.statistic-number {
  font-size: 24px;
  font-weight: bold;
  margin-top: 10px;
}

.normal .statistic-number {
  color: #67C23A;
}

.absent .statistic-number {
  color: #F56C6C;
}

.late .statistic-number {
  color: #E6A23C;
}

.early-leave .statistic-number {
  color: #909399;
}

.leave .statistic-number {
  color: #409EFF;
}

.chart {
  height: 300px;
  width: 100%;
}
</style> 