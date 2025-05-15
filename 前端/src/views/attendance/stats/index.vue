<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤统计分析</span>
        </div>
      </template>

      <el-form :model="queryParams" ref="queryForm" :inline="true" class="stats-form">
        <el-form-item label="统计类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择统计类型" @change="handleTypeChange" style="width: 180px;">
            <el-option label="按课程统计" value="course" />
            <el-option label="按班级统计" value="class" />
            <el-option label="按日期统计" value="date" />
            <el-option label="按考勤状态统计" value="status" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 320px;"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="课程" prop="courseId" v-if="showCourseFilter">
          <el-autocomplete
            v-model="courseSearchText"
            :fetch-suggestions="searchCourses"
            placeholder="请输入课程名称"
            @select="handleCourseSelect"
            :trigger-on-focus="false"
            style="width: 250px;"
          >
            <template #default="{ item }">
              <div class="course-item">
                {{ item.name }} <span class="course-code">({{ item.code }})</span>
              </div>
            </template>
          </el-autocomplete>
        </el-form-item>

        <el-form-item label="班级" prop="classId" v-if="showClassFilter">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" filterable style="width: 250px;">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="charts-container">
      <el-row :gutter="20">
        <!-- 考勤状态分布卡片 -->
        <el-col :span="24">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>考勤状态分布</span>
              </div>
            </template>
            <div class="chart-content">
              <div id="statusChart" class="chart" ref="statusChartRef"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 考勤趋势图 -->
        <el-col :span="24">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>考勤趋势分析</span>
              </div>
            </template>
            <div class="chart-content">
              <div id="trendChart" class="chart" ref="trendChartRef"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      </div>

      <!-- 考勤统计表格 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card class="table-card" shadow="hover">
            <template #header>
              <div class="table-header">
                <span>考勤统计详情</span>
                <el-button type="primary" icon="Download" size="small" @click="handleExport">导出</el-button>
              </div>
            </template>
            <div class="table-container">
              <el-table v-loading="loading" :data="statisticsList" border stripe>
              <!-- 按课程统计 -->
              <template v-if="queryParams.type === 'course'">
                  <el-table-column label="课程名称" align="center" prop="courseName" min-width="120" />
                  <el-table-column label="课程编码" align="center" prop="courseCode" min-width="100" />
                  <el-table-column label="出勤率" align="center" prop="attendanceRate" min-width="120">
                  <template #default="scope">
                    <el-progress 
                      :percentage="parseFloat(scope.row.attendanceRate)" 
                      :color="getAttendanceRateColor(scope.row.attendanceRate)" 
                    />
                  </template>
                </el-table-column>
                  <el-table-column label="出勤" align="center" prop="normalCount" min-width="80" />
                  <el-table-column label="缺勤" align="center" prop="absentCount" min-width="80" />
                  <el-table-column label="迟到" align="center" prop="lateCount" min-width="80" />
                  <el-table-column label="早退" align="center" prop="earlyLeaveCount" min-width="80" />
                  <el-table-column label="请假" align="center" prop="leaveCount" min-width="80" />
                  <el-table-column label="总记录数" align="center" prop="totalCount" min-width="100" />
              </template>
              
              <!-- 按班级统计 -->
              <template v-if="queryParams.type === 'class'">
                  <el-table-column label="班级名称" align="center" prop="className" min-width="150" />
                  <el-table-column label="班级编码" align="center" prop="classCode" min-width="100" />
                  <el-table-column label="出勤率" align="center" prop="attendanceRate" min-width="120">
                  <template #default="scope">
                    <el-progress 
                      :percentage="parseFloat(scope.row.attendanceRate)" 
                      :color="getAttendanceRateColor(scope.row.attendanceRate)" 
                    />
                  </template>
                </el-table-column>
                  <el-table-column label="出勤" align="center" prop="normalCount" min-width="80" />
                  <el-table-column label="缺勤" align="center" prop="absentCount" min-width="80" />
                  <el-table-column label="迟到" align="center" prop="lateCount" min-width="80" />
                  <el-table-column label="早退" align="center" prop="earlyLeaveCount" min-width="80" />
                  <el-table-column label="请假" align="center" prop="leaveCount" min-width="80" />
                  <el-table-column label="总记录数" align="center" prop="totalCount" min-width="100" />
              </template>
              
              <!-- 按日期统计 -->
              <template v-if="queryParams.type === 'date'">
                  <el-table-column label="日期" align="center" prop="date" min-width="120" />
                  <el-table-column label="出勤率" align="center" prop="attendanceRate" min-width="120">
                  <template #default="scope">
                    <el-progress 
                      :percentage="parseFloat(scope.row.attendanceRate)" 
                      :color="getAttendanceRateColor(scope.row.attendanceRate)" 
                    />
                  </template>
                </el-table-column>
                  <el-table-column label="出勤" align="center" prop="normalCount" min-width="80" />
                  <el-table-column label="缺勤" align="center" prop="absentCount" min-width="80" />
                  <el-table-column label="迟到" align="center" prop="lateCount" min-width="80" />
                  <el-table-column label="早退" align="center" prop="earlyLeaveCount" min-width="80" />
                  <el-table-column label="请假" align="center" prop="leaveCount" min-width="80" />
                  <el-table-column label="总记录数" align="center" prop="totalCount" min-width="100" />
              </template>
              
              <!-- 按考勤状态统计 -->
              <template v-if="queryParams.type === 'status'">
                  <el-table-column label="考勤状态" align="center" prop="statusName" min-width="120" />
                  <el-table-column label="记录数" align="center" prop="count" min-width="100" />
                  <el-table-column label="占比" align="center" prop="percentage" min-width="180">
                  <template #default="scope">
                    <el-progress 
                      :percentage="parseFloat(scope.row.percentage)" 
                      :color="getStatusColor(scope.row.status)" 
                    />
                  </template>
                </el-table-column>
              </template>
            </el-table>
            
            <pagination
              v-show="total > 0"
              :total="total"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              @pagination="getList"
            />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getAttendanceStatistics, exportAttendance, getCourseAttendanceStatistics, getClassAttendanceStatistics } from '@/api/attendance'
