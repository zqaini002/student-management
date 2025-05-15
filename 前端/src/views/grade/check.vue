<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <div class="search-form-container">
        <el-form :model="queryParams" ref="searchForm" :inline="true" class="search-form">
          <div class="form-row">
            <el-form-item label="学期">
              <el-select v-model="queryParams.semester" placeholder="选择学期" clearable>
                <el-option
                  v-for="item in semesterOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="学号">
              <el-input
                v-model="queryParams.studentNo"
                placeholder="请输入学号"
                clearable
              />
            </el-form-item>
            <el-form-item label="学生姓名">
              <el-input
                v-model="queryParams.studentName"
                placeholder="请输入学生姓名"
                clearable
              />
            </el-form-item>
            <el-form-item label="课程名称">
              <el-input
                v-model="queryParams.courseName"
                placeholder="请输入课程名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option
                  v-for="dict in statusOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
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
          </div>
        </el-form>
      </div>
    </el-card>
    
    <!-- 功能按钮区 -->
    <el-row :gutter="10" class="mb-2 mt-2">
      <el-col :span="1.5">
        <el-button type="primary" @click="handleExport" v-if="checkPermission(['admin', 'teacher'])">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </el-col>
    </el-row>
    
    <!-- 表格区域 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="gradeList"
        stripe
        border
        style="width: 100%"
        highlight-current-row
      >
        <el-table-column type="index" width="55" align="center" label="序号" />
        <el-table-column prop="studentNo" label="学号" align="center" />
        <el-table-column prop="studentName" label="学生姓名" align="center" />
        <el-table-column prop="className" label="班级" align="center" />
        <el-table-column prop="courseCode" label="课程代码" align="center" />
        <el-table-column prop="courseName" label="课程名称" align="center" />
        <el-table-column prop="semester" label="学期" align="center" />
        <el-table-column prop="score" label="成绩" align="center">
          <template #default="scope">
            <span 
              :class="getScoreClass(scope.row.score)"
              style="font-weight: bold;">
              {{ scope.row.score !== null ? scope.row.score.toFixed(1) : '--' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="statusDesc" label="状态" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              size="small" 
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <el-pagination
        v-if="total > 0"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :currentPage="queryParams.pageNum"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="成绩详情"
      width="550px"
      append-to-body
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生姓名">{{ form.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ form.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ form.className }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ form.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程代码">{{ form.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ form.courseType }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ form.credits }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ form.semester }}</el-descriptions-item>
        <el-descriptions-item label="教师">{{ form.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="成绩">
          <span :class="getScoreClass(form.score)" style="font-weight: bold;">
            {{ form.score !== null ? form.score.toFixed(1) : '--' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="选课时间">{{ form.selectionTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(form.status)">{{ form.statusDesc }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Download, Refresh } from '@element-plus/icons-vue';
import { listSelections, exportSelections, getSelectionDetail } from '@/api/grade/check';
import { listSemesters } from '@/api/selection/manage';
import { checkPermission } from '@/utils/permission';

// 状态选项
const statusOptions = [
  { value: 0, label: '已选' },
  { value: 1, label: '已退' },
  { value: 2, label: '已修完' }
];

// 学期选项
const semesterOptions = ref([]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  studentNo: '',
  studentName: '',
  courseName: '',
  semester: '',
  status: undefined
});

// 表格数据和加载状态
const gradeList = ref([]);
const loading = ref(false);
const total = ref(0);

// 详情对话框
const dialogVisible = ref(false);
const form = ref({});

// 生命周期钩子
onMounted(() => {
  // 获取学期列表
  getSemesterOptions();
  // 加载数据
  getList();
});

// 获取学期选项
const getSemesterOptions = async () => {
  try {
    const res = await listSemesters();
    if (res.code === 200) {
      semesterOptions.value = res.data.map(item => ({
        value: item.value,
        label: item.label
      }));
    }
  } catch (error) {
    console.error('获取学期选项失败', error);
    ElMessage.error('获取学期选项失败');
  }
};

// 获取列表数据
const getList = async () => {
  loading.value = true;
  try {
    const res = await listSelections(queryParams);
    if (res.code === 200) {
      gradeList.value = res.data.list || [];
      total.value = res.data.total;
    } else {
      ElMessage.error(res.msg || '获取数据失败');
      gradeList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取列表数据失败', error);
    ElMessage.error('获取列表数据失败');
    gradeList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 根据状态获取标签类型
const getStatusType = (status) => {
  if (status === 0) return 'info';
  if (status === 1) return 'danger';
  if (status === 2) return 'success';
  return '';
};

// 根据成绩获取样式类
const getScoreClass = (score) => {
  if (score === null) return '';
  if (score < 60) return 'text-danger';
  if (score >= 90) return 'text-success';
  return '';
};

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置按钮
const handleReset = () => {
  queryParams.studentNo = '';
  queryParams.studentName = '';
  queryParams.courseName = '';
  queryParams.semester = '';
  queryParams.status = undefined;
  handleQuery();
};

// 每页条数改变
const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  getList();
};

// 页码改变
const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  getList();
};

// 查看详情
const handleDetail = async (row) => {
  try {
    const res = await getSelectionDetail(row.id);
    if (res.code === 200) {
      form.value = res.data;
      dialogVisible.value = true;
    } else {
      ElMessage.error(res.msg || '获取详情失败');
    }
  } catch (error) {
    console.error('获取详情失败', error);
    ElMessage.error('获取详情失败');
  }
};

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('是否确认导出所有成绩数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportSelections(queryParams).then(res => {
      // 处理文件下载
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
      const fileName = `成绩数据_${new Date().getTime()}.xlsx`;
      
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
</script>

<style scoped>
.search-form-container {
  width: 100%;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
}

:deep(.el-form-item) {
  margin-right: 10px;
  margin-bottom: 15px;
  flex-shrink: 0;
}

:deep(.el-select), :deep(.el-input) {
  width: 180px !important;
}

:deep(.el-form-item__label) {
  white-space: nowrap;
}

.search-card {
  margin-bottom: 20px;
  overflow: visible;
}

.mb-2 {
  margin-bottom: 20px;
}

.mt-2 {
  margin-top: 20px;
}

.text-danger {
  color: #F56C6C;
}

.text-success {
  color: #67C23A;
}
</style> 