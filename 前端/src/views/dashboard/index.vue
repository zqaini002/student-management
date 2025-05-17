<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-info">
        <el-avatar :size="64" :src="avatar" class="user-avatar" />
        <div class="welcome-text">
          <h1>{{ welcomeText }}</h1>
          <p>欢迎回到EduSmart 高校智能管理平台</p>
        </div>
      </div>
      <div class="current-time">
        <div class="date">{{ currentDate }}</div>
        <div class="time">{{ currentTime }}</div>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :sm="12" :md="6" v-for="(card, index) in statsCards" :key="index" class="card-col">
        <el-card class="stats-card" shadow="hover">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon><component :is="card.icon"></component></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ card.title }}</div>
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-trend">
              <span :class="{'trend-up': card.increase, 'trend-down': !card.increase}">
                {{ card.increase ? '↑' : '↓' }} {{ card.compare }}
              </span>
              <span v-if="index === 3 && card.value === '0.0%'" class="refresh-action">
                <el-button size="small" link @click="refreshStatistics">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表和通知 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :sm="24" :md="12">
        <!-- 学生院系分布/个人学习统计 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span v-if="isStudent">个人学习统计</span>
              <span v-else>学生院系分布</span>
              <el-select v-if="!isStudent" v-model="deptYearFilter" class="filter-select" @change="updateDeptChart">
                <el-option label="今年" value="今年" />
                <el-option label="去年" value="去年" />
                <el-option label="全部" value="全部" />
              </el-select>
            </div>
          </template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :sm="24" :md="12">
        <!-- 月度课程数量/成绩分布 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span v-if="isStudent">成绩分布统计</span>
              <span v-else>月度课程数量</span>
              <el-select v-if="!isStudent" v-model="semesterTimeFilter" class="filter-select" @change="updateSemesterChart">
                <el-option label="近半年" value="近半年" />
                <el-option label="近一年" value="近一年" />
                <el-option label="全部" value="全部" />
              </el-select>
            </div>
          </template>
          <div ref="lineChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 通知和待办 -->
    <el-row :gutter="20" class="notice-row">
      <el-col :sm="24" :md="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近通知</span>
              <el-button class="button" :link="true" @click="viewMoreNotices">查看更多</el-button>
            </div>
          </template>
          <div class="notice-list">
            <div v-for="(notice, index) in recentNotices" :key="index" class="notice-item">
              <div class="notice-icon" :class="notice.type">
                <el-icon><component :is="noticeIcons[notice.type]"></component></el-icon>
              </div>
              <div class="notice-content">
                <div class="notice-title">{{ notice.title }}</div>
                <div class="notice-time">{{ notice.time }}</div>
              </div>
            </div>
            <el-empty v-if="recentNotices.length === 0" description="暂无通知" />
          </div>
        </el-card>
      </el-col>
      <el-col :sm="24" :md="12">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button class="button" :link="true" @click="viewMoreTodos">查看更多</el-button>
            </div>
          </template>
          <div class="todo-list">
            <div v-for="(todo, index) in todoList" :key="index" class="todo-item">
              <el-tag size="small" :type="getTagType(todo.priority)" class="todo-priority">{{ todo.priority }}</el-tag>
              <div class="todo-content">
                <div class="todo-title">{{ todo.title }}</div>
                <div class="todo-time">{{ todo.deadline }}</div>
              </div>
            </div>
            <el-empty v-if="todoList.length === 0" description="暂无待办事项" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts/core'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import { 
  TooltipComponent, 
  GridComponent, 
  LegendComponent,
  TitleComponent,
  ToolboxComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import defaultAvatar from '@/assets/logo.png'
import { formatImageUrl } from '@/utils/image'
import { 
  getStatistics, 
  getTodoList, 
  getNoticeList, 
  getDepartmentDistribution,
  getStudentSemesterData,
  getStudentPersonalStats,
  getStudentGradeDistribution,
  getStudentDepartmentDistribution,
  getStudentSemesterData2
} from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import { forceRefreshRoute, registerMessageRoutes } from '@/router'

// 年份和学期筛选过滤器
const deptYearFilter = ref('今年')
const semesterTimeFilter = ref('近半年')

// 注册必要的组件
echarts.use([
  PieChart,
  LineChart,
  BarChart,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  TitleComponent,
  ToolboxComponent,
  CanvasRenderer
])