import { getTeacherCourses } from '@/api/teacher'
import { getClassList } from '@/api/class'
import { getCourseListForAttendance } from '@/api/course'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const userId = ref(userStore.userId)

// 图表引用
const statusChartRef = ref(null)
const trendChartRef = ref(null)
let statusChart = null
let trendChart = null

// 日期范围
const dateRange = ref([])

// 统计列表
const statisticsList = ref([])
const loading = ref(false)
const total = ref(0)

// 课程选项
const courseOptions = ref([])
const courseLoading = ref(false)

// 班级选项
const classOptions = ref([])
const classLoading = ref(false)

// 显示控制变量
const showCourseFilter = ref(false)
const showClassFilter = ref(false)

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  type: 'course', // 默认按课程统计
  startDate: undefined,
  endDate: undefined,
  courseId: undefined,
  classId: undefined
})

// 课程搜索文本
const courseSearchText = ref('')

// 监听查询参数类型变化
watch(() => queryParams.value.type, (newType) => {
  handleTypeChange(newType)
})

// 加载课程选项
function remoteCourseMethod(query) {
  if (query !== '') {
    courseLoading.value = true
    getCourseListForAttendance({
      keyword: query,
      pageNum: 1,
      pageSize: 20
    }).then(response => {
      if (response.code === 200 && response.data && response.data.list) {
        const courses = response.data.list;
        // 处理课程数据，确保字段格式正确
        courseOptions.value = courses.map(course => {
          return {
            id: course.id,
            courseName: course.name,
            semester: course.semesterName || '当前学期',
            credit: course.credit
          };
        });
      } else {
        console.error('未能识别的课程数据结构:', response);
        // 使用备用方法
        fallbackCourseSearch(query);
      }
      courseLoading.value = false;
    }).catch(() => {
      fallbackCourseSearch(query);
      courseLoading.value = false;
    });
  } else {
    courseOptions.value = [];
  }
}

