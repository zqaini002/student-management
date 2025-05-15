<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryParams" ref="searchForm" :inline="true">
        <el-form-item label="学期" prop="semester">
          <el-select v-model="queryParams.semester" placeholder="选择学期" clearable>
            <el-option
              v-for="item in semesterOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="queryParams.courseId" placeholder="选择课程" clearable filterable>
            <el-option
              v-for="item in courseOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="queryParams.classId" placeholder="选择班级" clearable filterable>
            <el-option
              v-for="item in classOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 统计概览 -->
    <el-row :gutter="20" class="mt-2">
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>总人数</span>
            </div>
          </template>
          <div class="card-body">
            <div class="statistic-value">{{ statsData.totalStudents || 0 }}</div>
            <div class="statistic-label">参与课程学生总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>平均分</span>
            </div>
          </template>
          <div class="card-body">
            <div class="statistic-value">{{ statsData.averageScore ? statsData.averageScore.toFixed(2) : '0.00' }}</div>
            <div class="statistic-label">课程平均成绩</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>及格率</span>
            </div>
          </template>
          <div class="card-body">
            <div class="statistic-value">{{ statsData.passRate ? (statsData.passRate * 100).toFixed(2) + '%' : '0.00%' }}</div>
            <div class="statistic-label">60分以上占比</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>优秀率</span>
            </div>
          </template>
          <div class="card-body">
            <div class="statistic-value">{{ statsData.excellentRate ? (statsData.excellentRate * 100).toFixed(2) + '%' : '0.00%' }}</div>
            <div class="statistic-label">90分以上占比</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="mt-2">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>成绩分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div id="scoreDistributionChart" style="width: 100%; height: 400px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>班级平均分对比</span>
            </div>
          </template>
          <div class="chart-container">
            <div id="classAverageChart" style="width: 100%; height: 400px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-2">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>班级成绩分析</span>
            </div>
          </template>
          <el-table
            v-loading="loading"
            :data="classStatsList"
            stripe
            border
            style="width: 100%"
          >
            <el-table-column prop="className" label="班级" align="center" />
            <el-table-column prop="totalStudents" label="学生人数" align="center" />
            <el-table-column prop="averageScore" label="平均分" align="center">
              <template #default="scope">
                {{ scope.row.averageScore !== null ? scope.row.averageScore.toFixed(2) : '--' }}
              </template>
            </el-table-column>
            <el-table-column prop="maxScore" label="最高分" align="center">
              <template #default="scope">
                {{ scope.row.maxScore !== null ? scope.row.maxScore.toFixed(1) : '--' }}
              </template>
            </el-table-column>
            <el-table-column prop="minScore" label="最低分" align="center">
              <template #default="scope">
                {{ scope.row.minScore !== null ? scope.row.minScore.toFixed(1) : '--' }}
              </template>
            </el-table-column>
            <el-table-column label="及格率" align="center">
              <template #default="scope">
                {{ calculatePassRate(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="优秀率" align="center">
              <template #default="scope">
                {{ calculateExcellentRate(scope.row) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 导出报告按钮 -->
    <el-row :gutter="20" class="mt-2">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>生成报告</span>
            </div>
          </template>
          <div class="report-actions">
            <el-button type="primary" @click="handleExportReport">
              <el-icon><Download /></el-icon>导出成绩统计报告
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Download, Refresh } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import { listSemesters } from '@/api/selection/manage';
import { getCourseOptions, getClassOptions, getGradeStats, exportGradeReport } from '@/api/grade/stats';

// 学期选项
const semesterOptions = ref([]);
const courseOptions = ref([]);
const classOptions = ref([]);

// 图表实例
let scoreDistributionChart = null;
let classAverageChart = null;

// 查询参数
const queryParams = reactive({
  semester: '',
  courseId: undefined,
  classId: undefined
});

// 数据和加载状态
const loading = ref(false);
const statsData = ref({});
const classStatsList = ref([]);

// 生命周期钩子
onMounted(() => {
  // 获取下拉选项数据
  getSemesterOptions();
  getCourseOptionsList();
  getClassOptionsList();
  // 初始化图表
  initCharts();
  // 加载数据
  getStats();
});

onUnmounted(() => {
  // 销毁图表实例，避免内存泄漏
  if (scoreDistributionChart) {
    scoreDistributionChart.dispose();
    scoreDistributionChart = null;
  }
  if (classAverageChart) {
    classAverageChart.dispose();
    classAverageChart = null;
  }
});

// 获取学期选项
const getSemesterOptions = async () => {
  try {
    const res = await listSemesters();
    if (res.code === 200) {
      // 添加"全部学期"选项
      const allOption = { value: "all", label: "全部学期" };
      semesterOptions.value = [allOption, ...res.data];
      
      // 默认选择"全部学期"
      queryParams.semester = "all";
    }
  } catch (error) {
    console.error('获取学期选项失败', error);
    ElMessage.error('获取学期选项失败');
  }
};

// 获取课程选项
const getCourseOptionsList = async () => {
  try {
    const res = await getCourseOptions();
    if (res.code === 200) {
      courseOptions.value = res.data;
    }
  } catch (error) {
    console.error('获取课程选项失败', error);
    ElMessage.error('获取课程选项失败');
  }
};

// 获取班级选项
const getClassOptionsList = async () => {
  try {
    const res = await getClassOptions();
    if (res.code === 200) {
      classOptions.value = res.data;
    }
  } catch (error) {
    console.error('获取班级选项失败', error);
    ElMessage.error('获取班级选项失败');
  }
};

// 初始化图表
const initCharts = () => {
  // 初始化成绩分布图
  scoreDistributionChart = echarts.init(document.getElementById('scoreDistributionChart'));
  
  // 初始化班级平均分图
  classAverageChart = echarts.init(document.getElementById('classAverageChart'));
  
  // 适应窗口变化
  window.addEventListener('resize', () => {
    scoreDistributionChart && scoreDistributionChart.resize();
    classAverageChart && classAverageChart.resize();
  });
};

// 获取统计数据
const getStats = async () => {
  loading.value = true;
  try {
    const res = await getGradeStats(queryParams);
    if (res.code === 200) {
      statsData.value = res.data.overview || {};
      
      // 计算及格率和优秀率
      if (statsData.value.totalWithScore && statsData.value.totalWithScore > 0) {
        statsData.value.passRate = statsData.value.passCount / statsData.value.totalWithScore;
        statsData.value.excellentRate = statsData.value.excellentCount / statsData.value.totalWithScore;
      } else {
        statsData.value.passRate = 0;
        statsData.value.excellentRate = 0;
      }
      
      // 处理班级统计数据
      const classStats = res.data.classStats || [];
      classStatsList.value = classStats.map(item => {
        // 计算班级及格率和优秀率
        if (item.totalStudents && item.totalStudents > 0) {
          item.passRate = item.passCount / item.totalStudents;
          item.excellentRate = item.excellentCount / item.totalStudents;
        } else {
          item.passRate = 0;
          item.excellentRate = 0;
        }
        return item;
      });
      
      // 更新图表
      updateScoreDistributionChart(res.data.scoreDistribution || []);
      updateClassAverageChart(res.data.classStats || []);
    } else {
      ElMessage.error(res.msg || '获取统计数据失败');
    }
  } catch (error) {
    console.error('获取统计数据失败', error);
    ElMessage.error('获取统计数据失败');
  } finally {
    loading.value = false;
  }
};

// 更新成绩分布图
const updateScoreDistributionChart = (data) => {
  // 定义分数区间及对应颜色
  const scoreRanges = [
    { range: '0-59', label: '<60', color: '#F56C6C' },
    { range: '60-69', label: '60-69', color: '#E6A23C' },
    { range: '70-79', label: '70-79', color: '#409EFF' },
    { range: '80-89', label: '80-89', color: '#67C23A' },
    { range: '90-100', label: '90-100', color: '#31C27C' }
  ];
  
  // 将后端数据转换为图表所需格式
  const chartData = scoreRanges.map(item => {
    // 查找此区间的数据
    const rangeData = data.find(d => d.scoreRange === item.range);
    return {
      value: rangeData ? rangeData.count : 0,
      itemStyle: { color: item.color }
    };
  });
  
  const option = {
    title: {
      text: '成绩分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: scoreRanges.map(item => item.label),
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '学生数'
    },
    series: [
      {
        name: '成绩分布',
        type: 'bar',
        barWidth: '60%',
        data: chartData
      }
    ]
  };
  
  scoreDistributionChart.setOption(option);
};

// 更新班级平均分图
const updateClassAverageChart = (data) => {
  const classNames = data.map(item => item.className);
  const averageScores = data.map(item => item.averageScore);
  
  const option = {
    title: {
      text: '班级平均分对比',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: classNames,
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '平均分',
      min: function(value) {
        return Math.floor(Math.max(0, value.min - 5));
      }
    },
    series: [
      {
        name: '平均分',
        type: 'bar',
        barWidth: '60%',
        data: averageScores,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }
    ]
  };
  
  classAverageChart.setOption(option);
};

// 查询按钮
const handleQuery = () => {
  getStats();
};

// 重置按钮
const handleReset = () => {
  // 重置筛选条件
  queryParams.courseId = undefined;
  queryParams.classId = undefined;
  // 设置为"全部学期"
  queryParams.semester = "all";
  
  // 重新查询
  getStats();
};

// 导出报告
const handleExportReport = () => {
  ElMessageBox.confirm('是否确认导出成绩统计报告?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportGradeReport(queryParams).then(res => {
      // 处理文件下载
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
      const fileName = `成绩统计报告_${new Date().getTime()}.xlsx`;
      
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
      URL.revokeObjectURL(link.href);
      
      ElMessage.success('导出成功');
    }).catch(err => {
      console.error('导出失败', err);
      ElMessage.error('导出失败');
    });
  });
};

// 计算及格率
const calculatePassRate = (row) => {
  if (row.passRate !== null) {
    return (row.passRate * 100).toFixed(2) + '%';
  }
  return '--';
};

// 计算优秀率
const calculateExcellentRate = (row) => {
  if (row.excellentRate !== null) {
    return (row.excellentRate * 100).toFixed(2) + '%';
  }
  return '--';
};
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}
.mt-2 {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-body {
  text-align: center;
  padding: 20px 0;
}
.statistic-value {
  font-size: 30px;
  font-weight: bold;
  color: #409EFF;
}
.statistic-label {
  margin-top: 10px;
  color: #606266;
}
.chart-container {
  position: relative;
  width: 100%;
  height: 400px;
}
.report-actions {
  text-align: center;
  padding: 10px 0;
}
:deep(.el-select) {
  width: 240px !important;
}
:deep(.el-form-item) {
  margin-right: 10px;
  margin-bottom: 15px;
}
</style> 