<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">成绩录入</span>
          <el-button @click="goBack" link v-if="fromCourseTeaching">
            <el-icon><Back /></el-icon>返回
          </el-button>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
        <el-form-item label="课程" prop="courseId" required>
          <el-select
            v-model="queryParams.courseId"
            placeholder="请选择课程"
            clearable
            style="width: 240px"
            :disabled="fromCourseTeaching"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="学期" prop="semester" required>
          <el-select
            v-model="queryParams.semester"
            placeholder="请选择学期"
            clearable
            style="width: 180px"
            :disabled="fromCourseTeaching"
          >
            <el-option
              v-for="item in semesterOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="班级" prop="classId">
          <el-select
            v-model="queryParams.classId"
            placeholder="请选择班级"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="item in classOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="学生姓名" prop="studentName">
          <el-input
            v-model="queryParams.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
          />
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
      
      <!-- 操作按钮 -->
      <div class="mb-2 operation-container">
        <el-button
          type="success"
          :disabled="!tableData.length || !selectedRows.length"
          @click="handleSaveGrades"
        >
          <el-icon><Check /></el-icon>保存成绩
        </el-button>
        <el-button
          type="info"
          :disabled="!tableData.length || !selectedRows.length"
          @click="handleResetScores"
        >
          <el-icon><Close /></el-icon>重置选中成绩
        </el-button>
        <el-button
          type="primary"
          :disabled="!canImport"
          @click="handleImportGrades"
        >
          <el-icon><Upload /></el-icon>导入成绩
        </el-button>
        <el-button
          type="primary"
          :disabled="!tableData.length"
          @click="handleExportGrades"
        >
          <el-icon><Download /></el-icon>导出成绩
        </el-button>
        <el-button
          type="primary"
          @click="handleDownloadTemplate"
        >
          <el-icon><Document /></el-icon>下载导入模板
        </el-button>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column
          prop="studentNo"
          label="学号"
          width="120"
          align="center"
        />
        <el-table-column
          prop="studentName"
          label="姓名"
          width="100"
          align="center"
        />
        <el-table-column
          prop="gender"
          label="性别"
          width="60"
          align="center"
        >
          <template #default="scope">
            {{ scope.row.gender === 0 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="className"
          label="班级"
          width="180"
          align="center"
        />
        <el-table-column
          prop="majorName"
          label="专业"
          width="180"
          align="center"
        />
        <el-table-column
          prop="courseName"
          label="课程"
          min-width="180"
          align="center"
        />
        <el-table-column
          prop="courseTypeDesc"
          label="课程类型"
          width="100"
          align="center"
        />
        <el-table-column
          prop="score"
          label="成绩"
          width="120"
          align="center"
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row.score"
              :min="0"
              :max="100"
              :precision="1"
              :step="0.5"
              size="small"
              placeholder="输入成绩"
              @change="(val) => handleScoreChange(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="grade"
          label="等级"
          width="80"
          align="center"
        >
          <template #default="scope">
            <el-tag v-if="scope.row.grade" :type="getGradeTagType(scope.row.grade)">
              {{ scope.row.grade }}
            </el-tag>
            <span v-else class="no-grade">-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="isPassedDesc"
          label="是否通过"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag
              :type="scope.row.isPassed === 1 ? 'success' : (scope.row.isPassed === 0 ? 'danger' : 'info')"
            >
              {{ scope.row.isPassedDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="120"
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="handleEditScore(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              link 
              @click="handleClearScore(scope.row)"
            >
              清空
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-if="total > 0"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 成绩导入对话框 -->
    <el-dialog v-model="importDialog.visible" title="导入成绩" width="500px" append-to-body>
      <div class="import-container">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :file-list="importDialog.fileList"
          :on-change="handleFileChange"
          :limit="1"
          accept=".xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">拖拽文件到此处或 <em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传Excel文件(.xlsx, .xls)，且文件大小不超过2MB
            </div>
          </template>
        </el-upload>

        <div class="import-tips">
          <h4>导入说明：</h4>
          <ol>
            <li>请先下载成绩导入模板</li>
            <li>按照模板格式填写学生成绩</li>
            <li>成绩取值范围：0-100分</li>
            <li>Excel表格中的学号必须与系统中的学生学号一致</li>
            <li>导入成功后，请检查成绩是否正确</li>
          </ol>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialog.visible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitImport" 
            :loading="importDialog.loading"
            :disabled="!importDialog.fileList.length"
          >
            确定导入
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 成绩编辑对话框 -->
    <el-dialog v-model="editDialog.visible" title="编辑成绩" width="500px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="学生" prop="studentName">
          <el-input v-model="editForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="editForm.studentNo" disabled />
        </el-form-item>
        <el-form-item label="课程" prop="courseName">
          <el-input v-model="editForm.courseName" disabled />
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number 
            v-model="editForm.score" 
            :min="0" 
            :max="100" 
            :precision="1" 
            :step="0.5" 
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input 
            v-model="editForm.comment" 
            type="textarea" 
            rows="3" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialog.visible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitEdit" 
            :loading="editDialog.loading"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close, Upload, Download, Document, Back } from '@element-plus/icons-vue'
import { getCourseOptions, getClassOptions, getSemesterList, getStudentGradesList, batchSubmitGrades } from '@/api/grade'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()
const route = useRoute()
const fromCourseTeaching = computed(() => {
  return route.query.courseId && route.query.courseOfferingId
})

// 用户信息
const userStore = useUserStore()
const teacherId = ref(null)

// 查询参数
const queryParams = reactive({
  courseId: undefined,
  courseOfferingId: undefined,
  semester: undefined,
  classId: undefined,
  studentName: undefined,
  pageNum: 1,
  pageSize: 10
})

// 状态变量
const loading = ref(false)
const total = ref(0)
const courseOptions = ref([])
const classOptions = ref([])
const semesterOptions = ref([])
const tableData = ref([])
const queryForm = ref(null)
const modifiedRows = ref({}) // 记录修改过的行
const selectedRows = ref([]) // 记录选中的行

// 导入对话框
const importDialog = reactive({
  visible: false,
  fileList: [],
  loading: false
})

// 编辑对话框
const editDialog = reactive({
  visible: false,
  loading: false
})
const editForm = reactive({
  id: null,
  studentId: null,
  studentName: '',
  studentNo: '',
  courseName: '',
  courseId: null,
  score: null,
  comment: ''
})
const editRules = {
  score: [
    { required: true, message: '请输入成绩', trigger: 'blur' }
  ]
}
const editFormRef = ref(null)

// 计算是否可以提交表单
const canQuery = computed(() => {
  return queryParams.courseId && queryParams.semester
})

// 计算是否可以导入成绩
const canImport = computed(() => {
  return queryParams.courseId && tableData.value.length > 0
})

// 初始化页面
onMounted(async () => {
  await fetchTeacherId()
  await initOptions()
  
  // 如果是从课程教学页面跳转来的，自动获取参数并查询
  if (fromCourseTeaching.value) {
    queryParams.courseId = Number(route.query.courseId)
    queryParams.courseOfferingId = Number(route.query.courseOfferingId)
    
    // 获取课程的学期信息，然后查询数据
    const tempCourse = courseOptions.value.find(item => item.value === queryParams.courseId)
    if (tempCourse && tempCourse.semester) {
      queryParams.semester = tempCourse.semester
      handleQuery()
    }
  }
})

// 获取教师ID
async function fetchTeacherId() {
  try {
    // 使用userStore中的userInfo属性，而不是调用getUserInfo方法
    const userInfo = userStore.userInfo
    
    // 判断用户类型和角色
    const isAdmin = userInfo && userInfo.roles && userInfo.roles.includes('admin')
    const isTeacher = userInfo && userInfo.roles && userInfo.roles.includes('teacher')
    
    if (userInfo && userInfo.teacherId) {
      // 教师用户
      teacherId.value = userInfo.teacherId
    } else if (isTeacher && userInfo.userId) {
      // 教师用户但没有teacherId字段，使用userId作为替代
      console.log('教师用户但缺少teacherId，使用userId替代:', userInfo.userId)
      teacherId.value = userInfo.userId
    } else if (isAdmin) {
      // 管理员用户 - 不需要特定的teacherId
      console.log('管理员用户访问成绩录入页面')
      teacherId.value = null
    } else {
      console.warn('未知用户类型或缺少必要信息:', userInfo)
      ElMessage.warning('获取教师信息失败，请重新登录')
    }
  } catch (error) {
    console.error('获取教师ID失败:', error)
    ElMessage.error('获取教师信息失败')
  }
}

// 初始化下拉选项
const initOptions = async () => {
  try {
    // 获取课程选项
    const courseRes = await getCourseOptions()
    if (courseRes.data && Array.isArray(courseRes.data)) {
      // 确保value是数字类型
      courseOptions.value = courseRes.data.map(item => ({
        label: item.label,
        value: Number(item.value),
        semester: item.semester
      }))
      console.log('处理后的课程选项:', courseOptions.value)
    } else {
      courseOptions.value = []
      console.warn('课程选项API返回格式异常:', courseRes)
    }
    
    // 获取班级选项
    const classRes = await getClassOptions()
    if (classRes.data && Array.isArray(classRes.data)) {
      classOptions.value = classRes.data.map(item => ({
        label: item.label,
        value: Number(item.value)
      }))
    } else {
      classOptions.value = []
    }
    
    // 获取学期选项
    const semesterRes = await getSemesterList()
    if (semesterRes.data && Array.isArray(semesterRes.data)) {
      semesterOptions.value = semesterRes.data
    } else {
      semesterOptions.value = ['2023-2024-1', '2023-2024-2', '2024-2025-1']
    }
  } catch (error) {
    console.error('初始化选项时出错:', error)
    ElMessage.error('获取下拉选项失败，请刷新页面重试')
  }
}

// 查询学生成绩列表
const handleQuery = async () => {
  if (!canQuery.value) {
    ElMessage.warning('请选择课程和学期')
    return
  }
  
  loading.value = true
  try {
    // 构建查询参数
    const params = { ...queryParams }
    
    // 只有当teacherId有值时才添加到参数中
    if (teacherId.value !== null) {
      params.teacherId = teacherId.value
    }
    
    console.log('查询成绩列表参数:', params)
    
    const res = await getStudentGradesList(params)
    console.log('查询成绩列表结果:', res)
    
    if (res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
      
      // 重置修改记录
      modifiedRows.value = {}
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('查询成绩列表失败:', error)
    ElMessage.error('查询成绩列表失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  // 如果来自课程教学页面，则不重置课程和学期
  if (fromCourseTeaching.value) {
    queryParams.classId = undefined
    queryParams.studentName = undefined
  } else {
    queryParams.courseId = undefined
    queryParams.semester = undefined
    queryParams.classId = undefined
    queryParams.studentName = undefined
  }
  
  queryParams.pageNum = 1
  
  // 如果有课程和学期选项，则自动查询
  if (queryParams.courseId && queryParams.semester) {
    handleQuery()
  } else {
  tableData.value = []
  total.value = 0
  }
}

// 页码变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  handleQuery()
}

const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  handleQuery()
}

// 返回
function goBack() {
  router.go(-1)
}

// 成绩变更
const handleScoreChange = (row, value) => {
  // 记录修改过的行
  modifiedRows.value[row.id] = {
    ...row,
    score: value,
    modified: true
  }
  
  // 自动计算等级和是否通过
  row.grade = calculateGrade(value)
    row.isPassed = value >= 60 ? 1 : 0
  row.isPassedDesc = value >= 60 ? '通过' : '不通过'
}

// 计算成绩等级
const calculateGrade = (score) => {
  if (score === null || score === undefined) return null
  if (score >= 95) return 'A+'
  if (score >= 90) return 'A'
  if (score >= 85) return 'B+'
  if (score >= 80) return 'B'
  if (score >= 75) return 'C+'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

// 获取等级标签类型
function getGradeTagType(grade) {
  const gradeMap = {
    'A+': 'success',
    'A': 'success',
    'B+': 'success',
    'B': 'warning',
    'C+': 'warning',
    'C': 'warning',
    'D': 'info',
    'F': 'danger'
  }
  return gradeMap[grade] || 'info'
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 保存成绩
const handleSaveGrades = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要保存的学生成绩')
    return
  }
  
  const saveItems = selectedRows.value.map(row => {
    return {
      selectionId: row.selectionId, // 使用正确的字段名
      id: row.id,
      studentId: row.studentId,
      courseId: row.courseId,
      score: row.score,
      isPassed: row.score >= 60 ? 1 : 0,
      grade: calculateGrade(row.score)
    }
  })
  
  ElMessageBox.confirm(`确定要保存${saveItems.length}条学生成绩吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    
    // 直接传递成绩数组，不包装在对象中
    batchSubmitGrades(saveItems).then(res => {
      ElMessage.success('成绩保存成功')
      // 刷新数据
      handleQuery()
      // 清空修改记录
      modifiedRows.value = {}
    }).catch(err => {
      console.error('保存成绩失败:', err)
      ElMessage.error('保存成绩失败')
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {
    // 取消操作
  })
}

// 重置选中成绩
const handleResetScores = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要重置的学生成绩')
    return
  }
  
  ElMessageBox.confirm(`确定要重置${selectedRows.value.length}条学生成绩吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    selectedRows.value.forEach(row => {
      row.score = null
      row.grade = null
      row.isPassed = null
      row.isPassedDesc = '-'
      
      // 更新修改记录
      modifiedRows.value[row.id] = {
        ...row,
        score: null,
        modified: true
      }
    })
    
    ElMessage.success('已重置选中学生的成绩')
  }).catch(() => {
    // 取消操作
  })
}

// 编辑单个学生成绩
const handleEditScore = (row) => {
  editDialog.visible = true
  editForm.id = row.id
  editForm.studentId = row.studentId
  editForm.studentName = row.studentName
  editForm.studentNo = row.studentNo
  editForm.courseName = row.courseName
  editForm.courseId = row.courseId
  editForm.score = row.score
  editForm.comment = row.comment || ''
}

// 提交编辑
const submitEdit = () => {
  editFormRef.value?.validate(valid => {
    if (!valid) return
    
    editDialog.loading = true
    
    // 更新表格中的行数据
    const rowIndex = tableData.value.findIndex(item => item.id === editForm.id)
    if (rowIndex !== -1) {
      const row = tableData.value[rowIndex]
      row.score = editForm.score
      row.comment = editForm.comment
      row.grade = calculateGrade(editForm.score)
      row.isPassed = editForm.score >= 60 ? 1 : 0
      row.isPassedDesc = editForm.score >= 60 ? '通过' : '不通过'
      
      // 更新修改记录
      modifiedRows.value[row.id] = {
        ...row,
        modified: true
      }
      
      editDialog.visible = false
      editDialog.loading = false
      
      ElMessage.success('成绩已更新，请点击保存按钮提交到服务器')
    } else {
      editDialog.loading = false
      ElMessage.error('找不到对应的学生记录')
    }
  })
}

// 清空单个学生成绩
const handleClearScore = (row) => {
  ElMessageBox.confirm(`确定要清空 ${row.studentName} 的成绩吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.score = null
    row.grade = null
      row.isPassed = null
    row.isPassedDesc = '-'
    
    // 更新修改记录
    modifiedRows.value[row.id] = {
      ...row,
      score: null,
      modified: true
    }
  
    ElMessage.success('已清空该学生的成绩，请点击保存按钮提交到服务器')
  }).catch(() => {
    // 取消操作
  })
}

// 导入成绩按钮点击
const handleImportGrades = () => {
  importDialog.visible = true
  importDialog.fileList = []
}

// 文件选择变化
const handleFileChange = (file, fileList) => {
  importDialog.fileList = fileList.slice(-1) // 只保留最后一个
}

// 提交导入
const submitImport = () => {
  if (importDialog.fileList.length === 0) {
    ElMessage.warning('请先选择要导入的文件')
    return
  }
  
  const file = importDialog.fileList[0].raw
  if (!file) {
    ElMessage.warning('文件不存在')
    return
  }
  
  if (!/\.(xlsx|xls)$/.test(file.name)) {
    ElMessage.warning('只能上传Excel文件')
    return
  }
  
  importDialog.loading = true
  
  // 模拟导入过程
  setTimeout(() => {
    importDialog.loading = false
    importDialog.visible = false
    importDialog.fileList = []
    
    ElMessage.success('成绩导入成功')
    handleQuery() // 刷新数据
  }, 1500)
}

// 导出成绩
const handleExportGrades = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  ElMessage.info('导出成绩功能待实现')
}

// 下载导入模板
const handleDownloadTemplate = () => {
  ElMessage.info('下载导入模板功能待实现')
}
</script>

<style scoped>
.app-container {
  padding: 10px;
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

.operation-container {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.no-grade {
  color: #909399;
}

.mb-2 {
  margin-bottom: 15px;
}

.import-container {
  padding: 10px;
}

.import-tips {
  margin-top: 20px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.import-tips h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.import-tips ol {
  padding-left: 20px;
  margin-bottom: 0;
  color: #606266;
}

.import-tips li {
  margin-bottom: 5px;
}
</style> 