<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>新增课程</span>
        </div>
      </template>
      
      <el-form ref="courseFormRef" :model="form" :rules="rules" label-width="120px">
        <el-divider content-position="left">基本信息</el-divider>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入课程编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学分" prop="credit">
              <el-input-number 
                v-model="form.credit" 
                :min="0" 
                :max="20" 
                :precision="1" 
                :step="0.5"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学时" prop="hours">
              <el-input-number 
                v-model="form.hours" 
                :min="0" 
                :max="200" 
                :precision="0" 
                :step="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="必修" :value="0" />
                <el-option label="选修" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开课院系" prop="departmentId">
              <el-select v-model="form.departmentId" placeholder="请选择院系" style="width: 100%">
                <el-option
                  v-for="item in departmentList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="课程描述" prop="description">
              <el-input 
                v-model="form.description" 
                type="textarea" 
                placeholder="请输入课程描述"
                :rows="4"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">教学计划</el-divider>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="适用专业" prop="majorIds">
              <el-select 
                v-model="form.majorIds" 
                multiple 
                placeholder="请选择适用专业" 
                style="width: 100%"
                :collapse-tags="true"
                :collapse-tags-tooltip="true"
              >
                <el-option
                  v-for="item in majorList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="推荐学期" prop="recommendSemester">
              <el-select v-model="form.recommendSemester" placeholder="请选择推荐学期" style="width: 100%">
                <el-option label="大一上学期" value="1-1" />
                <el-option label="大一下学期" value="1-2" />
                <el-option label="大二上学期" value="2-1" />
                <el-option label="大二下学期" value="2-2" />
                <el-option label="大三上学期" value="3-1" />
                <el-option label="大三下学期" value="3-2" />
                <el-option label="大四上学期" value="4-1" />
                <el-option label="大四下学期" value="4-2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="先修课程" prop="prerequisites">
              <el-select 
                v-model="form.prerequisites" 
                multiple 
                placeholder="请选择先修课程" 
                style="width: 100%"
                :collapse-tags="true"
                :collapse-tags-tooltip="true"
                filterable
              >
                <el-option
                  v-for="item in courseList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程难度" prop="difficulty">
              <el-rate
                v-model="form.difficulty"
                show-score
                :max="5"
                :allow-half="false"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="form-buttons">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addCourse } from '@/api/course'
import { getAllDepartments } from '@/api/department'
import { getAllMajors } from '@/api/major'
import { useRouter } from 'vue-router'

const router = useRouter()
const courseFormRef = ref(null)

// 院系列表
const departmentList = ref([])
// 专业列表
const majorList = ref([])
// 课程列表
const courseList = ref([])

// 表单数据
const form = reactive({
  code: '',
  name: '',
  credit: 3,
  hours: 48,
  type: 0,
  departmentId: null,
  description: '',
  majorIds: [],
  recommendSemester: '',
  prerequisites: [],
  difficulty: 3
})

// 表单验证规则
const rules = {
  code: [
    { required: true, message: '请输入课程编号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9-]+$/, message: '课程编号只能包含字母、数字和连字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  credit: [
    { required: true, message: '请输入学分', trigger: 'blur' }
  ],
  hours: [
    { required: true, message: '请输入学时', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择课程类型', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择开课院系', trigger: 'change' }
  ]
}

// 初始化数据
onMounted(() => {
  getDepartmentList()
  getMajorList()
  getCourseList()
})

// 获取院系列表
const getDepartmentList = async () => {
  try {
    const res = await getAllDepartments()
    if (res && res.data) {
      departmentList.value = res.data
    }
  } catch (err) {
    console.error('获取院系列表失败:', err)
    ElMessage.error('获取院系列表失败')
  }
}

// 获取专业列表
const getMajorList = async () => {
  try {
    // 假设有一个获取所有专业的接口
    const res = await getAllMajors()
    if (res && res.data) {
      majorList.value = res.data
    }
  } catch (err) {
    console.error('获取专业列表失败:', err)
    ElMessage.error('获取专业列表失败')
  }
}

// 获取课程列表（用于选择先修课程）
const getCourseList = async () => {
  try {
    // 这里实际应该调用获取所有课程的API
    // 由于没有实现该API，这里先使用模拟数据
    courseList.value = [
      { id: 1, name: '高等数学' },
      { id: 2, name: '线性代数' },
      { id: 3, name: '大学英语' },
      { id: 4, name: '计算机导论' },
      { id: 5, name: '程序设计基础' }
    ]
  } catch (err) {
    console.error('获取课程列表失败:', err)
    ElMessage.error('获取课程列表失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!courseFormRef.value) return
  
  await courseFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        await addCourse(form)
        ElMessage.success('新增课程成功')
        // 跳转到课程列表页面
        router.push('/course/list')
      } catch (err) {
        console.error('新增课程失败:', err)
        ElMessage.error(err.response?.data?.message || '新增课程失败')
      }
    } else {
      console.log('表单校验不通过', fields)
    }
  })
}

// 取消表单提交
const cancel = () => {
  ElMessage.info('已取消新增')
  router.push('/course/list')
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
  }
  
  .form-buttons {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    gap: 10px;
  }
  
  .el-divider {
    margin-top: 20px;
    margin-bottom: 20px;
    
    .el-divider__text {
      font-size: 16px;
      font-weight: bold;
      color: #409EFF;
    }
  }
}
</style> 