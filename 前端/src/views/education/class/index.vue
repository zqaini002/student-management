<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="班级名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入班级名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="班级编码" prop="code">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入班级编码"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <el-select
            v-model="queryParams.majorId"
            placeholder="请选择所属专业"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="(option, index) in majorOptions"
              :key="index"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select
            v-model="queryParams.grade"
            placeholder="请选择年级"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="(grade, index) in gradeOptions"
              :key="index"
              :label="grade"
              :value="grade"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['education:class:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['education:class:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['education:class:remove']"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 表格展示区域 -->
      <el-table v-loading="loading" :data="classList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="班级ID" align="center" prop="id" width="80" />
        <el-table-column label="班级名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="班级编码" align="center" prop="code" :show-overflow-tooltip="true" />
        <el-table-column label="所属专业" align="center" prop="majorName" :show-overflow-tooltip="true" />
        <el-table-column label="年级" align="center" prop="grade" />
        <el-table-column label="班主任" align="center" prop="advisorName" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              type="primary"
              link
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['education:class:edit']"
            >修改</el-button>
            <el-button
              type="primary"
              link
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['education:class:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 添加或修改班级对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="classForm" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入班级名称" />
          </el-form-item>
          <el-form-item label="班级编码" prop="code">
            <el-input v-model="form.code" placeholder="请输入班级编码" />
          </el-form-item>
          <el-form-item label="所属专业" prop="majorId">
            <el-select v-model="form.majorId" placeholder="请选择所属专业" style="width: 100%">
              <el-option
                v-for="(option, index) in majorOptions"
                :key="index"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="年级" prop="grade">
            <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
              <el-option
                v-for="(grade, index) in gradeOptions"
                :key="index"
                :label="grade"
                :value="grade"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="班主任" prop="advisorId">
            <el-select
              v-model="form.advisorId"
              filterable
              remote
              placeholder="请输入教师姓名搜索"
              :remote-method="remoteTeacherMethod"
              :loading="teacherLoading"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="(teacher, index) in teacherOptions"
                :key="index"
                :label="teacher.label"
                :value="teacher.value"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassList, getClassDetail, addClass, updateClass, deleteClass } from '@/api/education/class'
import { listMajor, getAllMajors, getMajorOptions as fetchMajorOptions } from '@/api/education/major'
import { listTeachers, getAllTeachers } from '@/api/education/teacher'

// 遮罩层
const loading = ref(false)
// 选中数组
const ids = ref([])
// 非单个禁用
const single = computed(() => ids.value.length !== 1)
// 非多个禁用
const multiple = computed(() => ids.value.length === 0)
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 班级表格数据
const classList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 专业选项
const majorOptions = ref([])
// 年级选项 - 确保是完整的年级数据
const gradeOptions = ref(['2020', '2021', '2022', '2023', '2024', '2025'])
// 教师选项
const teacherOptions = ref([])
// 教师加载状态
const teacherLoading = ref(false)

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  code: undefined,
  majorId: undefined,
  grade: undefined
})

// 表单参数
const form = ref({
  id: undefined,
  name: undefined,
  code: undefined,
  majorId: undefined,
  grade: undefined,
  advisorId: undefined
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: "班级名称不能为空", trigger: "blur" }],
  code: [{ required: true, message: "班级编码不能为空", trigger: "blur" }],
  majorId: [{ required: true, message: "所属专业不能为空", trigger: "change" }],
  grade: [{ required: true, message: "年级不能为空", trigger: "change" }]
}

// 表单引用
const classForm = ref(null)

