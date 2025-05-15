<template>
  <div class="teacher-edit-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>编辑教师</span>
          <div class="operations">
            <el-button type="primary" @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      
      <el-form
        ref="teacherFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        v-loading="loading"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="教师编号" prop="teacherNo">
                  <el-input v-model="form.teacherNo" placeholder="请输入教师编号" disabled />
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
                <el-form-item label="院系" prop="departmentId">
                  <el-select v-model="form.departmentId" placeholder="请选择院系">
                    <el-option
                      v-for="item in departmentOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-select v-model="form.title" placeholder="请选择职称">
                    <el-option label="教授" value="教授" />
                    <el-option label="副教授" value="副教授" />
                    <el-option label="讲师" value="讲师" />
                    <el-option label="助教" value="助教" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态">
                    <el-option label="在职" :value="0" />
                    <el-option label="离职" :value="1" />
                    <el-option label="退休" :value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <el-tab-pane label="联系信息" name="contact">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="地址" prop="address">
                  <el-input v-model="form.address" placeholder="请输入地址" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <el-tab-pane label="教育背景" name="education">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="学历" prop="education">
                  <el-select v-model="form.education" placeholder="请选择学历">
                    <el-option label="博士" value="博士" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="本科" value="本科" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="毕业院校" prop="graduateSchool">
                  <el-input v-model="form.graduateSchool" placeholder="请输入毕业院校" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthday">
                  <el-date-picker
                    v-model="form.birthday"
                    type="date"
                    placeholder="请选择出生日期"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期" prop="entryDate">
                  <el-date-picker
                    v-model="form.entryDate"
                    type="date"
                    placeholder="请选择入职日期"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
        
        <div class="form-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="goBack">取 消</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTeacherDetail, updateTeacher } from '@/api/teacher';
import { getAllDepartments } from '@/api/department';

const route = useRoute();
const router = useRouter();
const teacherId = route.params.id;

const loading = ref(false);
const activeTab = ref('basic');
const teacherFormRef = ref();
const departmentOptions = ref([]);

// 表单数据
const form = reactive({
  id: '',
  teacherNo: '',
  name: '',
  gender: 0,
  departmentId: '',
  title: '',
  status: 0,
  phone: '',
  email: '',
  address: '',
  education: '',
  graduateSchool: '',
  birthday: '',
  entryDate: '',
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

// 获取教师详情
const getTeacherInfo = async () => {
  try {
    loading.value = true;
    const response = await getTeacherDetail(teacherId);
    if (response.code === 200) {
      Object.assign(form, response.data);
    } else {
      ElMessage.error(response.msg || '获取教师详情失败');
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error('获取教师详情失败');
    loading.value = false;
  }
};

// 获取院系列表
const getDepartments = async () => {
  try {
    const response = await getAllDepartments();
    departmentOptions.value = response.data;
  } catch (error) {
    ElMessage.error('获取院系列表失败');
  }
};

// 表单提交
const submitForm = () => {
  teacherFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      loading.value = true;
      const response = await updateTeacher(form);
      if (response.code === 200) {
        ElMessage.success('修改成功');
        goBack();
      } else {
        ElMessage.error(response.msg || '修改失败');
      }
      loading.value = false;
    } catch (error) {
      ElMessage.error('提交数据失败');
      loading.value = false;
    }
  });
};

// 返回上一页
const goBack = () => {
  router.back();
};

onMounted(() => {
  getTeacherInfo();
  getDepartments();
});
</script>

<style scoped>
.teacher-edit-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}
</style> 