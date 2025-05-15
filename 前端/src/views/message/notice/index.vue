<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">公告管理</span>
          <div class="right-container">
            <el-form :inline="true" :model="queryParams" class="search-form">
              <el-form-item label="标题">
                <el-input
                  v-model="queryParams.title"
                  placeholder="请输入公告标题"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="类型">
                <el-select v-model="queryParams.type" placeholder="公告类型" clearable>
                  <el-option
                    v-for="dict in noticeTypeOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="queryParams.status" placeholder="公告状态" clearable>
                  <el-option
                    v-for="dict in noticeStatusOptions"
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
              v-hasPermi="['system:notice:add']"
            >
              <el-icon><Plus /></el-icon> 新增
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="noticeList" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" width="55" align="center" type="index" />
        <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="类型" prop="type" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.type === 0 ? 'info' : scope.row.type === 1 ? 'warning' : 'danger'"
            >
              {{ noticeTypeFormat(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 0 ? 'info' : scope.row.status === 1 ? 'success' : ''"
            >
              {{ noticeStatusFormat(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布人" prop="publisherName" width="100" align="center" />
        <el-table-column label="发布时间" prop="publishTime" width="160" align="center">
          <template #default="scope">
            <span>{{ scope.row.publishTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              text
              @click="handleView(scope.row)"
              v-hasPermi="['system:notice:query']"
            >查看</el-button>
            <el-button
              size="small"
              type="success"
              text
              @click="handleEdit(scope.row)"
              v-hasPermi="['system:notice:edit']"
              v-if="scope.row.status !== 1"
            >修改</el-button>
            <el-button
              size="small"
              type="success"
              text
              @click="handlePublish(scope.row)"
              v-hasPermi="['system:notice:edit']"
              v-if="scope.row.status === 0"
            >发布</el-button>
            <el-button
              size="small"
              type="warning"
              text
              @click="handleOffline(scope.row)"
              v-hasPermi="['system:notice:edit']"
              v-if="scope.row.status === 1"
            >下线</el-button>
            <el-button
              size="small"
              type="danger"
              text
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:notice:remove']"
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
    
    <!-- 添加或修改公告对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="60%"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form ref="noticeFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option
              v-for="dict in noticeTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="公告状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in noticeStatusOptions"
              :key="dict.value"
              :label="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 查看公告详情对话框 -->
    <el-dialog
      title="公告详情"
      v-model="viewOpen"
      width="60%"
      append-to-body
    >
      <div class="notice-detail">
        <div class="notice-title">{{ viewForm.title }}</div>
        <div class="notice-info">
          <span>
            类型：{{ noticeTypeFormat(viewForm.type) }}
          </span>
          <span>
            发布人：{{ viewForm.publisherName }}
          </span>
          <span>
            发布时间：{{ viewForm.publishTime || '-' }}
          </span>
          <span>
            状态：{{ noticeStatusFormat(viewForm.status) }}
          </span>
        </div>
        <div class="notice-content">
          {{ viewForm.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, toRefs } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listNotice, getNotice, addNotice, updateNotice, deleteNotice, publishNotice, offlineNotice } from '@/api/notice';

const noticeTypeOptions = [
  { label: '普通公告', value: 0 },
  { label: '重要公告', value: 1 },
  { label: '紧急公告', value: 2 }
];

const noticeStatusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 },
  { label: '已下线', value: 2 }
];

const noticeFormRef = ref();
const loading = ref(false);
const open = ref(false);
const viewOpen = ref(false);
const title = ref('');
const noticeList = ref([]);
const total = ref(0);

const noticeTypeFormat = (type) => {
  const typeMap = { 0: '普通公告', 1: '重要公告', 2: '紧急公告' };
  return typeMap[type] || '';
};

const noticeStatusFormat = (status) => {
  const statusMap = { 0: '草稿', 1: '已发布', 2: '已下线' };
  return statusMap[status] || '';
};

const data = reactive({
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    type: undefined,
    status: undefined
  },
  // 表单参数
  form: {
    id: undefined,
    title: '',
    type: 0,
    status: 0,
    content: ''
  },
  // 查看的公告
  viewForm: {
    id: undefined,
    title: '',
    type: 0,
    status: 0,
    content: '',
    publisherName: '',
    publishTime: ''
  },
  // 表单校验
  rules: {
    title: [
      { required: true, message: '公告标题不能为空', trigger: 'blur' },
      { max: 100, message: '公告标题长度不能超过100个字符', trigger: 'blur' }
    ],
    type: [
      { required: true, message: '公告类型不能为空', trigger: 'change' }
    ],
    content: [
      { required: true, message: '公告内容不能为空', trigger: 'blur' }
    ]
  }
});

const { queryParams, form, viewForm, rules } = toRefs(data);

/** 查询公告列表 */
const getList = () => {
  loading.value = true;
  listNotice(queryParams.value).then(response => {
    noticeList.value = response.data || [];
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
    type: undefined,
    status: undefined
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
  noticeFormRef.value?.resetFields();
  form.value = {
    id: undefined,
    title: '',
    type: 0,
    status: 0,
    content: ''
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
  title.value = '添加公告';
  open.value = true;
};

/** 修改按钮操作 */
const handleEdit = (row) => {
  resetForm();
  getNotice(row.id).then(response => {
    form.value = response.data;
    title.value = '修改公告';
    open.value = true;
  });
};

/** 查看按钮操作 */
const handleView = (row) => {
  getNotice(row.id).then(response => {
    viewForm.value = response.data;
    viewOpen.value = true;
  });
};

/** 提交表单 */
const submitForm = () => {
  noticeFormRef.value?.validate(valid => {
    if (valid) {
      if (form.value.id) {
        updateNotice(form.value).then(response => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addNotice(form.value).then(response => {
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
  ElMessageBox.confirm(`是否确认删除标题为"${row.title}"的公告?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteNotice(row.id).then(() => {
      ElMessage.success('删除成功');
      getList();
    });
  }).catch(() => {});
};

/** 发布按钮操作 */
const handlePublish = (row) => {
  ElMessageBox.confirm(`是否确认发布标题为"${row.title}"的公告?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    publishNotice(row.id).then(() => {
      ElMessage.success('发布成功');
      getList();
    });
  }).catch(() => {});
};

/** 下线按钮操作 */
const handleOffline = (row) => {
  ElMessageBox.confirm(`是否确认下线标题为"${row.title}"的公告?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    offlineNotice(row.id).then(() => {
      ElMessage.success('下线成功');
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
.notice-detail {
  padding: 0 20px;
}
.notice-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
}
.notice-info {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}
.notice-content {
  white-space: pre-wrap;
  line-height: 1.8;
  padding: 10px 0;
}
</style> 