/** 查询班级列表 */
function getList() {
  loading.value = true
  getClassList(queryParams.value).then(response => {
    classList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 查询专业列表 */
function getMajorOptions() {
  // 初始化为空数组，防止undefined
  majorOptions.value = [];
  
  // 先尝试使用专用的选项接口
  fetchMajorOptions().then(response => {
    try {
      if (response.code === 200 && response.data && response.data.length > 0) {
        // 确保数据格式为{value,label}并过滤无效数据
    majorOptions.value = response.data
          .filter(item => item && typeof item === 'object' && item.value !== undefined && item.label !== undefined)
          .map(item => ({
            value: item.value,
            label: item.label
          }));
        console.log('获取专业选项成功:', majorOptions.value);
      } else {
        // 如果专用接口失败，回退到获取所有专业接口
        getAllMajors().then(res => {
          if (res.code === 200 && res.data) {
            // 转换数据格式为{value,label}并过滤无效数据
            majorOptions.value = res.data
              .filter(item => item && typeof item === 'object' && item.id !== undefined && item.name !== undefined)
              .map(item => ({
                value: item.id,
                label: item.name
              }));
            console.log('获取专业列表成功(已转换):', majorOptions.value);
          } else {
            console.error('获取专业列表失败:', res);
            majorOptions.value = [];
          }
        }).catch(err => {
          console.error('获取专业列表异常:', err);
          majorOptions.value = [];
        });
      }
    } catch (err) {
      console.error('处理专业数据时出错:', err);
      majorOptions.value = [];
    }
  }).catch(error => {
    console.error('获取专业选项异常，尝试备用方法:', error);
    // 异常时也回退到获取所有专业接口
    getAllMajors().then(res => {
      try {
        if (res.code === 200 && res.data) {
          // 转换数据格式为{value,label}并过滤无效数据
          majorOptions.value = res.data
            .filter(item => item && typeof item === 'object' && item.id !== undefined && item.name !== undefined)
            .map(item => ({
              value: item.id,
              label: item.name
            }));
        } else {
          majorOptions.value = [];
        }
      } catch (err) {
        console.error('处理专业数据时出错:', err);
        majorOptions.value = [];
      }
    }).catch(() => {
      majorOptions.value = [];
    });
  });
}

/** 搜索教师 */
function remoteTeacherMethod(query) {
  if (query !== '') {
    teacherLoading.value = true;
    listTeachers({ name: query }).then(response => {
      try {
        if (response.code === 200 && response.data) {
          // 确保数据格式正确且过滤无效数据
          teacherOptions.value = (response.data || [])
            .filter(item => item && typeof item === 'object' && 
                   (item.value !== undefined || item.id !== undefined) && 
                   (item.label !== undefined || item.name !== undefined))
            .map(item => ({
              value: item.value || item.id,
              label: item.label || item.name
            }));
          console.log('搜索教师结果:', teacherOptions.value);
        } else {
          teacherOptions.value = [];
        }
      } catch (err) {
        console.error('处理教师搜索数据时出错:', err);
        teacherOptions.value = [];
      } finally {
        teacherLoading.value = false;
      }
    }).catch(error => {
      console.error("搜索教师失败:", error);
      teacherOptions.value = [];
      teacherLoading.value = false;
    });
  } else {
    // 如果查询为空，则显示预加载的教师列表
    preloadTeachers();
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    code: undefined,
    majorId: undefined,
    grade: undefined
  }
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  title.value = "添加班级"
  open.value = true
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getClassDetail(id).then(response => {
    try {
      if (response.code === 200 && response.data) {
        form.value = response.data
        
        // 如果有班主任信息，确保添加到教师选项中
        if (form.value.advisorId && form.value.advisorName) {
          // 确保教师选项数组已初始化
          if (!teacherOptions.value || !Array.isArray(teacherOptions.value)) {
            teacherOptions.value = [];
          }
          
          // 避免使用some方法，直接查找已有教师
          let teacherExists = false;
          for (const teacher of teacherOptions.value) {
            if (teacher && typeof teacher === 'object' && teacher.value === form.value.advisorId) {
              teacherExists = true;
              break;
            }
          }
          
          // 如果教师不存在，则添加到选项中
          if (!teacherExists) {
            teacherOptions.value.push({
              value: form.value.advisorId,
              label: form.value.advisorName
            });
          }
        }
        
    title.value = "修改班级"
    open.value = true
      } else {
        ElMessage.error(response.msg || "获取班级详情失败")
      }
    } catch (err) {
      console.error('处理班级详情数据时出错:', err);
      ElMessage.error("处理班级详情数据失败");
    }
  }).catch(error => {
    console.error("获取班级详情失败:", error)
    ElMessage.error("获取班级详情失败，请稍后重试")
  })
}

/** 提交按钮 */
function submitForm() {
  classForm.value?.validate(valid => {
    if (valid) {
      try {
        // 确保表单中的数据类型正确
        const formData = {
          ...form.value,
          id: form.value.id ? Number(form.value.id) : undefined,
          majorId: form.value.majorId ? Number(form.value.majorId) : undefined,
          advisorId: form.value.advisorId ? Number(form.value.advisorId) : null
        };
        
        console.log("提交表单数据:", formData);
        
        if (formData.id) {
          updateClass(formData).then(response => {
            if (response.code === 200) {
          ElMessage.success("修改成功")
          open.value = false
          getList()
            } else {
              ElMessage.error(response.msg || "修改失败")
            }
          }).catch(error => {
            console.error("修改班级失败:", error)
            ElMessage.error("修改失败，请稍后重试")
        })
      } else {
          addClass(formData).then(response => {
            if (response.code === 200) {
          ElMessage.success("新增成功")
          open.value = false
          getList()
            } else {
              ElMessage.error(response.msg || "新增失败")
            }
          }).catch(error => {
            console.error("新增班级失败:", error)
            ElMessage.error("新增失败，请稍后重试")
          })
        }
      } catch (error) {
        console.error("表单提交数据处理错误:", error);
        ElMessage.error("表单提交失败，请稍后重试");
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const classIds = row.id || ids.value
  ElMessageBox.confirm('确认删除选中的班级信息?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteClass(classIds).then(() => {
      ElMessage.success("删除成功")
      getList()
    })
  })
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    code: undefined,
    majorId: undefined,
    grade: undefined,
    advisorId: undefined
  }
  classForm.value?.resetFields()
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

// 在组件挂载时预加载一些教师数据
function preloadTeachers() {
  teacherLoading.value = true
  
  // 初始化教师选项为空数组，防止初始化阶段引用null
  teacherOptions.value = [];
  
  getAllTeachers().then(response => {
    try {
      if (response.code === 200 && response.data) {
        // 直接使用返回的教师数据，确保数据格式正确并过滤掉无效数据
        teacherOptions.value = response.data
          .filter(item => item && (item.value || item.id))
          .map(item => ({
            value: item.value || item.id, 
            label: item.label || item.name
          }));
        console.log('预加载教师数据成功:', teacherOptions.value);
      }
    } catch (err) {
      console.error('处理教师数据时出错:', err);
    } finally {
      teacherLoading.value = false;
    }
  }).catch(error => {
    console.error("获取教师列表失败:", error);
    teacherLoading.value = false;
  });
}

onMounted(() => {
  getList()
  getMajorOptions()
  preloadTeachers()
  
  // 添加调试代码
  console.log('年级选项数据:', gradeOptions.value)
  // 延迟检查专业选项是否加载
  setTimeout(() => {
    console.log('专业选项加载后:', majorOptions.value)
    // 手动触发一次更新
    majorOptions.value = [...majorOptions.value]
  }, 1000)
})
</script> 