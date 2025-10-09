<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>在线选课</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="可选课程" name="available">
          <!-- 搜索表单 -->
          <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
            <el-form-item label="课程名称" prop="courseName">
              <el-input
                v-model="queryParams.courseName"
                placeholder="请输入课程名称"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="queryParams.courseType" placeholder="请选择课程类型" clearable>
                <el-option
                  v-for="dict in courseTypeOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="学分" prop="creditsRange">
              <el-select v-model="queryParams.creditsRange" placeholder="请选择学分范围" clearable>
                <el-option label="1学分" value="1" />
                <el-option label="2学分" value="2" />
                <el-option label="3学分" value="3" />
                <el-option label="4学分" value="4" />
                <el-option label="5学分及以上" value="5+" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="10" class="mb8">
            <right-toolbar v-model:showSearch="showSearch" @queryTable="fetchAvailableCourses"></right-toolbar>
          </el-row>

          <!-- 选课统计提示 -->
          <el-alert
            title="选课提示"
            type="info"
            :closable="false"
            show-icon
          >
            <template #default>
              <div class="selection-tips">
                <p>1. 当前已选 <strong class="highlight">{{ selectionStats.selectedCount }}</strong> 门课程，共 <strong class="highlight">{{ selectionStats.selectedCredits }}</strong> 学分</p>
                <p>2. 本学期最多可选 <strong>{{ selectionStats.maxCourses }}</strong> 门课程，最多可选 <strong>{{ selectionStats.maxCredits }}</strong> 学分</p>
                <p>3. 选课时间：{{ selectionStats.startTime }} 至 {{ selectionStats.endTime }}</p>
              </div>
            </template>
          </el-alert>

          <!-- 可选课程表格 -->
          <el-table v-loading="loading" :data="availableCoursesList" style="margin-top: 15px;">
            <el-table-column type="expand">
              <template #default="props">
                <el-form label-position="left" inline class="course-detail-table">
                  <el-form-item label="课程简介">
                    <span>{{ props.row.description || '暂无课程简介' }}</span>
                  </el-form-item>
                  <el-form-item label="考核方式">
                    <span>{{ props.row.assessmentMethod || '暂无考核方式信息' }}</span>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="课程名称" align="center" prop="courseName" min-width="120" :show-overflow-tooltip="true" />
            <el-table-column label="课程编码" align="center" prop="courseCode" width="120" />
            <el-table-column label="课程类型" align="center" prop="courseType" width="100" />
            <el-table-column label="学分" align="center" prop="credits" width="80" />
            <el-table-column label="授课教师" align="center" prop="teacherName" width="100" />
            <el-table-column label="上课地点" align="center" prop="location" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="上课时间" align="center" prop="courseTime" min-width="120" :show-overflow-tooltip="true" />
            <el-table-column label="限选人数" align="center" prop="maxStudents" width="90" />
            <el-table-column label="已选人数" align="center" width="90">
              <template #default="scope">
                {{ scope.row.selected_count || scope.row.selectedCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="考核方式" align="center" width="120" :show-overflow-tooltip="true">
              <template #default="scope">
                {{ scope.row.assessmentMethod || scope.row.assessment_method || '笔试+平时考核' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button
                  type="primary"
                  :disabled="!selectionTimeValid || checkIfExceedLimit() || scope.row.selected_count >= scope.row.capacity"
                  @click="handleSelect(scope.row)"
                >选课</el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="availableTotal > 0"
            :total="availableTotal"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="fetchAvailableCourses"
          />
        </el-tab-pane>
        
        <el-tab-pane label="已选课程" name="selected">
          <!-- 已选课程表格 -->
          <el-table v-loading="selectedLoading" :data="selectedCoursesList">
            <el-table-column label="课程名称" align="center" prop="courseName" min-width="120" :show-overflow-tooltip="true" />
            <el-table-column label="课程编码" align="center" prop="courseCode" width="120" />
            <el-table-column label="课程类型" align="center" prop="courseType" width="100" />
            <el-table-column label="学分" align="center" prop="credits" width="80" />
            <el-table-column label="授课教师" align="center" prop="teacherName" width="100" />
            <el-table-column label="上课地点" align="center" prop="location" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="上课时间" align="center" prop="courseTime" min-width="120" :show-overflow-tooltip="true" />
            <el-table-column label="考核方式" align="center" width="120" :show-overflow-tooltip="true">
              <template #default="scope">
                {{ scope.row.assessmentMethod || scope.row.assessment_method || '笔试+平时考核' }}
              </template>
            </el-table-column>
            <el-table-column label="选课状态" align="center" prop="selectionStatus" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.selectionStatus === 0 ? 'warning' : scope.row.selectionStatus === 1 ? 'success' : 'info'">
                  {{ scope.row.selectionStatus === 0 ? '待审核' : scope.row.selectionStatus === 1 ? '已确认' : '已退选' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button
                  type="danger"
                  :disabled="!canWithdraw(scope.row) || !selectionTimeValid"
                  @click="handleWithdraw(scope.row)"
                >退选</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <pagination
            v-show="selectedTotal > 0"
            :total="selectedTotal"
            v-model:page="selectedQueryParams.pageNum"
            v-model:limit="selectedQueryParams.pageSize"
            @pagination="getSelectedCourses"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 课程冲突提示对话框 -->
    <el-dialog title="选课冲突提示" v-model="conflictDialogVisible" width="500px">
      <div class="conflict-message">
        <el-alert
          title="选课冲突"
          type="error"
          description="以下课程与您已选课程存在时间冲突，无法选择："
          :closable="false"
          show-icon
        />
        <div class="conflict-details" v-if="conflictCourse">
          <p><strong>冲突课程：</strong>{{ conflictCourse.courseName || conflictCourse.course_name }}</p>
          <p><strong>上课时间：</strong>{{ conflictCourse.courseTime || conflictCourse.class_time }}</p>
          <p><strong>冲突原因：</strong>与已选课程《{{ conflictWithCourse.courseName || conflictWithCourse.course_name }}》在时间上存在冲突</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="conflictDialogVisible = false">我知道了</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选课确认对话框 -->
    <el-dialog title="选课确认" v-model="selectConfirmDialogVisible" width="500px">
      <div class="select-confirm-message">
        <p>您确定要选择以下课程吗？</p>
        <div class="course-info" v-if="currentSelectCourse">
          <p><strong>课程名称：</strong>{{ currentSelectCourse.courseName }}</p>
          <p><strong>课程编码：</strong>{{ currentSelectCourse.courseCode }}</p>
          <p><strong>学分：</strong>{{ currentSelectCourse.credits }}</p>
          <p><strong>上课时间：</strong>{{ currentSelectCourse.courseTime }}</p>
          <p><strong>上课地点：</strong>{{ currentSelectCourse.location }}</p>
          <p><strong>授课教师：</strong>{{ currentSelectCourse.teacherName }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="selectConfirmDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSelect">确认选课</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 退选确认对话框 -->
    <el-dialog title="退选确认" v-model="withdrawConfirmDialogVisible" width="500px">
      <div class="withdraw-confirm-message">
        <el-alert
          title="退选提醒"
          type="warning"
          description="退选后如果再次选择该课程，将重新进入审核状态，且可能因课程已满而无法选择。"
          :closable="false"
          show-icon
        />
        <div class="course-info" v-if="currentWithdrawCourse">
          <p><strong>课程名称：</strong>{{ currentWithdrawCourse.courseName }}</p>
          <p><strong>课程编码：</strong>{{ currentWithdrawCourse.courseCode }}</p>
          <p><strong>学分：</strong>{{ currentWithdrawCourse.credits }}</p>
          <p><strong>上课时间：</strong>{{ currentWithdrawCourse.courseTime }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="withdrawConfirmDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmWithdraw">确认退选</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableCourses, getStudentSelectedCourses, selectCourse, withdrawCourse, getSelectionSettings, checkTimeConflict, getCurrentStudentAvailableCourses, getCurrentStudentSelectedCourses, currentStudentSelectCourse, currentStudentWithdrawCourse } from '@/api/course'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const studentId = ref(userStore.userId)
const loading = ref(false)
const selectedLoading = ref(false)
const showSearch = ref(true)
const availableTotal = ref(0)
const selectedTotal = ref(0)
const availableCoursesList = ref([])
const selectedCoursesList = ref([])
const activeTab = ref('available')
const conflictDialogVisible = ref(false)
const selectConfirmDialogVisible = ref(false)
const withdrawConfirmDialogVisible = ref(false)
const conflictCourse = ref(null)
const conflictWithCourse = ref(null)
const currentSelectCourse = ref(null)
const currentWithdrawCourse = ref(null)

// 课程类型选项
const courseTypeOptions = ref([
  { value: '必修', label: '必修课' },
  { value: '选修', label: '选修课' },
  { value: '公选', label: '公共选修课' },
  { value: '实践', label: '实践课' }
])

// 选课统计数据
const selectionStats = ref({
  selectedCount: 0,
  selectedCredits: 0,
  maxCourses: 10,
  maxCredits: 30,
  startTime: '2025-06-10 00:00:00',
  endTime: '2025-06-20 23:59:59',
  isSelectionTime: true
})

// 是否在选课时间内
const selectionTimeValid = computed(() => {
  return selectionStats.value.isSelectionTime
})

// 查询参数 - 可选课程
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  courseName: undefined,
  courseType: undefined,
  creditsRange: undefined
})

// 查询参数 - 已选课程
const selectedQueryParams = ref({
  pageNum: 1,
  pageSize: 10
})

/** 查询可选课程列表 */
function fetchAvailableCourses() {
  loading.value = true
  getCurrentStudentAvailableCourses(queryParams.value).then(response => {
    if (response && response.code === 200 && response.data) {
      // 添加字段映射处理
      const records = (response.data.rows || []).map(item => {
        return {
          ...item,
          // 字段映射，确保前端表格能正确显示数据
          courseName: item.courseName || item.course_name, // course_name -> courseName
          courseCode: item.courseCode || item.course_code, // course_code -> courseCode
          teacherName: item.teacherName || item.teacher_name, // teacher_name -> teacherName
          maxStudents: item.capacity || item.maxStudents, // capacity -> maxStudents
          courseTime: item.class_time || item.classTime || item.courseTime, // class_time -> courseTime
          selectionStatus: item.status !== undefined ? item.status : item.selectionStatus,
          location: item.location || item.classLocation, // 确保location字段存在
          // 确保已选人数字段存在(后端返回的字段可能是selected_count或selectedCount)
          selectedCount: item.selected_count !== undefined ? item.selected_count : (item.selectedCount || 0),
          // 课程类型转为显示文本
          courseType: item.course_type !== undefined ? 
            (item.course_type === 0 ? '必修' : item.course_type === 1 ? '选修' : item.course_type) :
            (item.courseType !== undefined ? 
              (item.courseType === 0 ? '必修' : item.courseType === 1 ? '选修' : item.courseType) : 
              '未知'),
          // 确保其他关键字段存在
          credits: item.credits || item.credit || 0, // 确保credits字段存在
          description: item.course_description || item.description || '', // 确保description字段存在
          assessmentMethod: item.assessmentMethod || item.assessment_method || '笔试+平时考核' // 确保考核方式字段存在
        }
      })
      
      availableCoursesList.value = records
      availableTotal.value = parseInt(response.data.total || 0)
      
      // 如果没有数据，显示提示信息
      if (availableCoursesList.value.length === 0 && queryParams.value.pageNum > 1) {
        queryParams.value.pageNum = 1
        fetchAvailableCourses()
      }
    } else {
      ElMessage.warning('获取可选课程失败: ' + (response ? response.msg : '未知错误'))
      availableCoursesList.value = []
      availableTotal.value = 0
    }
    loading.value = false
  }).catch(error => {
    console.error('获取可选课程失败:', error)
    ElMessage.error('获取可选课程失败: ' + (error.message || '服务器错误'))
    availableCoursesList.value = []
    availableTotal.value = 0
    loading.value = false
  })
}

/** 查询已选课程列表 */
function getSelectedCourses() {
  selectedLoading.value = true
  getCurrentStudentSelectedCourses(selectedQueryParams.value).then(response => {
    if (response && response.code === 200 && response.data) {
      // 添加字段映射处理
      const records = (response.data.rows || []).map(item => {
        return {
          ...item,
          // 字段映射，确保前端表格能正确显示数据
          courseName: item.courseName || item.course_name, // course_name -> courseName
          courseCode: item.courseCode || item.course_code, // course_code -> courseCode
          teacherName: item.teacherName || item.teacher_name, // teacher_name -> teacherName
          courseTime: item.classTime || item.class_time || item.courseTime, // classTime或class_time -> courseTime
          selectionStatus: item.status !== undefined ? item.status : (item.selectionStatus || 0),
          location: item.location || item.classLocation, // 确保location字段存在
          // 确保已选人数字段存在(后端返回的字段可能是selected_count或selectedCount)
          selectedCount: item.selected_count !== undefined ? item.selected_count : (item.selectedCount || 0),
          // 课程类型处理，确保显示文本
          courseType: typeof item.courseType === 'number' ? 
            (item.courseType === 0 ? '必修' : item.courseType === 1 ? '选修' : item.courseType) :
            (typeof item.course_type === 'number' ? 
              (item.course_type === 0 ? '必修' : item.course_type === 1 ? '选修' : item.course_type) :
              item.courseType || item.course_type || '未知'),
          // 确保其他关键字段存在
          credits: item.credits || item.credit || 0, // 确保credits字段存在
          assessmentMethod: item.assessmentMethod || item.assessment_method || '笔试+平时考核' // 确保考核方式字段存在
        }
      })
      
      selectedCoursesList.value = records
      selectedTotal.value = parseInt(response.data.total || 0)
    
    // 计算选课统计
    calculateSelectionStats()
    } else {
      ElMessage.warning('获取已选课程失败: ' + (response ? response.msg : '未知错误'))
      selectedCoursesList.value = []
      selectedTotal.value = 0
    }
    selectedLoading.value = false
  }).catch(error => {
    console.error('获取已选课程失败:', error)
    ElMessage.error('获取已选课程失败: ' + (error.message || '服务器错误'))
    selectedCoursesList.value = []
    selectedTotal.value = 0
    selectedLoading.value = false
  })
}

/** 获取选课配置 */
function getSelectionConfig() {
  getSelectionSettings().then(response => {
    if (response && response.code === 200 && response.data) {
    const data = response.data
    selectionStats.value.maxCourses = data.maxCourses || 10
    selectionStats.value.maxCredits = data.maxCredits || 30
      selectionStats.value.startTime = data.startTime || '暂未设置'
      selectionStats.value.endTime = data.endTime || '暂未设置'
      selectionStats.value.isSelectionTime = data.isSelectionTime !== undefined ? data.isSelectionTime : true
      selectionStats.value.currentSemester = data.currentSemester || '当前学期'
    } else {
      ElMessage.warning('获取选课设置失败: ' + (response ? response.msg : '未知错误'))
    }
  }).catch(error => {
    console.error('获取选课设置失败:', error)
    ElMessage.error('获取选课设置失败: ' + (error.message || '服务器错误'))
  })
}

/** 计算选课统计数据 */
function calculateSelectionStats() {
  // 只计算有效的选课记录（未退选的）
  const validSelections = selectedCoursesList.value.filter(course => course.selectionStatus !== 2)
  
  selectionStats.value.selectedCount = validSelections.length
  selectionStats.value.selectedCredits = validSelections.reduce((sum, course) => sum + (course.credits || 0), 0)
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  fetchAvailableCourses()
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    courseName: undefined,
    courseType: undefined,
    creditsRange: undefined
  }
  handleQuery()
}

/** 选课操作 */
function handleSelect(row) {
  // 检查课程冲突
  checkIfConflict(row).then(hasConflict => {
    if (hasConflict) {
    return
  }
  
  // 检查选课数量和学分限制
  if (checkIfExceedLimit()) {
    ElMessage.warning('已达到选课数量或学分上限，无法继续选课')
    return
  }
  
  // 检查选课时间是否有效
  if (!selectionTimeValid.value) {
    ElMessage.warning('当前不在选课时间范围内，无法进行选课操作')
    return
  }
  
  // 显示选课确认对话框
  currentSelectCourse.value = row
  selectConfirmDialogVisible.value = true
  }).catch(error => {
    console.error('处理选课操作失败:', error)
    ElMessage.error('处理选课操作失败，请重试')
  })
}

/** 确认选课 */
function confirmSelect() {
  if (!currentSelectCourse.value) return
  
  currentStudentSelectCourse(currentSelectCourse.value.id).then(response => {
    ElMessage.success('选课成功')
    selectConfirmDialogVisible.value = false
    fetchAvailableCourses()
    // 如果在"已选课程"标签页，也需要刷新数据
    if (activeTab.value === 'selected') {
      getSelectedCourses()
    }
  }).catch(error => {
    ElMessage.error(error.message || '选课失败，请重试')
  })
}

/** 退选操作 */
function handleWithdraw(row) {
  // 检查是否可以退选
  if (!canWithdraw(row)) {
    ElMessage.warning('该课程状态不允许退选')
    return
  }
  
  // 检查选课时间是否有效
  if (!selectionTimeValid.value) {
    ElMessage.warning('当前不在选课时间范围内，无法进行退选操作')
    return
  }
  
  // 显示退选确认对话框
  currentWithdrawCourse.value = row
  withdrawConfirmDialogVisible.value = true
}

/** 确认退选 */
function confirmWithdraw() {
  if (!currentWithdrawCourse.value) return
  
  // 使用 course_offering_id 而不是选课记录 id
  const courseOfferingId = currentWithdrawCourse.value.course_offering_id || currentWithdrawCourse.value.courseOfferingId
  currentStudentWithdrawCourse(courseOfferingId).then(response => {
    ElMessage.success('退选成功')
    withdrawConfirmDialogVisible.value = false
    getSelectedCourses()
  }).catch(error => {
    ElMessage.error(error.message || '退选失败，请重试')
  })
}

/** 检查课程时间冲突 */
async function checkIfConflict(course) {
  try {
  // 如果没有已选课程，不可能冲突
    if (!selectedCoursesList.value || selectedCoursesList.value.length === 0) {
      return false
    }
  
  // 只检查未退选的课程
  const validSelections = selectedCoursesList.value.filter(c => c.selectionStatus !== 2)
    if (validSelections.length === 0) {
      return false
    }
    
    // 获取规范化的课程时间
    const courseTime = course.courseTime || course.class_time
    if (!courseTime) {
      return false
    }
    
    // 逐个检查每门已选课程
  for (const selectedCourse of validSelections) {
      try {
        // 获取已选课程的时间
        const selectedCourseTime = selectedCourse.courseTime || selectedCourse.class_time
        if (!selectedCourseTime) {
          continue
        }
        
        // 调用后端API检查冲突
        const response = await checkTimeConflict({
          courseTime1: selectedCourseTime,
          courseTime2: courseTime
        })
        
        // 如果API返回冲突结果
        if (response && response.code === 200 && response.data === true) {
          // 设置冲突信息用于显示
          conflictCourse.value = {
            ...course,
            courseName: course.courseName || course.course_name,
            courseTime: courseTime
          }
          
          conflictWithCourse.value = {
            ...selectedCourse,
            courseName: selectedCourse.courseName || selectedCourse.course_name,
            courseTime: selectedCourseTime
          }
          
          // 显示冲突对话框
      conflictDialogVisible.value = true
      return true
        }
      } catch (error) {
        console.error('检查时间冲突失败:', error)
        ElMessage.error('检查课程时间冲突失败: ' + (error.message || '系统异常，请联系管理员'))
    }
  }
  
  return false
  } catch (error) {
    console.error('处理课程冲突检查失败:', error)
    ElMessage.error('检查课程冲突失败: ' + (error.message || '系统异常，请联系管理员'))
    return false // 出错时允许继续选课，避免阻断用户操作
  }
}

/** 检查是否超过选课限制 */
function checkIfExceedLimit() {
  // 检查数量和学分是否已达上限
  const validSelections = selectedCoursesList.value.filter(course => course.selectionStatus !== 2)
  
  if (validSelections.length >= selectionStats.value.maxCourses) {
    return true
  }
  
  const totalCredits = validSelections.reduce((sum, course) => sum + (course.credits || 0), 0)
  if (totalCredits >= selectionStats.value.maxCredits) {
    return true
  }
  
  return false
}

/** 检查课程是否可以退选 */
function canWithdraw(course) {
  // 只有待审核和已确认的课程可以退选
  return course.selectionStatus === 0 || course.selectionStatus === 1
}

/** 标签页切换处理 */
function handleTabClick() {
  if (activeTab.value === 'available') {
    fetchAvailableCourses()
  } else if (activeTab.value === 'selected') {
    getSelectedCourses()
  }
}

// 初始化数据
onMounted(async () => {
  try {
    // 先获取选课设置
    await getSelectionConfig()
    
    // 然后同时获取可选课程和已选课程
    Promise.all([
      fetchAvailableCourses(),
  getSelectedCourses()
    ]).catch(error => {
      console.error('初始化数据加载失败:', error)
      ElMessage.error('数据加载失败，请刷新页面重试')
    })
  } catch (error) {
    console.error('初始化选课设置失败:', error)
    ElMessage.error('初始化选课设置失败，某些功能可能不可用')
  }
})
</script>

<style scoped>
.selection-tips {
  font-size: 14px;
  line-height: 1.6;
}

.selection-tips .highlight {
  color: #409EFF;
  font-size: 16px;
}

.conflict-message,
.select-confirm-message,
.withdraw-confirm-message {
  margin-bottom: 20px;
}

.conflict-details,
.course-info {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.course-detail-table {
  display: flex;
  flex-direction: column;
  padding: 10px 20px;
}

.course-detail-table .el-form-item {
  margin-bottom: 10px;
}
</style> 