<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span><i class="el-icon-data-analysis"></i> 选课统计</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">总选课数</div>
            <div class="stat-value">{{ statistics.totalSelections || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">本学期选课数</div>
            <div class="stat-value">{{ statistics.currentSelections || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">已修完课程数</div>
            <div class="stat-value">{{ statistics.completedSelections || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">退课数</div>
            <div class="stat-value">{{ statistics.withdrawnSelections || 0 }}</div>
          </el-card>
        </el-col>
      </el-row>
      
      <div style="margin-top: 20px">
        <el-row>
          <el-col :span="12">
            <div ref="coursePieChartRef" style="width: 100%; height: 300px"></div>
          </el-col>
          <el-col :span="12">
            <div ref="semesterBarChartRef" style="width: 100%; height: 300px"></div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <el-card class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span><i class="el-icon-search"></i> 选课管理</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="queryParams.semester" placeholder="请选择学期" clearable style="width: 180px">
            <el-option
              v-for="semester in semesterOptions"
              :key="semester.value"
              :label="semester.label"
              :value="semester.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 操作工具栏 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleBatchWithdraw"
          >
            <i class="el-icon-delete"></i> 批量退课
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            @click="handleExport"
          >
            <i class="el-icon-download"></i> 导出
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            plain
            @click="handleGenerate"
          >
            <i class="el-icon-data-analysis"></i> 生成报表
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
      
      <!-- 表格展示 -->
      <el-table
        v-loading="loading"
        :data="selectionList"
        @selection-change="handleSelectionChange"
        border
        row-key="id"
        size="small"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="学号" align="center" prop="studentNo" min-width="100" :show-overflow-tooltip="true" />
        <el-table-column label="学生姓名" align="center" prop="studentName" min-width="90" />
        <el-table-column label="班级" align="center" prop="className" min-width="130" :show-overflow-tooltip="true" />
        <el-table-column label="课程编码" align="center" prop="courseCode" min-width="100" :show-overflow-tooltip="true" />
        <el-table-column label="课程名称" align="center" prop="courseName" min-width="130" :show-overflow-tooltip="true" />
        <el-table-column label="教师姓名" align="center" prop="teacherName" min-width="90" />
        <el-table-column label="学期" align="center" prop="semester" min-width="120" />
        <el-table-column label="选课时间" align="center" prop="selectionTime" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="成绩" align="center" min-width="100">
          <template #default="scope">
            <span v-if="scope.row.score !== null && scope.row.score !== undefined">
              {{ scope.row.score }}
              <el-tag size="small" :type="getScoreLevel(scope.row.score).type">
                {{ getScoreLevel(scope.row.score).label }}
              </el-tag>
            </span>
            <span v-else>未评分</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" min-width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" min-width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              @click="handleDetail(scope.row)"
            >详情</el-button>
            <el-button
              size="small"
              type="primary"
              link
              @click="handleEdit(scope.row)"
            >修改</el-button>
            <el-button
              v-if="scope.row.status !== 1 || isAdmin"
              size="small"
              type="primary"
              link
              @click="handleWithdraw(scope.row)"
            >退课</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total || 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handlePagination"
      />
    </el-card>
    
    <!-- 使用独立对话框组件 -->
    <selection-detail-dialog
      v-model:visible="open"
      :title="title"
      :data="form"
      :read-only="readOnly"
      :is-admin="isAdmin"
      @submit="submitForm"
      @withdraw="handleWithdrawInModal"
      @close="cancel"
    />
  </div>
</template>

<script>
import * as echarts from 'echarts'
import Pagination from '@/components/Pagination/index.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import SelectionDetailDialog from '@/components/SelectionDetailDialog/index.vue'
import { 
  listSelections, 
  updateSelection, 
  withdrawCourse, 
  exportSelections, 
  getSelectionStatistics,
  getCourseTypeStatistics,
  getSemesterStatistics,
  listSemesters,
  generateSelectionReport,
  batchWithdrawCourses,
  getSelection
} from '@/api/selection/manage'
import { useUserStore } from '@/stores/user'

export default {
  name: "SelectionManage",
  components: {
    Pagination,
    RightToolbar,
    SelectionDetailDialog
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 选课表格数据
      selectionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否只读模式
      readOnly: false,
      // 状态字典
      statusOptions: [
        { value: 0, label: "已选" },
        { value: 1, label: "已退" },
        { value: 2, label: "已修完" }
      ],
      // 学期选项
      semesterOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentNo: undefined,
        studentName: undefined,
        courseName: undefined,
        semester: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 统计数据
      statistics: {
        totalSelections: 0,
        currentSelections: 0,
        completedSelections: 0,
        withdrawnSelections: 0
      },
      // 图表实例
      coursePieChart: null,
      semesterBarChart: null
    };
  },
  computed: {
    isAdmin() {
      const userStore = useUserStore()
      return userStore.isAdmin
    }
  },
  created() {
    this.getList();
    this.getSemesters();
    this.getStatistics();
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts();
    });
  },
  methods: {
    /** 查询选课列表 */
    getList() {
      this.loading = true;
      listSelections(this.queryParams).then(response => {
        console.log('选课列表数据:', response);
        if (response.code === 200) {
          // 根据Result<PageResult<?>>格式处理数据
          if (response.data) {
            let records = response.data.list || response.data.records || [];
            
            // 打印原始数据，帮助诊断问题
            if (records.length > 0) {
              console.log('第一条记录详情:', records[0]);
            } else {
              console.warn('选课列表为空');
            }
            
            // 确保每条记录的关键字段都有值，避免显示undefined
            this.selectionList = records.map(item => {
              // 检查关键数据是否存在
              if (!item.courseCode || !item.courseName || !item.teacherName) {
                console.warn('记录存在空字段:', item);
              }
              
              return {
                ...item,
                courseName: item.courseName || '未知课程',
                courseCode: item.courseCode || '无编码',
                teacherName: item.teacherName || '未分配',
                semester: item.semester || '未指定学期',
                statusDesc: this.getStatusText(item.status)
              };
            });
          this.total = response.data.total || 0;
          } else {
            this.selectionList = [];
            this.total = 0;
            console.warn('返回数据格式不正确，无法找到列表数据');
          }
        } else {
          this.selectionList = [];
          this.total = 0;
          console.error('获取选课数据失败:', response.msg);
          this.$message.error(response.msg || '获取选课数据失败');
        }
        this.loading = false;
      }).catch(error => {
        console.error('获取选课列表异常:', error);
        this.selectionList = [];
        this.total = 0;
        this.loading = false;
        this.$message.error('获取选课数据失败: ' + (error.message || '未知错误'));
      });
    },
    /** 获取学期选项 */
    getSemesters() {
      listSemesters().then(response => {
        this.semesterOptions = response.data || [];
      }).catch(error => {
        console.error('获取学期列表失败:', error);
        this.semesterOptions = [];
      });
    },
    /** 获取统计数据 */
    getStatistics() {
      getSelectionStatistics().then(response => {
        if (response && response.data) {
          this.statistics = response.data;
        } else {
          console.error('获取统计数据失败，返回格式不正确:', response);
        }
      }).catch(error => {
        console.error('获取统计数据失败:', error);
      });
    },
    /** 初始化图表 */
    initCharts() {
      // 课程类型饼图
      if (this.$refs.coursePieChartRef) {
        this.coursePieChart = echarts.init(this.$refs.coursePieChartRef);
        
        getCourseTypeStatistics().then(response => {
          const data = response.data;
          this.coursePieChart.setOption({
            title: {
              text: '课程类型选课比例',
              left: 'center'
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              data: data.map(item => item.name)
            },
            series: [
              {
                name: '选课数量',
                type: 'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: '16',
                    fontWeight: 'bold'
                  }
                },
                labelLine: {
                  show: false
                },
                data: data
              }
            ]
          });
        });
      }
      
      // 学期选课柱状图
      if (this.$refs.semesterBarChartRef) {
        this.semesterBarChart = echarts.init(this.$refs.semesterBarChartRef);
        
        getSemesterStatistics().then(response => {
          const data = response.data;
          this.semesterBarChart.setOption({
            title: {
              text: '各学期选课统计',
              left: 'center'
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            xAxis: {
              type: 'category',
              data: data.map(item => item.semester)
            },
            yAxis: {
              type: 'value'
            },
            series: [{
              data: data.map(item => item.count),
              type: 'bar'
            }]
          });
        });
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 状态文字 */
    getStatusText(status) {
      const statusMap = {
        0: "已选",
        1: "已退",
        2: "已修完"
      };
      return statusMap[status] || "未知";
    },
    /** 状态类型 */
    getStatusType(status) {
      const statusMap = {
        0: "info",
        1: "danger",
        2: "success"
      };
      return statusMap[status] || "";
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 查看详情按钮操作 */
    handleDetail(row) {
      console.log('详情按钮点击，行数据:', row);
      // 深拷贝数据，避免引用传递
      this.form = JSON.parse(JSON.stringify(row));
      this.title = "查看选课详情";
      this.readOnly = true;
      this.open = true;
      
      // 记录日志，帮助调试
      console.log('详情模式打开，状态:', {
        open: this.open,
        readOnly: this.readOnly,
        title: this.title,
        form: this.form
      });
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      console.log('修改按钮点击，行数据:', row);
      // 深拷贝数据，避免引用传递
      this.form = JSON.parse(JSON.stringify(row));
      this.title = "修改选课信息";
      this.readOnly = false;
      this.open = true;
      
      // 记录日志，帮助调试
      console.log('修改模式打开，状态:', {
        open: this.open,
        readOnly: this.readOnly,
        title: this.title,
        form: this.form
      });
    },
    /** 退课按钮操作 */
    handleWithdraw(row) {
      if (row.status === 1) {
        this.$message.warning('该课程已退选，无需重复操作');
        return;
      }
      
      this.$confirm('确认要为学生 ' + row.studentName + ' 退课 ' + row.courseName + ' 吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.loading = true;
        withdrawCourse(row.id).then(response => {
          this.$message.success("退课成功");
          this.getList();
        }).catch(error => {
          this.$message.error("退课失败: " + (error.message || '未知错误'));
        }).finally(() => {
          this.loading = false;
        });
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$confirm('确认导出所有选课数据?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        exportSelections(this.queryParams).then(response => {
          this.download(response);
        });
      }).catch(() => {});
    },
    /** 生成报表按钮操作 */
    handleGenerate() {
      this.$confirm('确认生成选课报表?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        generateSelectionReport(this.queryParams).then(response => {
          this.$message.success("报表生成成功，请在报表管理中查看");
        });
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm(formData) {
      // 表单验证
      if (formData.score != null && (isNaN(formData.score) || formData.score < 0 || formData.score > 100)) {
        this.$message.error("成绩应在0-100之间");
        return;
      }
      
      console.log('准备提交的选课信息:', formData);
      
      // 构建提交数据，确保包含所有需要的字段
      const submitData = {
        id: formData.id,
        status: formData.status,
        score: formData.score,
        // 其他需要更新的字段
        studentId: formData.studentId,
        courseOfferingId: formData.courseOfferingId,
        selectionTime: formData.selectionTime
      };
      
      console.log('最终提交的完整数据:', submitData);
      
      this.$confirm('确认要更新选课信息吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.loading = true;
        updateSelection(submitData).then(response => {
          console.log('选课更新成功，返回数据:', response);
          this.$message.success("更新成功");
          this.open = false;
          this.getList(); // 刷新列表数据
        }).catch(error => {
          console.error('选课更新失败:', error);
          this.$message.error("更新失败: " + (error.message || '未知错误'));
        }).finally(() => {
          this.loading = false;
        });
      }).catch(() => {
        console.log('用户取消了提交操作');
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {};
    },
    /** 分页方法 */
    handlePagination(val) {
      console.log('分页事件触发:', val);
      this.queryParams.pageNum = val.page;
      this.queryParams.pageSize = val.limit;
      this.getList();
    },
    /** 根据分数获取等级 */
    getScoreLevel(score) {
      if (score === null || score === undefined) {
        return { type: 'info', label: '未评' };
      }
      
      const scoreNum = parseFloat(score);
      if (isNaN(scoreNum)) {
        return { type: 'info', label: '无效' };
      }
      
      if (scoreNum >= 90) {
        return { type: 'success', label: '优秀' };
      } else if (scoreNum >= 80) {
        return { type: 'primary', label: '良好' };
      } else if (scoreNum >= 70) {
        return { type: 'warning', label: '中等' };
      } else if (scoreNum >= 60) {
        return { type: '', label: '及格' };
      } else {
        return { type: 'danger', label: '不及格' };
      }
    },
    /** 退课按钮操作（在对话框中） */
    handleWithdrawInModal() {
      if (this.form.status === 1) {
        this.$message.warning('该课程已退选，无需重复操作');
        return;
      }
      
      this.$confirm('确认要为学生 ' + this.form.studentName + ' 退课 ' + this.form.courseName + ' 吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.loading = true;
        withdrawCourse(this.form.id).then(response => {
          this.$message.success("退课成功");
          this.open = false;
          this.getList();
        }).catch(error => {
          this.$message.error("退课失败: " + (error.message || '未知错误'));
        }).finally(() => {
          this.loading = false;
        });
      }).catch(() => {});
    },
    /** 批量退课按钮操作 */
    handleBatchWithdraw() {
      const selectionIds = this.ids;
      if (selectionIds.length === 0) {
        this.$message.warning("请至少选择一条记录");
        return;
      }
      
      this.$confirm('确认要为选中的 ' + selectionIds.length + ' 条选课记录执行退课操作吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.loading = true;
        batchWithdrawCourses(selectionIds).then(response => {
          this.$message.success("批量退课成功");
          this.getList();
        }).catch(error => {
          this.$message.error("批量退课失败: " + (error.message || '未知错误'));
        }).finally(() => {
          this.loading = false;
        });
      }).catch(() => {});
    },
    /** 对话框关闭前的处理 */
    handleDialogClose() {
      this.cancel();
    }
  },
  watch: {
    open(newValue) {
      console.log('对话框状态变更为:', newValue);
    }
  }
};
</script>

<style scoped>
.stat-card {
  text-align: center;
  color: #666;
}
.stat-title {
  font-size: 16px;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style> 
 