// 用户信息
const userStore = useUserStore()
const currentDate = ref('')
const currentTime = ref('')
const avatar = computed(() => {
  const avatarUrl = userStore.avatar || defaultAvatar;
  return avatarUrl.startsWith('/src/') || avatarUrl.startsWith('/assets/') ? avatarUrl : formatImageUrl(avatarUrl);
})
const welcomeText = computed(() => {
  const hour = new Date().getHours()
  const username = userStore.name
  
  if (hour < 6) return `晚上好，${username}`
  if (hour < 12) return `早上好，${username}`
  if (hour < 14) return `中午好，${username}`
  if (hour < 18) return `下午好，${username}`
  return `晚上好，${username}`
})

// 定时器ID
let timerID = null

// 更新时间
const updateTime = () => {
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  currentDate.value = now.toLocaleDateString('zh-CN', options)
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
}

// 统计卡片数据
const statsCards = ref([
  { 
    title: '总学生数',
    value: '0',
    icon: 'User',
    color: '#409EFF',
    increase: true,
    compare: '0%'
  },
  { 
    title: '总课程数',
    value: '0',
    icon: 'Notebook',
    color: '#67C23A',
    increase: true,
    compare: '0%'
  },
  { 
    title: '教师数量',
    value: '0',
    icon: 'UserFilled',
    color: '#E6A23C',
    increase: false,
    compare: '0%'
  },
  { 
    title: '本周考勤率',
    value: '0.0%',
    icon: 'Calendar',
    color: '#F56C6C',
    increase: true,
    compare: '0%'
  }
])

// 图表引用
const pieChartRef = ref(null)
const lineChartRef = ref(null)
const studentPieChartRef = ref(null)
const studentBarChartRef = ref(null)
let pieChart = null
let lineChart = null
let studentPieChart = null
let studentBarChart = null

// 判断当前用户是否是学生
const isStudent = computed(() => {
  return userStore.roles && userStore.roles.includes('student')
})

// 通知类型图标
const noticeIcons = {
  'primary': 'Bell',
  'success': 'Document',
  'warning': 'Warning',
  'danger': 'CircleClose'
}

// 最近通知
const recentNotices = ref([])

// 待办事项
const todoList = ref([])

// 路由实例
const router = useRouter()

// 获取标签类型
const getTagType = (priority) => {
  switch(priority) {
    case '高': return 'danger'
    case '中': return 'warning'
    case '低': return 'info'
    default: return 'info'
  }
}