// 备用课程搜索方法
function fallbackCourseSearch(query) {
  getTeacherCourses(userId.value, {
    pageNum: 1,
    pageSize: 10,
    courseName: query
  }).then(response => {
    if (response.data && response.data.records) {
      courseOptions.value = response.data.records;
    } else if (response.data && response.data.list) {
      courseOptions.value = response.data.list;
    }
  }).catch(() => {
    courseOptions.value = [];
  });
}

// 加载班级选项
function loadClassOptions() {
  classLoading.value = true
  getClassList({
    pageNum: 1,
    pageSize: 100
  }).then(response => {
    classOptions.value = response.data.records
    classLoading.value = false
  }).catch(() => {
    classLoading.value = false
  })
}

// 处理统计类型变更
function handleTypeChange(type) {
  // 重置表单参数
  queryParams.value.courseId = undefined
  queryParams.value.classId = undefined
  courseSearchText.value = ''
  
  // 根据类型显示不同的过滤条件
  switch (type) {
    case 'course':
      showCourseFilter.value = false
      showClassFilter.value = false
      break
    case 'class':
      showCourseFilter.value = false
      showClassFilter.value = false
      loadClassOptions()
      break
    case 'date':
      showCourseFilter.value = true
      showClassFilter.value = true
      loadClassOptions()
      break
    case 'status':
      showCourseFilter.value = true
      showClassFilter.value = true
      loadClassOptions()
      break
    default:
      showCourseFilter.value = false
      showClassFilter.value = false
      break
  }
  
  // 重新查询数据
  handleQuery()
}

// 查询按钮操作
function handleQuery() {
  queryParams.value.pageNum = 1
  // 处理日期范围
  if (dateRange.value && dateRange.value.length > 0) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }
  
  getList()
}

// 重置按钮操作
function resetQuery() {
  dateRange.value = []
  courseSearchText.value = ''
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    type: 'course',
    startDate: undefined,
    endDate: undefined,
    courseId: undefined,
    classId: undefined
  }
  handleQuery()
}

// 获取考勤统计数据
function getList() {
  loading.value = true
  
  getAttendanceStatistics(queryParams.value).then(response => {
    // 检查响应数据格式
    console.log('考勤统计响应数据:', response)
    
    if (response.data && response.data.list) {
      // 后端返回了data.list格式的数据
      statisticsList.value = response.data.list || []
      total.value = parseInt(response.data.total || 0)
    } else if (response.data && Array.isArray(response.data.records)) {
      // 后端返回了data.records格式的数据
      statisticsList.value = response.data.records || []
      total.value = parseInt(response.data.total || 0)
    } else if (Array.isArray(response.data)) {
      // 后端直接返回了数组格式的数据
      statisticsList.value = response.data || []
      total.value = statisticsList.value.length
    } else {
      // 无法识别的数据格式
      console.error('无法识别的响应数据格式:', response.data)
      statisticsList.value = []
      total.value = 0
    }
    
    loading.value = false
    
    // 加载图表数据
    nextTick(() => {
      try {
      initStatusChart()
      initTrendChart()
      } catch (error) {
        console.error('初始化图表时出错:', error)
      }
    })
  }).catch(error => {
    console.error('获取考勤统计数据失败:', error)
    statisticsList.value = []
    total.value = 0
    loading.value = false
  })
}

