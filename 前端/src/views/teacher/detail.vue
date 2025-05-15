<template>
  <div class="teacher-detail-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>教师详情</span>
          <div class="operations">
            <el-button type="primary" @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <div class="teacher-info" v-loading="loading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="教师编号">{{ teacherInfo.teacherNo }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ teacherInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ teacherInfo.genderDesc }}</el-descriptions-item>
          <el-descriptions-item label="院系">{{ teacherInfo.departmentName }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ teacherInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(teacherInfo.status)">{{ teacherInfo.statusDesc }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ teacherInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ teacherInfo.email }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">{{ teacherInfo.entryDate }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ teacherInfo.education }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ teacherInfo.birthday }}</el-descriptions-item>
          <el-descriptions-item label="毕业院校">{{ teacherInfo.graduateSchool }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 教师课程信息 -->
      <div class="courses-info" v-if="teacherCourses.length > 0">
        <h3 class="section-title">授课信息</h3>
        <el-table :data="teacherCourses" stripe border style="width: 100%">
          <el-table-column prop="courseCode" label="课程编号" width="120"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="180"></el-table-column>
          <el-table-column prop="credit" label="学分" width="80"></el-table-column>
          <el-table-column prop="semesterName" label="学期"></el-table-column>
          <el-table-column prop="studentCount" label="学生数"></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="text" @click="viewCourseDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTeacherDetail } from '@/api/teacher';

const route = useRoute();
const router = useRouter();
const teacherId = route.params.id;

const loading = ref(true);
const teacherInfo = ref({});
const teacherCourses = ref([]);

// 获取教师详情
const fetchTeacherDetail = async () => {
  try {
    loading.value = true;
    const response = await getTeacherDetail(teacherId);
    teacherInfo.value = response.data;
    
    // 如果有课程信息
    if (response.data.courses) {
      teacherCourses.value = response.data.courses;
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error('获取教师详情失败');
    loading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 根据状态获取对应的标签类型
const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'success';
    case 1:
      return 'danger';
    case 2:
      return 'info';
    default:
      return '';
  }
};

// 查看课程详情
const viewCourseDetail = (course) => {
  router.push(`/course/detail/${course.id}`);
};

onMounted(() => {
  fetchTeacherDetail();
});
</script>

<style scoped>
.teacher-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  margin: 20px 0;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.teacher-info {
  margin-bottom: 20px;
}
</style> 