// 刷新统计数据
const refreshStatistics = async () => {
  try {
    const { data } = await getStatistics()
    if (data) {
      // 更新卡片数据
      statsCards.value[0].value = data.totalStudents.toString()
      statsCards.value[1].value = data.totalCourses.toString()
      statsCards.value[2].value = data.totalTeachers.toString()
      statsCards.value[3].value = data.weeklyAttendanceRate
      
      // 上周比例
      statsCards.value[0].compare = '+5%'  // 示例值
      statsCards.value[1].compare = '+2%'  // 示例值
      statsCards.value[2].compare = '-1%'  // 示例值
      statsCards.value[3].compare = '+3%'  // 示例值
      
      // 增减标记
      statsCards.value[0].increase = true
      statsCards.value[1].increase = true
      statsCards.value[2].increase = false
      statsCards.value[3].increase = true
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化饼图
const initPieChart = async () => {
  if (!pieChartRef.value) return
  
  if (!isStudent.value) {
  try {
      // 管理员和教师查看院系分布数据
      const { data } = await getDepartmentDistribution(deptYearFilter.value)
      if (!data) {
        ElMessage.warning('获取院系分布数据失败')
        return
      }
      
      if (!pieChart) {
        pieChart = echarts.init(pieChartRef.value)
      }
  
      const option = {
        title: {
          text: '院系学生分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: data.labels || []
        },
        series: [
          {
            name: '院系分布',
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
            data: data.labels && data.data 
              ? data.labels.map((label, index) => ({
                  value: data.data[index],
                  name: label
                }))
              : [{ value: 0, name: '暂无数据' }]
          }
        ]
      }
  
      pieChart.setOption(option)
      window.addEventListener('resize', () => pieChart.resize())
  } catch (error) {
    console.error('获取院系分布数据失败:', error)
  }
  } else {
  try {
      // 学生查看个人学习统计数据 - 使用学生专用API
      const { data } = await getStudentDepartmentDistribution()
      if (!data) {
        ElMessage.warning('获取个人学习统计数据失败')
        return
      }
      
      if (!pieChart) {
        pieChart = echarts.init(pieChartRef.value)
      }
      
      const option = {
        title: {
          text: '个人学习统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: data.labels || []
        },
        series: [
          {
            name: '课程数量',
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
            data: data.labels && data.seriesData 
              ? data.labels.map((label, index) => ({
                  value: data.seriesData[Object.keys(data.seriesData || {})[0] || 'data']?.[index] || 0,
                  name: label
                }))
              : [{ value: 0, name: '暂无数据' }]
          }
        ]
      }
      
      pieChart.setOption(option)
      window.addEventListener('resize', () => pieChart.resize())
  } catch (error) {
      console.error('获取个人学习统计数据失败:', error)
    }
  }
}

// 初始化折线图
const initLineChart = async () => {
  if (!lineChartRef.value) return
  
  if (!isStudent.value) {
  try {
      // 管理员和教师查看学期课程数据
      const { data } = await getStudentSemesterData(semesterTimeFilter.value)
      if (!data) {
        ElMessage.warning('获取学期数据失败')
        return
      }
      
      if (!lineChart) {
        lineChart = echarts.init(lineChartRef.value)
      }
  
      const option = {
        title: {
          text: '学期课程统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: data.seriesData ? Object.keys(data.seriesData) : ['暂无数据'],
          bottom: '0%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.labels || ['暂无数据']
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: `{value} ${data.unit || ''}`
          }
        },
        series: data.seriesData
          ? Object.keys(data.seriesData).map(key => ({
              name: key,
              type: 'line',
              data: data.seriesData[key]
            }))
          : [{ name: '暂无数据', type: 'line', data: [0] }]
      }
  
      lineChart.setOption(option)
      window.addEventListener('resize', () => lineChart.resize())
  } catch (error) {
    console.error('获取学期数据失败:', error)
    }
  } else {
    try {
      // 学生查看成绩分布数据 - 使用学生专用API
      const { data } = await getStudentGradeDistribution()
      if (!data) {
        ElMessage.warning('获取成绩分布数据失败')
        return
      }
      
      if (!lineChart) {
        lineChart = echarts.init(lineChartRef.value)
      }
      
      // 提取平均分数据
      let averageScore = null;
      if (data.seriesDataDouble && data.seriesDataDouble['平均分'] && data.seriesDataDouble['平均分'].length > 0) {
        averageScore = data.seriesDataDouble['平均分'][0];
      }
      
      const option = {
        title: {
          text: '成绩分布统计' + (averageScore ? ` (平均分: ${averageScore})` : ''),
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: '0%',
          left: 'center',
          data: data.labels || []
        },
        series: [
          {
            name: '成绩分布',
            type: 'pie',
            radius: '50%',
            data: data.labels && data.seriesData
              ? data.labels.map((label, index) => ({
                  value: data.seriesData[Object.keys(data.seriesData || {})[0] || 'data']?.[index] || 0,
                  name: label
                }))
              : [{ value: 0, name: '暂无数据' }],
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
      
      lineChart.setOption(option)
      window.addEventListener('resize', () => lineChart.resize())
  } catch (error) {
      console.error('获取成绩分布数据失败:', error)
    }
  }
}

// 更新饼图
const updateDeptChart = () => {
  initPieChart()
}

// 更新折线图
const updateSemesterChart = () => {
  initLineChart()
}

// 初始化数据
const initData = async () => {
  try {
    // 更新时间
    updateTime()
    
    // 获取统计数据
    await refreshStatistics()
    
    // 获取待办事项列表
    const todoRes = await getTodoList()
    todoList.value = todoRes.data || []
    
    // 获取通知列表
    const noticeRes = await getNoticeList()
    recentNotices.value = noticeRes.data || []
    
    // 初始化图表
    await initPieChart()
    await initLineChart()
  } catch (error) {
    console.error('初始化数据失败:', error)
  }
}

// 挂载时初始化
onMounted(() => {
  // 设置时间更新定时器
  updateTime()
  timerID = setInterval(updateTime, 1000)
  
  // 初始化数据
  initData()
  
  // 确保消息相关路由已注册
  registerMessageRoutes()
})

// 卸载前清理
onBeforeUnmount(() => {
  if (timerID) {
    clearInterval(timerID)
  }
  
  // 销毁图表实例
  if (pieChart) {
    pieChart.dispose()
  }
  
  if (lineChart) {
    lineChart.dispose()
  }
  
  // 移除窗口resize事件监听
  window.removeEventListener('resize', () => pieChart && pieChart.resize())
  window.removeEventListener('resize', () => lineChart && lineChart.resize())
})

// 查看更多通知
const viewMoreNotices = () => {
  router.push('/notice')
}

// 查看更多待办事项
const viewMoreTodos = () => {
  router.push('/todo')
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  
  // 欢迎区域
  .welcome-section {
    background: linear-gradient(to right, #1890ff, #722ed1);
    color: white;
    padding: 24px;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    .welcome-info {
      display: flex;
      align-items: center;
      
      .user-avatar {
        margin-right: 20px;
      }
      
      .welcome-text {
        h1 {
          margin: 0 0 8px 0;
          font-size: 24px;
          font-weight: 600;
        }
        
        p {
          margin: 0;
          font-size: 16px;
          opacity: 0.9;
        }
      }
    }
    
    .current-time {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-end;
      
      .date {
        font-size: 16px;
        margin-bottom: 8px;
      }
      
      .time {
        font-size: 24px;
        font-weight: 700;
      }
    }
  }
  
  // 统计卡片
  .stats-row {
    margin-bottom: 24px;
    
    .card-col {
      margin-bottom: 20px;
    }
    
    .stats-card {
      display: flex;
      height: 100%;
      
      :deep(.el-card__body) {
        display: flex;
        padding: 20px;
        width: 100%;
      }
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 16px;
        
        .el-icon {
          font-size: 30px;
          color: white;
        }
      }
      
      .stat-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        
        .stat-title {
          font-size: 16px;
          color: #606266;
          margin-bottom: 8px;
        }
        
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          margin-bottom: 8px;
        }
        
        .stat-trend {
          font-size: 14px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .trend-up {
            color: #67C23A;
          }
          
          .trend-down {
            color: #F56C6C;
          }
          
          .refresh-action {
            cursor: pointer;
            color: #409EFF;
          }
        }
      }
    }
  }
  
  // 图表卡片
  .chart-row {
    margin-bottom: 24px;
    
    .chart-card {
      margin-bottom: 20px;
      
      :deep(.el-card__header) {
        padding: 12px 20px;
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        span {
          font-size: 16px;
          font-weight: 500;
        }
        
        .filter-select {
          width: 100px;
        }
      }
      
      .chart {
        height: 300px;
      }
    }
  }
  
  // 通知和待办卡片
  .notice-row {
    .recent-card {
      margin-bottom: 20px;
      
      :deep(.el-card__header) {
        padding: 12px 20px;
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        span {
          font-size: 16px;
          font-weight: 500;
        }
      }
      
      // 通知列表
      .notice-list {
        .notice-item {
          display: flex;
          padding: 12px 0;
          border-bottom: 1px solid #ebeef5;
          
          &:last-child {
            border-bottom: none;
          }
          
          .notice-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-right: 12px;
            
            .el-icon {
              font-size: 18px;
              color: white;
            }
            
            &.primary {
              background-color: #409EFF;
            }
            
            &.success {
              background-color: #67C23A;
            }
            
            &.warning {
              background-color: #E6A23C;
            }
            
            &.danger {
              background-color: #F56C6C;
            }
          }
          
          .notice-content {
            flex: 1;
            
            .notice-title {
              font-size: 14px;
              color: #303133;
              margin-bottom: 4px;
              display: -webkit-box;
              -webkit-line-clamp: 1;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
            
            .notice-time {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
      
      // 待办列表
      .todo-list {
        .todo-item {
          display: flex;
          padding: 12px 0;
          border-bottom: 1px solid #ebeef5;
          
          &:last-child {
            border-bottom: none;
          }
          
          .todo-priority {
            margin-right: 12px;
            align-self: flex-start;
            margin-top: 3px;
          }
          
          .todo-content {
            flex: 1;
            
            .todo-title {
              font-size: 14px;
              color: #303133;
              margin-bottom: 4px;
              display: -webkit-box;
              -webkit-line-clamp: 1;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
            
            .todo-time {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .dashboard-container {
    .welcome-section {
      flex-direction: column;
      
      .welcome-info {
        margin-bottom: 16px;
      }
      
      .current-time {
        align-items: flex-start;
      }
    }
  }
}
</style> 