// 初始化考勤状态分布图表
function initStatusChart() {
  if (!statusChartRef.value) return
  
  // 处理数据
  let statusData = []
  
  // 确保数据存在并有效
  if (!statisticsList.value || statisticsList.value.length === 0) {
    console.log('没有统计数据，不能初始化状态图表')
    // 设置默认空数据
    statusData = [
      { value: 0, name: '出勤' },
      { value: 0, name: '缺勤' },
      { value: 0, name: '迟到' },
      { value: 0, name: '早退' },
      { value: 0, name: '请假' }
    ]
  } else if (queryParams.value.type === 'status') {
    statusData = statisticsList.value.map(item => ({
      value: item.count || 0,
      name: item.statusName || '未知状态'
    }))
  } else {
    // 汇总数据
    const aggregatedData = {
      normalCount: 0,
      absentCount: 0,
      lateCount: 0,
      earlyLeaveCount: 0,
      leaveCount: 0
    }
    
    statisticsList.value.forEach(item => {
      aggregatedData.normalCount += parseInt(item.normalCount || 0)
      aggregatedData.absentCount += parseInt(item.absentCount || 0)
      aggregatedData.lateCount += parseInt(item.lateCount || 0)
      aggregatedData.earlyLeaveCount += parseInt(item.earlyLeaveCount || 0)
      aggregatedData.leaveCount += parseInt(item.leaveCount || 0)
    })
    
    statusData = [
      { value: aggregatedData.normalCount, name: '出勤' },
      { value: aggregatedData.absentCount, name: '缺勤' },
      { value: aggregatedData.lateCount, name: '迟到' },
      { value: aggregatedData.earlyLeaveCount, name: '早退' },
      { value: aggregatedData.leaveCount, name: '请假' }
    ]
  }
  
  if (statusChart) {
    statusChart.dispose()
  }
  
  statusChart = echarts.init(statusChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom',
      data: statusData.map(item => item.name)
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
        data: statusData,
        color: ['#67C23A', '#F56C6C', '#E6A23C', '#909399', '#909399']
      }
    ]
  }
  
  statusChart.setOption(option)
  
  window.addEventListener('resize', () => {
    statusChart && statusChart.resize()
  })
}

// 初始化考勤趋势图表
function initTrendChart() {
  if (!trendChartRef.value) return
  
  let trendData = []
  let xAxisData = []
  
  // 确保数据存在并有效
  if (!statisticsList.value || statisticsList.value.length === 0) {
    console.log('没有统计数据，不能初始化趋势图表')
    // 设置一些默认数据以便显示空图表
    xAxisData = ['暂无数据']
    trendData = [{
      name: '出勤率',
      type: 'bar',
      data: [0],
      itemStyle: {
        color: '#67C23A'
      }
    }]
  } else if (queryParams.value.type === 'date') {
    // 日期趋势
    statisticsList.value.forEach(item => {
      xAxisData.push(item.date || '未知日期')
    })
    
    // 各状态的趋势数据
    const normalData = statisticsList.value.map(item => parseInt(item.normalCount || 0))
    const absentData = statisticsList.value.map(item => parseInt(item.absentCount || 0))
    const lateData = statisticsList.value.map(item => parseInt(item.lateCount || 0))
    const earlyLeaveData = statisticsList.value.map(item => parseInt(item.earlyLeaveCount || 0))
    const leaveData = statisticsList.value.map(item => parseInt(item.leaveCount || 0))
    
    trendData = [
      {
        name: '出勤',
        type: 'line',
        stack: 'Total',
        data: normalData,
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '缺勤',
        type: 'line',
        stack: 'Total',
        data: absentData,
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '迟到',
        type: 'line',
        stack: 'Total',
        data: lateData,
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '早退',
        type: 'line',
        stack: 'Total',
        data: earlyLeaveData,
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '请假',
        type: 'line',
        stack: 'Total',
        data: leaveData,
        emphasis: {
          focus: 'series'
        }
      }
    ]
  } else if (queryParams.value.type === 'course' || queryParams.value.type === 'class') {
    // 按课程或班级统计的趋势
    const nameKey = queryParams.value.type === 'course' ? 'courseName' : 'className'
    statisticsList.value.forEach(item => {
      xAxisData.push(item[nameKey] || '未知')
    })
    
    // 出勤率趋势
    const attendanceRateData = statisticsList.value.map(item => parseFloat(item.attendanceRate || 0))
    
    trendData = [
      {
        name: '出勤率',
        type: 'bar',
        data: attendanceRateData,
        itemStyle: {
          color: function(params) {
            return getAttendanceRateColor(params.value)
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%'
        }
      }
    ]
  }
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: trendData.map(item => item.name)
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: function(value) {
          return queryParams.value.type === 'date' ? value : value + '%'
        }
      },
      max: queryParams.value.type !== 'date' ? 100 : undefined
    },
    series: trendData,
    color: ['#67C23A', '#F56C6C', '#E6A23C', '#909399', '#409EFF']
  }
  
  trendChart.setOption(option)
  
  window.addEventListener('resize', () => {
    trendChart && trendChart.resize()
  })
}

