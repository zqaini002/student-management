<template>
  <div class="student-edit-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>编辑学生</span>
          <el-button @click="goBack" link>
            <el-icon><Back /></el-icon>返回
          </el-button>
        </div>
      </template>
      
      <el-form
        ref="studentFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="student-form"
        v-loading="loading"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学号" prop="studentNo">
                  <el-input v-model="form.studentNo" placeholder="请输入学号" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender">
                    <el-radio :label="0">男</el-radio>
                    <el-radio :label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="班级" prop="classId">
                  <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
                    <el-option
                      v-for="item in classList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                    <template #empty>
                      <div class="empty-data-tip">
                        <span v-if="classList.length === 0">暂无班级数据</span>
                        <span v-else>加载班级数据中...</span>
                      </div>
                    </template>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入学日期" prop="admissionDate">
                  <el-date-picker
                    v-model="form.admissionDate"
                    type="date"
                    placeholder="选择入学日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="预计毕业" prop="graduationDate">
                  <el-date-picker
                    v-model="form.graduationDate"
                    type="date"
                    placeholder="选择毕业日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="form.idCard" placeholder="请输入身份证号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthday">
                  <el-date-picker
                    v-model="form.birthday"
                    type="date"
                    placeholder="选择出生日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="家庭住址" prop="address">
                  <el-input v-model="form.address" placeholder="请输入家庭住址" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                    <el-option label="在读" :value="0" />
                    <el-option label="毕业" :value="1" />
                    <el-option label="休学" :value="2" />
                    <el-option label="退学" :value="3" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="账号信息" name="account">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号码" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
        
        <div class="form-footer">
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">提交</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import { getStudentDetail, updateStudent } from '@/api/student'
import { getAllClasses } from '@/api/class'
import { getAllMajors } from '@/api/major'
import { getAllDepartments } from '@/api/department'

// 路由
const router = useRouter()
const route = useRoute()
const studentId = ref(Number(route.params.id))

// 表单相关
const studentFormRef = ref(null)
const activeTab = ref('basic')
const loading = ref(true)
const submitting = ref(false)

