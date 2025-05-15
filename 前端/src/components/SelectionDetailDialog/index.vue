<template>
  <div class="selection-detail-dialog">
    <el-dialog 
      :title="title" 
      v-model="dialogVisible" 
      width="700px"
      @close="handleClose"
    >
      <el-form ref="form" :model="formData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="学生信息">
              <div>{{ formData.studentName }} ({{ formData.studentNo }})</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级">
              <div>{{ formData.className }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="课程信息">
              <div>{{ formData.courseCode }} - {{ formData.courseName }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授课教师">
              <div>{{ formData.teacherName }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="学期">
              <div>{{ formData.semester }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选课时间">
              <div>{{ formData.selectionTime }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="formData.status" :disabled="readOnly">
                <el-option
                  v-for="dict in statusOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成绩">
              <el-input-number 
                v-model="formData.score" 
                :min="0" 
                :max="100" 
                :step="0.5" 
                :disabled="readOnly || (formData.status === 1 && !isAdmin)" 
                :precision="1"
                @change="handleScoreChange"
              />
              <span v-if="formData.score !== null && formData.score !== undefined" style="margin-left: 10px;">
                <el-tag :type="getScoreLevel(formData.score).type">{{ getScoreLevel(formData.score).label }}</el-tag>
              </span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSubmit" v-if="!readOnly && formData.status !== 1">确 定</el-button>
        <el-button type="danger" @click="handleWithdraw" v-if="!readOnly && formData.status === 0">退 课</el-button>
        <el-button @click="handleClose">{{ readOnly ? '关 闭' : '取 消' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SelectionDetailDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '选课详情'
    },
    data: {
      type: Object,
      default: () => ({})
    },
    readOnly: {
      type: Boolean,
      default: true
    },
    isAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: false,
      formData: {},
      statusOptions: [
        { value: 0, label: "已选" },
        { value: 1, label: "已退" },
        { value: 2, label: "已修完" }
      ],
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        // 深拷贝数据，避免引用问题
        this.formData = this.data ? JSON.parse(JSON.stringify(this.data)) : {};
        console.log('对话框组件接收到的数据:', this.data);
        console.log('对话框组件内部数据:', this.formData);
      }
    },
    dialogVisible(val) {
      if (!val) {
        this.$emit('update:visible', false);
      }
    },
    data: {
      deep: true,
      handler(newVal) {
        if (this.dialogVisible && newVal) {
          // 深拷贝数据，避免引用问题
          this.formData = JSON.parse(JSON.stringify(newVal));
          console.log('对话框组件数据更新:', this.formData);
        }
      }
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
      this.$emit('close');
    },
    handleSubmit() {
      console.log('准备提交表单数据:', this.formData);
      
      // 深拷贝一份数据，避免直接引用
      const submitData = JSON.parse(JSON.stringify(this.formData));
      console.log('提交的完整表单数据:', submitData);
      
      this.$emit('submit', submitData);
    },
    handleWithdraw() {
      this.$emit('withdraw', this.formData);
    },
    handleScoreChange(val) {
      console.log('成绩已修改为:', val);
    },
    getScoreLevel(score) {
      if (score === null || score === undefined) {
        return { type: '', label: '未评分' };
      }
      if (score >= 90) {
        return { type: 'success', label: '优秀' };
      } else if (score >= 80) {
        return { type: 'info', label: '良好' };
      } else if (score >= 70) {
        return { type: 'warning', label: '中等' };
      } else if (score >= 60) {
        return { type: 'danger', label: '及格' };
      } else {
        return { type: 'info', label: '不及格' };
      }
    }
  }
}
</script>

<style scoped>
.selection-detail-dialog {
  /* 这里可以添加你的自定义样式 */
}
</style> 