// 导出按钮操作
function handleExport() {
  // 处理日期范围
  const exportParams = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length > 0) {
    exportParams.startDate = dateRange.value[0]
    exportParams.endDate = dateRange.value[1]
  }
  
  exportAttendance({
    ...exportParams,
    export: true
  }).then(response => {
    const blob = new Blob([response.data])
    const fileName = `考勤统计_${new Date().getTime()}.xlsx`
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName
    link.click()
    URL.revokeObjectURL(link.href)
  })
}

// 获取出勤率颜色
function getAttendanceRateColor(rate) {
  rate = parseFloat(rate)
  if (rate >= 90) {
    return '#67C23A' // 绿色 - 优秀
  } else if (rate >= 80) {
    return '#409EFF' // 蓝色 - 良好
  } else if (rate >= 70) {
    return '#E6A23C' // 黄色 - 中等
  } else {
    return '#F56C6C' // 红色 - 不及格
  }
}

// 获取考勤状态颜色
function getStatusColor(status) {
  switch (status) {
    case 0: return '#67C23A' // 出勤 - 绿色
    case 1: return '#F56C6C' // 缺勤 - 红色
    case 2: return '#E6A23C' // 迟到 - 黄色
    case 3: return '#909399' // 早退 - 灰色
    case 4: return '#409EFF' // 请假 - 蓝色
    default: return '#409EFF'
  }
}

// 课程搜索方法
function searchCourses(query, cb) {
  if (query) {
    getCourseListForAttendance({
      keyword: query,
      pageNum: 1,
      pageSize: 10
    }).then(response => {
      console.log('课程搜索结果:', response)
      if (response.code === 200 && response.data && response.data.list) {
        const suggestions = response.data.list.map(course => ({
          value: course.name,
          name: course.name,
          id: course.id,
          code: course.code
        }));
        // 调用回调函数返回建议列表
        cb(suggestions);
      } else {
        cb([]);
      }
    }).catch(() => {
      cb([]);
    });
  } else {
    cb([]);
  }
}

// 处理课程选择
function handleCourseSelect(item) {
  console.log('选中课程:', item);
  // 设置课程ID和名称
  queryParams.value.courseId = item.id;
  courseSearchText.value = item.name;
  // 自动触发查询
  handleQuery();
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
  min-width: 1200px;  /* 设置最小宽度，确保内容不会过度挤压 */
}

.box-card {
  margin-bottom: 20px;
}

.stats-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
}

.charts-container {
  margin-top: 20px;
  width: 100%;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-content {
  width: 100%;
  overflow-x: auto;
}

.chart {
  height: 400px;
  width: 100%;
  min-width: 800px;  /* 确保图表有足够的宽度 */
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-container {
  width: 100%;
  overflow-x: auto;
}

.course-item {
  display: flex;
  justify-content: space-between;
}

.course-code {
  font-size: 12px;
  color: #909399;
}

/* 添加响应式布局 */
@media screen and (max-width: 1400px) {
  .el-form {
    display: flex;
    flex-wrap: wrap;
  }
  
  .el-form-item {
    margin-right: 10px;
    margin-bottom: 10px;
  }
}

/* 优化表格在各种屏幕大小下的显示 */
.el-table {
  width: 100%;
  overflow-x: auto;
}

/* 添加表格样式 */
:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #f8f8f9;
}

:deep(.el-table .el-table__header-wrapper th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}
</style> 