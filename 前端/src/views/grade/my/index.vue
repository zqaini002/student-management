<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的成绩</span>
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
        <el-form-item label="成绩状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择成绩状态" clearable>
            <el-option label="已公布" :value="1" />
            <el-option label="未公布" :value="0" />
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
            type="success"
            plain
            icon="Printer"
            @click="handlePrint"
          >打印成绩单</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 成绩统计 -->
      <el-row :gutter="20" class="mb20">
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">总课程数</div>
            <div class="statistic-number">{{ gradeStats.totalCourses }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">平均分</div>
            <div class="statistic-number">{{ gradeStats.averageScore }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">获得学分</div>
            <div class="statistic-number">{{ gradeStats.totalCredits }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="statistic-title">GPA</div>
            <div class="statistic-number">{{ gradeStats.gpa }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-table 
        v-loading="loading" 
        :data="gradeList"
        style="width: 100%">
        <el-table-column label="学期" align="center" prop="semester" width="140" />
        <el-table-column label="课程名称" align="center" prop="course_name" min-width="140" :show-overflow-tooltip="true" />
        <el-table-column label="课程编码" align="center" prop="course_code" width="120" />
        <el-table-column label="学分" align="center" width="80">
          <template #default="scope">
            {{ scope.row.credits || scope.row.credit || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="教师" align="center" prop="teacher_name" width="100" />
        <el-table-column label="总评成绩" align="center" prop="score" width="100">
          <template #default="scope">
            <template v-if="scope.row.score !== null && scope.row.score !== undefined">
              <span :class="getScoreClass(scope.row.score)">{{ scope.row.score }}</span>
            </template>
            <template v-else>
              <el-tag type="info" size="small">{{ scope.row.status_text || '待评分' }}</el-tag>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="绩点" align="center" width="80">
          <template #default="scope">
            <template v-if="scope.row.score !== null && scope.row.score !== undefined">
              {{ calculateGP(scope.row.score) }}
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column label="等级" align="center" width="100">
          <template #default="scope">
            <template v-if="scope.row.score !== null && scope.row.score !== undefined">
              <el-tag :type="getTagType(scope.row.score)" disable-transitions>{{ getGradeLevel(scope.row.score) }}</el-tag>
            </template>
            <template v-else>
              <el-tag type="info" disable-transitions>未评分</el-tag>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="考试时间" align="center" width="160">
          <template #default="scope">
            <template v-if="scope.row.examTime">
              {{ scope.row.examTime }}
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column label="排名" align="center" width="120">
          <template #default="scope">
            <template v-if="scope.row.score !== null && scope.row.score !== undefined && scope.row.ranking">
              {{ scope.row.ranking }}<span v-if="scope.row.totalStudents">/{{ scope.row.totalStudents }}</span>
              <el-progress 
                :percentage="calculateRankingPercentage(scope.row.ranking, scope.row.totalStudents || 100)" 
                :format="formatRanking"
                :color="calculateProgressColor(scope.row.ranking, scope.row.totalStudents || 100)"
                style="width: 80px; display: inline-block; margin-left: 10px;"></el-progress>
            </template>
            <template v-else>-</template>
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

    <!-- 成绩趋势图 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>成绩趋势分析</span>
        </div>
      </template>
      <div id="gradeChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <!-- 成绩分布图 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>成绩分布统计</span>
        </div>
      </template>
      <div id="distributionChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <!-- 打印成绩单对话框 -->
    <el-dialog title="成绩单预览" v-model="printDialogVisible" width="800px">
      <div id="printGradeReport">
        <div class="print-header">
          <h2>学生成绩单</h2>
          <div class="student-info">
            <p><span>学号：</span>{{ studentInfo.studentId }}</p>
            <p><span>姓名：</span>{{ studentInfo.studentName }}</p>
            <p><span>专业：</span>{{ studentInfo.majorName }}</p>
            <p><span>班级：</span>{{ studentInfo.className }}</p>
          </div>
        </div>
        <table class="print-table">
          <thead>
            <tr>
              <th>学期</th>
              <th>课程名称</th>
              <th>课程编码</th>
              <th>学分</th>
              <th>成绩</th>
              <th>绩点</th>
              <th>等级</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in printGradeList" :key="index">
              <td>{{ item.semester }}</td>
              <td>{{ item.course_name || item.courseName || item.name }}</td>
              <td>{{ item.course_code || item.courseCode || item.code }}</td>
              <td>{{ item.credits || item.credit }}</td>
              <td>{{ item.status === 1 ? item.score : '未公布' }}</td>
              <td>{{ item.status === 1 ? calculateGP(item.score) : '-' }}</td>
              <td>{{ item.status === 1 ? getGradeLevel(item.score) : '-' }}</td>
            </tr>
          </tbody>
        </table>
        <div class="summary-info">
          <p><span>总修学分：</span>{{ gradeStats.totalCredits }}</p>
          <p><span>课程数：</span>{{ gradeStats.totalCourses }}</p>
          <p><span>平均分：</span>{{ gradeStats.averageScore }}</p>
          <p><span>GPA：</span>{{ gradeStats.gpa }}</p>
          <p><span>打印时间：</span>{{ getCurrentTime() }}</p>
        </div>
        <div class="school-signature">
          <p>学校（盖章）</p>
          <p>{{ getCurrentDate() }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="printDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="doPrint">打印</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentGrades, getStudentGradeStats, getStudentInfo, getStudentCourseSelections } from '@/api/grade'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const userId = ref(userStore.userId)
const studentId = ref(userStore.userId) // 学生ID与用户ID一致

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const gradeList = ref([])
const printGradeList = ref([])
const printDialogVisible = ref(false)

// 学生信息
const studentInfo = ref({
  studentId: '',
  studentName: '',
  majorName: '',
  className: ''
})

// 成绩统计数据
const gradeStats = ref({
  totalCourses: 0,
  averageScore: 0,
  totalCredits: 0,
  gpa: 0
})

// 学期选项
const semesterOptions = ref([
  { value: '2024-2025-1', label: '2024-2025学年第一学期' },
  { value: '2024-2025-2', label: '2024-2025学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' },
  { value: '2023-2024-2', label: '2023-2024学年第二学期' }
])

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  courseName: undefined,
  semester: undefined,
  status: undefined
})

/** 查询成绩列表 */
function getList() {
  loading.value = true
  getStudentCourseSelections(studentId.value, queryParams.value).then(response => {
    console.log('成绩查询结果:', response)
    if (response && response.data) {
      // 处理分页数据
      let records = []
      if (response.data.records) {
        records = response.data.records || []
      } else if (response.data.rows) {
        records = response.data.rows || []
      } else if (response.data.list) {
        records = response.data.list || []
      } else if (Array.isArray(response.data)) {
        records = response.data
      }
      
      // 处理总数
      if (response.data.total !== undefined) {
        total.value = parseInt(response.data.total || 0)
      } else {
        total.value = records.length
      }
      
      // 标准化数据
      gradeList.value = records.map(item => {
        // 确保所有必要的字段都有值
        const normalizedItem = { ...item }
        
        // 处理课程名称
        if (!normalizedItem.course_name) {
          if (normalizedItem.courseName) {
            normalizedItem.course_name = normalizedItem.courseName
          } else if (normalizedItem.name) {
            normalizedItem.course_name = normalizedItem.name
          }
        }
        
        // 处理课程编码
        if (!normalizedItem.course_code) {
          if (normalizedItem.courseCode) {
            normalizedItem.course_code = normalizedItem.courseCode
          } else if (normalizedItem.code) {
            normalizedItem.course_code = normalizedItem.code
          }
        }
        
        // 处理学分
        if (!normalizedItem.credits && normalizedItem.credit) {
          normalizedItem.credits = normalizedItem.credit
        }
        
        // 处理教师名称
        if (!normalizedItem.teacher_name) {
          if (normalizedItem.teacherName) {
            normalizedItem.teacher_name = normalizedItem.teacherName
          } else if (normalizedItem.teacher) {
            normalizedItem.teacher_name = normalizedItem.teacher
          }
        }
        
        // 处理考试时间
        if (!normalizedItem.examTime && normalizedItem.exam_time) {
          normalizedItem.examTime = normalizedItem.exam_time;
        }
        
        // 处理排名数据
        if (!normalizedItem.ranking && normalizedItem.ranking !== 0 && normalizedItem.ranking !== null) {
          if (normalizedItem.rank) {
            normalizedItem.ranking = normalizedItem.rank;
          }
        }
        
        // 处理总学生数
        if (!normalizedItem.totalStudents && normalizedItem.total_students) {
          normalizedItem.totalStudents = normalizedItem.total_students;
        }
        
        // 处理状态文本
        if (!normalizedItem.status_text) {
          if (normalizedItem.statusText) {
            normalizedItem.status_text = normalizedItem.statusText
          } else if (normalizedItem.status === 0) {
            normalizedItem.status_text = '待评分'
          } else if (normalizedItem.status === 1) {
            normalizedItem.status_text = '已评分'
          } else if (normalizedItem.status === 2) {
            normalizedItem.status_text = '已取消'
          } else {
            normalizedItem.status_text = '未知'
          }
        }
        
        return normalizedItem
      })
      
      // 按学期和课程名排序
      gradeList.value.sort((a, b) => {
        // 首先按学期排序（降序）
        if (a.semester !== b.semester) {
          return b.semester?.localeCompare(a.semester || '') || 0
        }
        // 然后按课程名称排序
        return (a.course_name || '')?.localeCompare(b.course_name || '') || 0
      })
      
      // 如果有成绩数据并且长度>0，获取图表数据
      if (gradeList.value && gradeList.value.length > 0) {
        console.log('成绩列表数据:', gradeList.value)
        
        // 获取图表数据
        nextTick(() => {
          initCharts()
        })
      } else {
        console.log('没有成绩数据')
      }
    } else {
      // 数据为空时显示提示
      gradeList.value = []
      total.value = 0
    }
    loading.value = false
  }).catch(error => {
    console.error('获取成绩数据失败:', error)
    gradeList.value = []
    total.value = 0
    loading.value = false
  })

  // 获取成绩统计数据
  getStudentGradeStats(studentId.value).then(response => {
    console.log('成绩统计数据:', response)
    gradeStats.value = response.data || {}
    // 设置默认值，避免数据为空的情况
    if (!gradeStats.value.totalCourses) gradeStats.value.totalCourses = 0
    if (!gradeStats.value.averageScore) gradeStats.value.averageScore = 0
    if (!gradeStats.value.totalCredits) gradeStats.value.totalCredits = 0
    if (!gradeStats.value.gpa) gradeStats.value.gpa = 0
  }).catch(error => {
    console.error('获取成绩统计数据失败:', error)
    gradeStats.value = {
      totalCourses: 0,
      averageScore: 0,
      totalCredits: 0,
      gpa: 0
    }
  })
}

/** 获取学生信息 */
function getStudentDetail() {
  getStudentInfo(studentId.value).then(response => {
    studentInfo.value = response.data
  })
}

/** 成绩样式（区分不同分数段） */
function getScoreClass(score) {
  if (score === null || score === undefined) {
    return 'score-pending'
  } else if (score >= 90) {
    return 'score-excellent'
  } else if (score >= 80) {
    return 'score-good'
  } else if (score >= 70) {
    return 'score-normal'
  } else if (score >= 60) {
    return 'score-pass'
  } else {
    return 'score-fail'
  }
}

/** 计算绩点 */
function calculateGP(score) {
  if (score >= 90) return '4.0'
  if (score >= 85) return '3.7'
  if (score >= 80) return '3.3'
  if (score >= 75) return '3.0'
  if (score >= 70) return '2.7'
  if (score >= 65) return '2.3'
  if (score >= 60) return '2.0'
  return '0.0'
}

/** 获取成绩等级 */
function getGradeLevel(score) {
  if (score >= 90) return 'A'
  if (score >= 85) return 'A-'
  if (score >= 80) return 'B+'
  if (score >= 75) return 'B'
  if (score >= 70) return 'B-'
  if (score >= 65) return 'C+'
  if (score >= 60) return 'C'
  return 'F'
}

/** 计算排名百分比 */
function calculateRankingPercentage(ranking, total) {
  if (!ranking || !total || total === 0) return 0
  return 100 - (ranking / total * 100)
}

/** 格式化排名进度条 */
function formatRanking(percentage) {
  return ''  // 不显示百分比文字
}

/** 计算排名进度条颜色 */
function calculateProgressColor(ranking, total) {
  if (!ranking || !total || total === 0) return '#409eff'
  const percentage = 100 - (ranking / total * 100)
  if (percentage >= 90) return '#67c23a'  // 前10%显示绿色
  if (percentage >= 60) return '#409eff'  // 前40%显示蓝色
  if (percentage >= 30) return '#e6a23c'  // 前70%显示黄色
  return '#f56c6c'  // 后30%显示红色
}

/** 初始化成绩图表 */
function initCharts() {
  // 初始化成绩趋势图
  initGradeChart()
  
  // 初始化成绩分布图
  initDistributionChart()
}

/** 初始化成绩趋势图 */
function initGradeChart() {
  const chartDom = document.getElementById('gradeChart')
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom)
  
  // 处理图表数据
  const semesterList = []
  const scoreList = []
  const gpList = []
  
  // 检查是否有成绩数据
  if (!gradeList.value || gradeList.value.length === 0) {
    // 无数据时显示空图表
    const option = {
      title: {
        text: '暂无成绩数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 20
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: { show: false },
      yAxis: { show: false }
    }
    
    myChart.setOption(option)
    
    // 窗口大小变化时自动调整图表大小
    window.addEventListener('resize', () => {
      myChart.resize()
    })
    
    return
  }
  
  // 按学期排序
  const sortedData = [...gradeList.value].sort((a, b) => {
    if (!a.semester || !b.semester) return 0
    return a.semester.localeCompare(b.semester)
  })
  
  // 按学期分组计算平均分和GPA
  const semesterMap = {}
  
  sortedData.forEach(item => {
    if (!item.semester) return
    
    if (!semesterMap[item.semester]) {
      semesterMap[item.semester] = {
        scores: [],
        gps: [],
        credits: []
      }
    }
    
    if (item.score !== null && item.score !== undefined) {
      // 确保分数是数字类型
      const numericScore = parseFloat(item.score)
      semesterMap[item.semester].scores.push(numericScore)
      semesterMap[item.semester].gps.push(parseFloat(calculateGP(numericScore)))
      semesterMap[item.semester].credits.push(parseFloat(item.credits || 0))
    }
  })
  
  // 生成图表数据
  Object.keys(semesterMap).forEach(semester => {
    semesterList.push(semester)
    
    const scores = semesterMap[semester].scores
    const gps = semesterMap[semester].gps
    const credits = semesterMap[semester].credits
    
    // 检查是否有有效成绩
    if (scores.length === 0) {
      scoreList.push(0)
      gpList.push(0)
      return
    }
    
    // 计算加权平均分
    const totalWeightedScore = scores.reduce((sum, score, index) => sum + score * credits[index], 0)
    const totalCredits = credits.reduce((sum, credit) => sum + credit, 0)
    const weightedAvgScore = totalCredits > 0 ? (totalWeightedScore / totalCredits).toFixed(2) : 0
    
    // 计算平均GPA
    const totalWeightedGP = gps.reduce((sum, gp, index) => sum + gp * credits[index], 0)
    const weightedAvgGP = totalCredits > 0 ? (totalWeightedGP / totalCredits).toFixed(2) : 0
    
    scoreList.push(weightedAvgScore)
    gpList.push(weightedAvgGP)
  })
  
  // 如果没有学期数据，显示空图表
  if (semesterList.length === 0) {
    const option = {
      title: {
        text: '暂无成绩数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 20
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: { show: false },
      yAxis: { show: false }
    }
    
    myChart.setOption(option)
    
    // 窗口大小变化时自动调整图表大小
    window.addEventListener('resize', () => {
      myChart.resize()
    })
    
    return
  }
  
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['平均分', 'GPA']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: semesterList
    },
    yAxis: [
      {
        type: 'value',
        name: '分数',
        min: 0,
        max: 100,
        interval: 10,
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: 'GPA',
        min: 0,
        max: 4,
        interval: 0.5,
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '平均分',
        type: 'bar',
        data: scoreList,
        itemStyle: {
          color: '#3498db'
        }
      },
      {
        name: 'GPA',
        type: 'line',
        yAxisIndex: 1,
        data: gpList,
        symbolSize: 8,
        itemStyle: {
          color: '#e74c3c'
        }
      }
    ]
  }
  
  myChart.setOption(option)
  
  // 窗口大小变化时自动调整图表大小
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

/** 初始化成绩分布图 */
function initDistributionChart() {
  const chartDom = document.getElementById('distributionChart')
  if (!chartDom) return
  
  const myChart = echarts.init(chartDom)
  
  // 检查是否有成绩数据
  if (!gradeList.value || gradeList.value.length === 0) {
    // 无数据时显示空图表
    const option = {
      title: {
        text: '暂无成绩数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 20
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: { show: false },
      yAxis: { show: false }
    }
    
    myChart.setOption(option)
    
    // 窗口大小变化时自动调整图表大小
    window.addEventListener('resize', () => {
      myChart.resize()
    })
    
    return
  }
  
  // 统计各分数段的课程数量
  const scoreRanges = [
    { name: '90-100分', range: [90, 100], count: 0, color: '#67C23A' },
    { name: '80-89分', range: [80, 89], count: 0, color: '#409EFF' },
    { name: '70-79分', range: [70, 79], count: 0, color: '#E6A23C' },
    { name: '60-69分', range: [60, 69], count: 0, color: '#F56C6C' },
    { name: '0-59分', range: [0, 59], count: 0, color: '#909399' }
  ]
  
  // 统计有效成绩数量
  let validScoreCount = 0
  
  // 统计各成绩区间的数量
  gradeList.value.forEach(item => {
    if (item.score !== null && item.score !== undefined) {
      validScoreCount++
      // 确保分数是数字类型
      const score = parseFloat(item.score)
      console.log('处理成绩分布: 课程=', item.course_name, '分数=', score)
      for (const range of scoreRanges) {
        if (score >= range.range[0] && score <= range.range[1]) {
          range.count++
          console.log('  分配到区间:', range.name, '当前数量:', range.count)
          break
        }
      }
    }
  })
  
  // 打印最终统计结果
  console.log('成绩分布统计结果:', scoreRanges)

  // 如果没有有效成绩，显示空图表
  if (validScoreCount === 0) {
    const option = {
      title: {
        text: '暂无成绩数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999',
          fontSize: 20
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: { show: false },
      yAxis: { show: false }
    }
    
    myChart.setOption(option)
    
    // 窗口大小变化时自动调整图表大小
    window.addEventListener('resize', () => {
      myChart.resize()
    })
    
    return
  }
  
  // 饼图数据
  const pieData = scoreRanges.map(item => ({
    value: item.count,
    name: item.name,
    itemStyle: {
      color: item.color
    }
  }))
  
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '成绩分布',
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}门'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: pieData
      }
    ]
  }
  
  myChart.setOption(option)
  
  // 窗口大小变化时自动调整图表大小
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

/** 打印成绩单 */
function handlePrint() {
  // 获取所有成绩数据用于打印
  loading.value = true
  getStudentCourseSelections(studentId.value, { pageSize: 9999 }).then(response => {
    if (response && response.data) {
      // 处理成绩数据
      let records = []
      if (response.data.records) {
        records = response.data.records || []
      } else if (response.data.rows) {
        records = response.data.rows || []
      } else if (response.data.list) {
        records = response.data.list || []
      } else if (Array.isArray(response.data)) {
        records = response.data
      }
      
      // 标准化数据
      printGradeList.value = records.map(item => {
        const normalizedItem = { ...item }
        
        // 处理课程名称
        if (!normalizedItem.course_name) {
          if (normalizedItem.courseName) {
            normalizedItem.course_name = normalizedItem.courseName
          } else if (normalizedItem.name) {
            normalizedItem.course_name = normalizedItem.name
          }
        }
        
        // 处理课程编码
        if (!normalizedItem.course_code) {
          if (normalizedItem.courseCode) {
            normalizedItem.course_code = normalizedItem.courseCode
          } else if (normalizedItem.code) {
            normalizedItem.course_code = normalizedItem.code
          }
        }
        
        // 处理学分
        if (!normalizedItem.credits && normalizedItem.credit) {
          normalizedItem.credits = normalizedItem.credit
        }
        
        // 处理教师名称
        if (!normalizedItem.teacher_name) {
          if (normalizedItem.teacherName) {
            normalizedItem.teacher_name = normalizedItem.teacherName
          } else if (normalizedItem.teacher) {
            normalizedItem.teacher_name = normalizedItem.teacher
          }
        }
        
        // 处理考试时间
        if (!normalizedItem.examTime && normalizedItem.exam_time) {
          normalizedItem.examTime = normalizedItem.exam_time;
        }
        
        // 处理排名数据
        if (!normalizedItem.ranking && normalizedItem.ranking !== 0 && normalizedItem.ranking !== null) {
          if (normalizedItem.rank) {
            normalizedItem.ranking = normalizedItem.rank;
          }
        }
        
        // 处理总学生数
        if (!normalizedItem.totalStudents && normalizedItem.total_students) {
          normalizedItem.totalStudents = normalizedItem.total_students;
        }
        
        // 处理状态文本
        if (!normalizedItem.status_text) {
          if (normalizedItem.statusText) {
            normalizedItem.status_text = normalizedItem.statusText
          } else if (normalizedItem.status === 0) {
            normalizedItem.status_text = '待评分'
          } else if (normalizedItem.status === 1) {
            normalizedItem.status_text = '已评分'
          } else if (normalizedItem.status === 2) {
            normalizedItem.status_text = '已取消'
          } else {
            normalizedItem.status_text = '未知'
          }
        }
        
        return normalizedItem
      })
      
    printDialogVisible.value = true
    } else {
      ElMessage.warning('没有可打印的成绩数据')
    }
    loading.value = false
  }).catch(error => {
    console.error('获取打印成绩数据失败:', error)
    loading.value = false
    ElMessage.error('获取成绩数据失败')
  })
}

/** 执行打印 */
function doPrint() {
  const printContent = document.getElementById('printGradeReport').innerHTML
  const originalContent = document.body.innerHTML
  
  document.body.innerHTML = printContent
  
  window.print()
  
  document.body.innerHTML = originalContent
  
  // 重新注册Vue事件
  printDialogVisible.value = false
  
  setTimeout(() => {
    location.reload()
  }, 100)
}

/** 获取当前日期时间 */
function getCurrentTime() {
  const now = new Date()
  return now.toLocaleString()
}

/** 获取当前日期 */
function getCurrentDate() {
  const now = new Date()
  return now.toLocaleDateString()
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
    semester: undefined,
    status: undefined
  }
  handleQuery()
}

/** 等级样式 */
function getTagType(score) {
  if (score === null || score === undefined) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 70) return ''
  if (score >= 60) return 'warning'
  return 'danger'
}

onMounted(() => {
  getList()
  getStudentDetail()
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

/* 成绩样式 */
.score-excellent {
  color: #67C23A;
  font-weight: bold;
}

.score-good {
  color: #409EFF;
  font-weight: bold;
}

.score-normal {
  color: #E6A23C;
}

.score-pass {
  color: #F56C6C;
}

.score-fail {
  color: #F56C6C;
  font-weight: bold;
}

/* 学期分组样式 */
:deep(.el-table__row) {
  transition: background-color 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 让相同学期的行颜色略有区别，以示分组 */
:deep(.el-table__row:nth-child(odd)) {
  background-color: #fafafa;
}

:deep(.el-table__row:nth-child(even)) {
  background-color: #ffffff;
}

/* 学期列加粗显示 */
:deep(.el-table__cell:first-child .cell) {
  font-weight: bold;
  color: #303133;
}

/* 打印样式 */
#printGradeReport {
  padding: 20px;
  font-family: SimSun, serif;
}

.print-header {
  text-align: center;
  margin-bottom: 20px;
}

.print-header h2 {
  font-size: 24px;
  margin-bottom: 20px;
}

.student-info {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 20px;
}

.student-info p {
  width: 45%;
  margin: 5px 0;
}

.student-info p span {
  font-weight: bold;
}

.print-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.print-table th, .print-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.print-table th {
  background-color: #f2f2f2;
}

.summary-info {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
}

.summary-info p {
  margin-right: 30px;
  margin-bottom: 10px;
}

.summary-info p span {
  font-weight: bold;
}

.school-signature {
  margin-top: 50px;
  text-align: right;
}

@media print {
  body {
    margin: 0;
    padding: 0;
  }
  
  #printGradeReport {
    width: 100%;
    height: auto;
  }
}

.course-detail {
  margin-bottom: 20px;
}
.semester-header {
  background-color: #f5f7fa;
  font-weight: bold;
}
.score-excellent {
  color: #67C23A;
  font-weight: bold;
}
.score-good {
  color: #409EFF;
  font-weight: bold;
}
.score-normal {
  color: #909399;
  font-weight: bold;
}
.score-pass {
  color: #E6A23C;
  font-weight: bold;
}
.score-fail {
  color: #F56C6C;
  font-weight: bold;
}
.score-pending {
  color: #909399;
  font-style: italic;
}
</style> 