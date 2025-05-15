<template>
  <div class="course-offering-container">
    <!-- 查询区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="教师名称" prop="teacherName">
          <el-input v-model="queryParams.teacherName" placeholder="请输入教师名称" clearable />
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

    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">课程开设记录</span>
          <div class="header-buttons">
            <el-button type="primary" @click="openDialog('add')">
              <el-icon><Plus /></el-icon>新增
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="list"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="courseName" label="课程名称" width="160" />
        <el-table-column prop="teacherName" label="教师名称" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="classTime" label="上课时间" width="120" />
        <el-table-column prop="location" label="上课地点" width="120" />
        <el-table-column prop="capacity" label="容量" width="80" align="center" />
        <el-table-column prop="selectedCount" label="已选人数" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? '正常' : '已结课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog('edit', scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程" prop="courseId">
              <el-select v-model="form.courseId" placeholder="请选择课程" filterable>
                <el-option v-for="item in courseList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教师" prop="teacherId">
              <el-select v-model="form.teacherId" placeholder="请选择教师" filterable>
                <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-input v-model="form.semester" placeholder="如 2023-2024-1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课时间" prop="classTime">
              <el-input v-model="form.classTime" placeholder="如 周一 1-2节" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课地点" prop="location">
              <el-input v-model="form.location" placeholder="如 教学楼A-101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity">
              <el-input-number v-model="form.capacity" :min="1" :max="200" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="正常" :value="0" />
                <el-option label="已结课" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-buttons">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseOfferingList, addCourseOffering, updateCourseOffering, deleteCourseOffering } from '@/api/courseOffering'
import { getAllCourses } from '@/api/course'
import { getAllTeachers } from '@/api/teacher'

const queryParams = reactive({
  courseName: '',
  teacherName: '',
  semester: '',
  pageNum: 1,
  pageSize: 10
})
const list = ref([])
const total = ref(0)
const loading = ref(false)

const courseList = ref([])
const teacherList = ref([])

const dialog = reactive({
  visible: false,
  title: '新增课程开设',
  type: 'add'
})
const form = reactive({
  id: null,
  courseId: null,
  teacherId: null,
  semester: '',
  classTime: '',
  location: '',
  capacity: 30,
  status: 0
})
const rules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
  classTime: [{ required: true, message: '请输入上课时间', trigger: 'blur' }],
  location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}
const formRef = ref(null)

function fetchList() {
  loading.value = true
  getCourseOfferingList(queryParams).then(res => {
    list.value = res.data.list
    total.value = res.data.total
  }).finally(() => loading.value = false)
}
function handleQuery() {
  queryParams.pageNum = 1
  fetchList()
}
function resetQuery() {
  queryParams.courseName = ''
  queryParams.teacherName = ''
  queryParams.semester = ''
  handleQuery()
}
function handleSizeChange(size) {
  queryParams.pageSize = size
  fetchList()
}
function handleCurrentChange(page) {
  queryParams.pageNum = page
  fetchList()
}
function openDialog(type, row) {
  dialog.type = type
  dialog.title = type === 'add' ? '新增课程开设' : '编辑课程开设'
  dialog.visible = true
  if (type === 'edit' && row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, courseId: null, teacherId: null, semester: '', classTime: '', location: '', capacity: 30, status: 0 })
  }
}
function submitForm() {
  console.log('提交表单数据:', form);
  formRef.value.validate(valid => {
    if (!valid) {
      console.error('表单验证失败');
      return;
    }
    
    // 确保表单数据类型正确
    const submitData = { 
      ...form,
      courseId: Number(form.courseId),
      teacherId: Number(form.teacherId),
      capacity: Number(form.capacity),
      status: Number(form.status)
    };
    
    console.log('处理后的提交数据:', submitData);
    
    if (dialog.type === 'add') {
      addCourseOffering(submitData).then(res => {
        console.log('新增课程开设成功:', res);
        ElMessage.success('新增成功');
        dialog.visible = false;
        fetchList();
      }).catch(err => {
        console.error('新增课程开设失败:', err);
        ElMessage.error(err.message || '新增失败，请检查表单数据');
      });
    } else {
      updateCourseOffering(submitData).then(res => {
        console.log('修改课程开设成功:', res);
        ElMessage.success('修改成功');
        dialog.visible = false;
        fetchList();
      }).catch(err => {
        console.error('修改课程开设失败:', err);
        ElMessage.error(err.message || '修改失败，请检查表单数据');
      });
    }
  });
}
function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该课程开设记录吗？', '提示', { type: 'warning' })
    .then(() => {
      deleteCourseOffering(row.id).then(() => {
        ElMessage.success('删除成功')
        fetchList()
      })
    })
}
function fetchCourseAndTeacher() {
  // 获取课程列表
  getAllCourses().then(res => { 
    // 确保返回的数据是正确的数组格式
    if (res.code === 200 && res.data && res.data.list) {
      courseList.value = res.data.list.map(item => ({
        id: item.id,
        name: item.name
      }));
    } else {
      courseList.value = [];
      console.error('获取课程列表失败:', res);
    }
  }).catch(err => {
    console.error('获取课程列表异常:', err);
    courseList.value = [];
  });
  
  // 获取教师列表
  getAllTeachers().then(res => {
    // 确保返回的数据是正确的数组格式
    if (res.code === 200 && res.data && res.data.list) {
      teacherList.value = res.data.list.map(item => ({
        id: item.id,
        name: item.name || (item.user ? item.user.name : '未知教师')
      }));
    } else {
      teacherList.value = [];
      console.error('获取教师列表失败:', res);
    }
  }).catch(err => {
    console.error('获取教师列表异常:', err);
    teacherList.value = [];
  });
}
onMounted(() => {
  fetchList()
  fetchCourseAndTeacher()
})
</script>

<style scoped>
.course-offering-container { padding: 20px; }
.search-card, .table-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-title { font-weight: bold; font-size: 16px; }
.header-buttons { display: flex; gap: 10px; }
.pagination-container { margin-top: 20px; text-align: right; }
.form-buttons { text-align: right; margin-top: 20px; }
</style> 