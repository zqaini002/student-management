<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">待办事项</span>
          <div class="right-container">
            <el-form :inline="true" :model="queryParams" class="search-form">
              <el-form-item label="标题">
                <el-input
                  v-model="queryParams.title"
                  placeholder="请输入待办标题"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="queryParams.status" placeholder="待办状态" clearable>
                  <el-option
                    v-for="dict in todoStatusOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="优先级">
                <el-select v-model="queryParams.priority" placeholder="优先级" clearable>
                  <el-option
                    v-for="dict in priorityOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">
                  <el-icon><Search /></el-icon> 搜索
                </el-button>
                <el-button @click="resetQuery">
                  <el-icon><Refresh /></el-icon> 重置
                </el-button>
              </el-form-item>
            </el-form>
            
            <el-button
              type="primary"
              plain
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon> 新增
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="todoList" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" width="55" align="center" type="index" />
        <el-table-column label="标题" prop="title" show-overflow-tooltip width="200">
          <template #default="scope">
            <el-tooltip :content="scope.row.title" placement="top" :show-after="300">
              <span>{{ scope.row.title }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" show-overflow-tooltip min-width="180">
          <template #default="scope">
            <el-tooltip :content="scope.row.content" placement="top" :show-after="300">
              <span>{{ scope.row.content }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 0 ? 'warning' : 'success'"
            >
              {{ todoStatusFormat(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" prop="priority" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.priority === 0 ? 'info' : scope.row.priority === 1 ? 'warning' : 'danger'"
            >
              {{ priorityFormat(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="到期时间" prop="dueDate" width="160" align="center">
          <template #default="scope">
            <span>{{ scope.row.dueDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              text
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              size="small"
              type="success"
              text
              @click="handleComplete(scope.row)"
              v-if="scope.row.status === 0"
            >完成</el-button>
            <el-button
              size="small"
              type="danger"
              text
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <!-- 添加或修改待办事项对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="500px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form ref="todoFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入待办标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入待办内容"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option
              v-for="dict in priorityOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="datetime"
            placeholder="请选择到期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listTodo, listMyTodo, getTodo, addTodo, updateTodo, deleteTodo, completeTodo } from '@/api/todo';

// 状态字典
const todoStatusOptions = [
  { label: '未完成', value: 0 },
  { label: '已完成', value: 1 }
];

// 优先级字典
const priorityOptions = [
  { label: '低', value: 0 },
  { label: '中', value: 1 },
  { label: '高', value: 2 }
];

const todoFormRef = ref();
const loading = ref(false);
const open = ref(false);
const title = ref('');
const todoList = ref([]);
const total = ref(0);
const isAdmin = ref(false); // 后续根据权限判断是否是管理员

// 状态格式化
const todoStatusFormat = (status) => {
  const statusMap = { 0: '未完成', 1: '已完成' };
  return statusMap[status] || '';
};

// 优先级格式化
const priorityFormat = (priority) => {
  const priorityMap = { 0: '低', 1: '中', 2: '高' };
  return priorityMap[priority] || '';
};

const data = reactive({
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    status: undefined,
    priority: undefined
  },
  // 表单参数
  form: {
    id: undefined,
    title: '',
    content: '',
    status: 0,
    priority: 1,
    dueDate: null
  },
  // 表单校验
  rules: {
    title: [
      { required: true, message: '待办标题不能为空', trigger: 'blur' },
      { max: 100, message: '待办标题长度不能超过100个字符', trigger: 'blur' }
    ],
    priority: [
      { required: true, message: '优先级不能为空', trigger: 'change' }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询待办事项列表 */
const getList = () => {
  loading.value = true;
  // 根据角色使用不同的接口：管理员查看所有，普通用户只查看自己的
  const apiFunc = isAdmin.value ? listTodo : listMyTodo;
  apiFunc(queryParams.value).then(response => {
    todoList.value = response.data || [];
    total.value = response.total || 0;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
};

/** 重置查询操作 */
const resetQuery = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    status: undefined,
    priority: undefined
  };
  handleQuery();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 分页大小选择 */
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val;
  getList();
};

/** 页码选择 */
const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val;
  getList();
};

/** 重置表单数据 */
const resetForm = () => {
  todoFormRef.value?.resetFields();
  form.value = {
    id: undefined,
    title: '',
    content: '',
    status: 0,
    priority: 1,
    dueDate: null
  };
};

/** 取消按钮 */
const cancel = () => {
  open.value = false;
  resetForm();
};

/** 新增按钮操作 */
const handleAdd = () => {
  resetForm();
  title.value = '添加待办事项';
  open.value = true;
};

/** 修改按钮操作 */
const handleEdit = (row) => {
  resetForm();
  getTodo(row.id).then(response => {
    form.value = response.data;
    title.value = '修改待办事项';
    open.value = true;
  });
};

/** 提交表单 */
const submitForm = () => {
  todoFormRef.value?.validate(valid => {
    if (valid) {
      if (form.value.id) {
        updateTodo(form.value).then(response => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addTodo(form.value).then(response => {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 删除按钮操作 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`是否确认删除标题为"${row.title}"的待办事项?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteTodo(row.id).then(() => {
      ElMessage.success('删除成功');
      getList();
    });
  }).catch(() => {});
};

/** 完成待办事项 */
const handleComplete = (row) => {
  ElMessageBox.confirm(`是否确认完成标题为"${row.title}"的待办事项?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    completeTodo(row.id).then(() => {
      ElMessage.success('操作成功');
      getList();
    });
  }).catch(() => {});
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
}
.right-container {
  display: flex;
  align-items: center;
}
.search-form {
  margin-right: 10px;
}
</style> 