// 表单数据
const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: 0,
  classId: null,
  admissionDate: null,
  graduationDate: null,
  idCard: '',
  birthday: null,
  address: '',
  email: '',
  phone: '',
  status: 0
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  admissionDate: [
    { required: true, message: '请选择入学日期', trigger: 'change' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[\w.-]+@[\w.-]+\.\w+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 相关数据列表
const classList = ref([])
const majorList = ref([])
const departmentList = ref([])

// 获取学生详情
const getStudentData = () => {
  loading.value = true
  
  getStudentDetail(studentId.value)
    .then(res => {
      console.log('获取学生详情成功:', res.data)
      // 确保数据类型正确
      if (res.data) {
        const studentData = { ...res.data }
        
        // 确保classId是数字类型
        if (studentData.classId) {
          studentData.classId = Number(studentData.classId)
        }
        
        // 确保gender是数字类型
        if (studentData.gender !== undefined) {
          studentData.gender = Number(studentData.gender)
        }
        
        // 确保status是数字类型
        if (studentData.status !== undefined) {
          studentData.status = Number(studentData.status)
        }
        
        Object.assign(form, studentData)
      } else {
        ElMessage.warning('获取的学生数据为空')
      }
      loading.value = false
    })
    .catch(err => {
      console.error('获取学生详情失败:', err)
      ElMessage.error('获取学生详情失败')
      loading.value = false
    })
}

// 获取班级列表
const getClassListData = () => {
  console.log('开始获取班级列表...')
  
  return getAllClasses()
    .then(res => {
      console.log('班级列表获取成功:', res.data)
      if (Array.isArray(res.data)) {
        classList.value = res.data
      } else {
        console.warn('班级列表数据格式不正确:', res.data)
        classList.value = []
        ElMessage.warning('班级列表数据格式不正确')
      }
      return Promise.resolve(res.data)
    })
    .catch(err => {
      console.error('获取班级列表失败:', err)
      classList.value = []
      ElMessage.warning('获取班级列表失败，请刷新页面重试')
      return Promise.resolve([]) // 不阻止其他操作
    })
}

// 获取专业列表
const getMajorListData = () => {
  console.log('开始获取专业列表...')
  
  return getAllMajors()
    .then(res => {
      console.log('专业列表获取成功:', res.data)
      if (Array.isArray(res.data)) {
        majorList.value = res.data
      } else {
        console.warn('专业列表数据格式不正确:', res.data)
        majorList.value = []
        ElMessage.warning('专业列表数据格式不正确')
      }
      return Promise.resolve(res.data)
    })
    .catch(err => {
      console.error('获取专业列表失败:', err)
      majorList.value = []
      ElMessage.warning('获取专业列表失败，请刷新页面重试')
      return Promise.resolve([]) // 不阻止其他操作
    })
}

// 获取院系列表
const getDepartmentListData = () => {
  console.log('开始获取院系列表...')
  
  return getAllDepartments()
    .then(res => {
      console.log('院系列表获取成功:', res.data)
      if (Array.isArray(res.data)) {
        departmentList.value = res.data
      } else {
        console.warn('院系列表数据格式不正确:', res.data)
        departmentList.value = []
        ElMessage.warning('院系列表数据格式不正确')
      }
      return Promise.resolve(res.data)
    })
    .catch(err => {
      console.error('获取院系列表失败:', err)
      departmentList.value = []
      ElMessage.warning('获取院系列表失败，请刷新页面重试')
      return Promise.resolve([]) // 不阻止其他操作
    })
}

// 初始化数据
onMounted(() => {
  // 加载相关数据
  Promise.all([
    getClassListData(),
    getMajorListData(),
    getDepartmentListData()
  ]).then(() => {
    console.log('所有基础数据加载完成')
    getStudentData()
  }).catch(err => {
    console.error('加载基础数据时出错:', err)
    // 即使加载其他数据出错，仍然加载学生详情
    getStudentData()
  })
})

// 提交表单
const submitForm = async () => {
  if (!studentFormRef.value) return
  
  // 首先检查必要的数据是否加载完成
  if (classList.value.length === 0) {
    ElMessage.warning('班级数据未加载，无法提交表单。请刷新页面重试')
    return
  }
  
  // 检查并转换数据类型
  if (form.classId !== null) {
    form.classId = Number(form.classId)
  }
  
  if (form.gender !== null) {
    form.gender = Number(form.gender)
  }
  
  if (form.status !== null) {
    form.status = Number(form.status)
  }
  
  await studentFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        submitting.value = true
        console.log('表单数据:', form)
        
        // 提交表单
        const res = await updateStudent(form)
        console.log('更新学生成功:', res)
        ElMessage.success('学生信息更新成功')
        
        // 返回列表页
        router.push('/student-list')
      } catch (error) {
        console.error('更新学生失败:', error)
        ElMessage.error(error.response?.data?.message || '更新学生失败')
      } finally {
        submitting.value = false
      }
    } else {
      console.error('表单验证失败:', JSON.stringify(fields, null, 2))
      // 切换到有错误的标签页
      const fieldKeys = Object.keys(fields)
      console.log('验证失败的字段:', fieldKeys)
      
      const basicFields = ['studentNo', 'name', 'gender', 'classId', 'admissionDate', 'graduationDate', 'idCard', 'birthday', 'address', 'status']
      const accountFields = ['email', 'phone']
      
      if (fieldKeys.some(key => basicFields.includes(key))) {
        activeTab.value = 'basic'
        console.log('切换到基本信息标签页')
      } else if (fieldKeys.some(key => accountFields.includes(key))) {
        activeTab.value = 'account'
        console.log('切换到账号信息标签页')
      }
      
      // 构建错误信息
      let errorMsg = '表单填写有误，请检查以下字段：';
      for (const key in fields) {
        if (fields[key] && fields[key][0] && fields[key][0].message) {
          errorMsg += `\n- ${fields[key][0].message}`;
        }
      }
      
      ElMessage.error(errorMsg)
      submitting.value = false
    }
  })
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.student-edit-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .student-form {
    margin-top: 20px;
  }
  
  .form-footer {
    margin-top: 30px;
    text-align: center;
  }
  
  .empty-data-tip {
    text-align: center;
    color: #909399;
    padding: 15px 0;
    font-size: 14px;
  }
}
</style> 