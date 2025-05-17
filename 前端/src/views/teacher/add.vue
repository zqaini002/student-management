<template>
  <div class="teacher-add-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>新增教师</span>
          <el-button @click="goBack" link>
            <el-icon><Back /></el-icon>返回
          </el-button>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <el-form
        v-else
        ref="teacherFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="teacher-form"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="教师编号" prop="teacherNo">
                  <el-input v-model="form.teacherNo" placeholder="请输入教师编号" @input="handleTeacherNoChange" />
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
                    <el-radio :value="0">男</el-radio>
                    <el-radio :value="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="院系" prop="departmentId">
                  <el-select v-model="form.departmentId" placeholder="请选择院系" style="width: 100%">
                    <el-option
                      v-for="item in departmentList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                    <template #empty>
                      <div class="empty-data-tip">
                        <span v-if="departmentList.length === 0">暂无院系数据</span>
                        <span v-else>加载院系数据中...</span>
                      </div>
                    </template>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
                    <el-option label="教授" value="教授" />
                    <el-option label="副教授" value="副教授" />
                    <el-option label="讲师" value="讲师" />
                    <el-option label="助教" value="助教" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期" prop="entryDate">
                  <el-date-picker
                    v-model="form.entryDate"
                    type="date"
                    placeholder="选择入职日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="form.idCard" placeholder="请输入身份证号" @blur="extractBirthday" />
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
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                    <el-option label="在职" :value="0" />
                    <el-option label="离职" :value="1" />
                    <el-option label="退休" :value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="账号信息" name="account">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="form.username" placeholder="请输入用户名（默认与教师编号相同）" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
              </el-col>
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
              <el-col :span="24">
                <el-alert
                  title="系统将自动为教师分配教师角色"
                  type="info"
                  show-icon
                  :closable="false"
                  style="margin: 10px 0;"
                />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import { addTeacher } from '@/api/teacher'
import { getAllDepartments } from '@/api/department'

// 路由
const router = useRouter()

// 表单相关
const teacherFormRef = ref(null)
const activeTab = ref('basic')
const submitting = ref(false)
const loading = ref(true)

// 表单数据
const form = reactive({
  teacherNo: '',
  name: '',
  departmentId: null,
  title: '',
  gender: 0,
  idCard: '',
  birthday: null,
  entryDate: null,
  status: 0,  // 默认状态：在职
  username: '',
  password: '123456',  // 默认密码
  email: '',
  phone: ''
})

// 表单校验规则
const rules = {
  teacherNo: [
    { required: true, message: '请输入教师编号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '教师编号只能包含字母和数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  entryDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{4,16}$/, message: '用户名必须为4-16位字母或数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[\w.-]+@[\w.-]+\.\w+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 相关数据列表
const departmentList = ref([])

// 监听教师编号变化自动填充用户名
const handleTeacherNoChange = () => {
  if (form.teacherNo && !form.username) {
    form.username = form.teacherNo
  }
}

// 从身份证号提取出生日期
const extractBirthday = () => {
  const idCard = form.idCard
  if (idCard && (idCard.length === 15 || idCard.length === 18)) {
    try {
      let birthStr
      if (idCard.length === 15) {
        birthStr = `19${idCard.substring(6, 12)}`
      } else {
        birthStr = idCard.substring(6, 14)
      }
      
      const year = birthStr.substring(0, 4)
      const month = birthStr.substring(4, 6)
      const day = birthStr.substring(6, 8)
      
      // 验证日期有效性
      const yearNum = parseInt(year)
      const monthNum = parseInt(month)
      const dayNum = parseInt(day)
      
      // 检查月份和日期范围
      if (monthNum < 1 || monthNum > 12) {
        ElMessage.warning('身份证号中的月份无效，请手动选择出生日期')
        return
      }
      
      // 获取每月的天数（考虑闰年）
      const isLeapYear = (yearNum % 4 === 0 && yearNum % 100 !== 0) || (yearNum % 400 === 0)
      const daysInMonth = [
        31, // 一月
        isLeapYear ? 29 : 28, // 二月，闰年29天，平年28天
        31, // 三月
        30, // 四月
        31, // 五月
        30, // 六月
        31, // 七月
        31, // 八月
        30, // 九月
        31, // 十月
        30, // 十一月
        31  // 十二月
      ]
      
      if (dayNum < 1 || dayNum > daysInMonth[monthNum - 1]) {
        ElMessage.warning('身份证号中的日期无效，请手动选择出生日期')
        return
      }
      
      // 格式化为YYYY-MM-DD（确保月份和日期是两位数）
      const formattedMonth = month.padStart(2, '0')
      const formattedDay = day.padStart(2, '0')
      form.birthday = `${year}-${formattedMonth}-${formattedDay}`
    } catch (err) {
      console.error('从身份证号提取出生日期失败:', err)
      ElMessage.warning('无法从身份证号提取出生日期，请手动选择')
    }
  }
}

// 获取院系列表
const getDepartmentListData = () => {
  console.log('开始获取院系列表...')
  loading.value = true
  
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
    .finally(() => {
      loading.value = false
    })
}

// 初始化数据
onMounted(() => {
  console.log('教师新增页面组件已加载')
  // 获取院系列表数据
  getDepartmentListData()
})

// 提交表单
const submitForm = async () => {
  if (!teacherFormRef.value) return
  
  await teacherFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        submitting.value = true
        
        // 确认提交
        try {
          await ElMessageBox.confirm(
            `确定要添加教师 ${form.name} 吗？`, 
            '提示', 
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'info'
            }
          )
        } catch (err) {
          // 用户取消操作
          submitting.value = false
          return
        }
        
        const res = await addTeacher(form)
        ElMessage.success('教师添加成功')
        
        // 提示用户是否继续添加
        try {
          await ElMessageBox.confirm(
            '教师添加成功，是否继续添加？',
            '操作成功',
            {
              confirmButtonText: '继续添加',
              cancelButtonText: '返回列表',
              type: 'success'
            }
          )
          // 重置表单
          if (teacherFormRef.value) {
            teacherFormRef.value.resetFields()
          }
          // 重置一些字段
          form.teacherNo = ''
          form.name = ''
          form.username = ''
          form.idCard = ''
          form.birthday = null
          form.email = ''
          form.phone = ''
          
          activeTab.value = 'basic'
        } catch (err) {
          // 用户选择返回列表
          router.push('/teacher-list')
        }
      } catch (err) {
        console.error('添加教师失败:', err)
        ElMessage.error(err.response?.data?.message || '添加教师失败')
      } finally {
        submitting.value = false
      }
    } else {
      console.log('表单验证不通过:', fields)
      // 切换到错误所在的标签页
      if (Object.keys(fields).some(key => ['username', 'password', 'email', 'phone'].includes(key))) {
        activeTab.value = 'account'
      } else {
        activeTab.value = 'basic'
      }
      
      ElMessage.warning('请完善表单信息')
    }
  })
}

// 返回列表页
const goBack = () => {
  router.push('/teacher-list')
}
</script>

<style lang="scss" scoped>
.teacher-add-container {
  padding: 20px;
  
  .loading-container {
    padding: 20px;
  }
  
  .box-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        font-size: 18px;
        font-weight: bold;
      }
    }
    
    .teacher-form {
      margin-top: 20px;
      
      .el-tabs {
        margin-bottom: 30px;
      }
      
      .form-footer {
        margin-top: 20px;
        display: flex;
        justify-content: center;
        gap: 20px;
      }
    }
  }
}

.empty-data-tip {
  text-align: center;
  color: #909399;
  padding: 15px 0;
  font-size: 14px;
}
</style>