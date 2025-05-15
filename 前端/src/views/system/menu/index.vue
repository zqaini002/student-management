<template>
  <div class="menu-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="停用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">菜单列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd" v-hasPerms="['system:menu:add']">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button type="success" @click="toggleExpandAll">
              <el-icon><Sort /></el-icon>{{ isExpandAll ? '折叠' : '展开' }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        ref="menuTableRef"
        v-loading="loading"
        :data="menuList"
        row-key="id"
        :tree-props="{ children: 'children' }"
        stripe
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="menuName" label="菜单名称" width="220" :show-overflow-tooltip="true" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="scope">
            <el-icon v-if="scope.row.icon && scope.row.icon !== '#'"><component :is="scope.row.icon" /></el-icon>
            <span v-else-if="scope.row.icon === '#'">-</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column prop="perms" label="权限标识" width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="path" label="路由地址" width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="component" label="组件路径" width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="menuType" label="类型" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.menuType === 'M'" type="danger">目录</el-tag>
            <el-tag v-else-if="scope.row.menuType === 'C'" type="primary">菜单</el-tag>
            <el-tag v-else-if="scope.row.menuType === 'F'" type="success">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="可见" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.visible === 0" type="success">显示</el-tag>
            <el-tag v-else type="info">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="handleAdd(scope.row)" 
              v-if="scope.row.menuType !== 'F'"
              v-hasPerms="['system:menu:add']"
            >
              新增
            </el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleUpdate(scope.row)" 
              v-hasPerms="['system:menu:edit']"
            >
              修改
            </el-button>
            <el-button 
              type="danger" 
              link 
              @click="handleDelete(scope.row)" 
              v-hasPerms="['system:menu:remove']"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑菜单对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="menuFormRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            value-key="id"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon" v-if="form.menuType !== 'F'">
          <icon-select v-model="form.icon" @change="handleIconChange" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" v-if="form.menuType !== 'F'">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
          <div class="form-tip" v-if="form.menuType === 'M'">
            <i class="el-icon-info"></i>
            <span>目录请输入路由地址，如：/system</span>
          </div>
          <div class="form-tip" v-else-if="form.menuType === 'C'">
            <i class="el-icon-info"></i>
            <span>菜单请输入路由地址，如：user</span>
          </div>
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.menuType === 'C'">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
          <div class="form-tip">
            <i class="el-icon-info"></i>
            <span>组件路径格式如：system/user/index</span>
          </div>
        </el-form-item>
        <el-form-item label="权限标识" prop="perms" v-if="form.menuType !== 'M'">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
          <div class="form-tip" v-if="form.menuType === 'F'">
            <i class="el-icon-info"></i>
            <span>按钮权限标识格式如：system:user:add</span>
          </div>
          <div class="form-tip" v-else>
            <i class="el-icon-info"></i>
            <span>菜单权限标识格式如：system:user:list</span>
          </div>
        </el-form-item>
        <el-form-item label="菜单状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="显示状态" prop="visible" v-if="form.menuType !== 'F'">
          <el-radio-group v-model="form.visible">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Plus,
  Sort
} from '@element-plus/icons-vue'
import { 
  getMenuList, 
  getMenuDetail, 
  addMenu, 
  updateMenu, 
  deleteMenu,
  getMenuTree
} from '@/api/menu'
import IconSelect from '@/components/IconSelect/index.vue'

// 定义数据变量
const menuList = ref([])              // 表格显示的当前数据
const expandedMenuData = ref([])      // 完全展开状态的菜单数据（带children的完整树结构）
const collapsedMenuData = ref([])     // 完全折叠状态的菜单数据（顶层项目，无子菜单）
const loading = ref(false)
const menuTableRef = ref(null)
const isExpandAll = ref(true)         // 是否展开全部菜单
const currentRow = ref(null)          // 当前选中的行
const queryParams = reactive({
  menuName: '',
  status: null
})

// 菜单树选项
const menuOptions = ref([])

// 表单相关
const menuFormRef = ref(null)
const dialog = reactive({
  visible: false,
  title: '',
  type: '' // add or update
})
const form = reactive({
  id: undefined,
  menuId: undefined,
  parentId: 0,
  menuName: '',
  icon: '',
  menuType: 'M',
  orderNum: 0,
  path: '',
  component: '',
  perms: '',
  visible: 0,
  status: 0,
  isFrame: 1,
  isCache: 0,
  query: '',
  remark: ''
})

// 表单验证规则
const rules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示排序', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ],
  perms: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

// 监听菜单类型变化，重置相关字段
watch(() => form.menuType, (val) => {
  if (val === 'F') {
    form.path = ''
    form.component = ''
    form.icon = ''
    form.visible = 0
    // 针对按钮类型，移除路径和组件必填校验
    if (menuFormRef.value) {
      menuFormRef.value.clearValidate(['path', 'component'])
    }
  } else if (val === 'M') {
    form.component = ''
    // 针对目录类型，移除组件必填校验
    if (menuFormRef.value) {
      menuFormRef.value.clearValidate(['component'])
    }
  } else if (val === 'C') {
    // 针对菜单类型，确保路径和组件的校验
    nextTick(() => {
      if (menuFormRef.value) {
        menuFormRef.value.validateField(['path', 'component'])
      }
    })
  }
})

// 初始化数据
onMounted(() => {
  // 读取本地存储中的展开状态
  const savedState = localStorage.getItem('menuExpandState');
  if (savedState !== null) {
    isExpandAll.value = savedState === 'true';
    console.log('初始化读取保存的展开状态:', isExpandAll.value);
  }
  
  // 获取菜单列表
  getList();
  
  // 监听窗口大小变化，以便在窗口调整大小后重新渲染表格
  window.addEventListener('resize', () => {
    if (menuTableRef.value) {
      menuTableRef.value.doLayout();
    }
  });
});

// 组件销毁时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', () => {});
})

// 获取菜单列表
const getList = () => {
  loading.value = true;
  getMenuList(queryParams)
    .then(res => {
      // 记录原始数据格式
      console.log('原始菜单数据:', JSON.stringify(res.data?.slice(0, 3)));
      
      if (!res.data || !Array.isArray(res.data)) {
        console.warn('获取到的菜单数据为空或格式不正确');
        menuList.value = expandedMenuData.value = collapsedMenuData.value = [];
        return;
      }

      try {
        // 处理菜单数据，构建树形结构
        const treeData = processMenuData(res.data);
        console.log('处理后的树形结构:', JSON.stringify(treeData?.slice(0, 2)));
        
        // 保存两种状态的数据
        // 1. 完整的树结构 - 用于展开状态
        expandedMenuData.value = JSON.parse(JSON.stringify(treeData));
        
        // 2. 仅顶层节点的树结构 - 用于折叠状态（去掉children）
        collapsedMenuData.value = treeData.map(item => {
          const { children, ...rest } = item;
          return { ...rest };
        });
        
        // 根据当前展开/折叠状态选择使用哪组数据
        if (isExpandAll.value) {
          // 展开状态：使用完整树结构
          console.log('应用展开状态数据');
          menuList.value = JSON.parse(JSON.stringify(expandedMenuData.value));
        } else {
          // 折叠状态：使用没有children的顶层菜单
          console.log('应用折叠状态数据');
          menuList.value = JSON.parse(JSON.stringify(collapsedMenuData.value));
        }
        
        // 获取菜单选项（用于下拉选择）- 确保总是使用完整的树结构
        getMenuOptions();
      } catch (error) {
        console.error('处理菜单数据时出错:', error);
        ElMessage.error('处理菜单数据时出错');
        menuList.value = expandedMenuData.value = collapsedMenuData.value = [];
      }
    })
    .catch(err => {
      console.error('获取菜单列表失败:', err);
      ElMessage.error('获取菜单列表失败');
      menuList.value = expandedMenuData.value = collapsedMenuData.value = [];
    })
    .finally(() => {
      loading.value = false;
    });
};

// 处理菜单数据，确保数据一致性并构建树形结构
const processMenuData = (menus) => {
  if (!Array.isArray(menus)) {
    console.warn('菜单数据不是数组类型');
    return [];
  }
  
  // 第一步：处理每个菜单项，确保ID一致性
  const processedMenus = menus.map(menu => {
    if (!menu) return null;
    
    // 创建菜单对象的副本
    const processedMenu = { ...menu };
    
    // 确保id和menuId一致，优先使用id
    if (processedMenu.id) {
      // 如果有id，确保menuId与id一致
      processedMenu.menuId = processedMenu.id;
    } else if (processedMenu.menuId) {
      // 如果没有id但有menuId，以menuId为准
      processedMenu.id = processedMenu.menuId;
    } else {
      // 如果都没有，生成一个唯一ID
      const uniqueId = 'temp_menu_' + Date.now() + '_' + Math.floor(Math.random() * 10000);
      processedMenu.id = uniqueId;
      processedMenu.menuId = uniqueId;
      console.warn('菜单缺少ID，生成临时ID:', uniqueId);
    }
    
    // 确保parentId为数值类型
    if (processedMenu.parentId) {
      processedMenu.parentId = Number(processedMenu.parentId);
    } else {
      // 如果parentId不存在，默认为0（顶级菜单）
      processedMenu.parentId = 0;
    }
    
    // 处理图标
    if (!processedMenu.icon || processedMenu.icon === '#') {
      processedMenu.icon = '';
    }
    
    // 初始化children数组
    processedMenu.children = [];
    
    return processedMenu;
  }).filter(Boolean); // 过滤掉null或undefined
  
  // 第二步：构建树形结构
  const menuMap = {};
  const rootMenus = [];
  
  // 创建以ID为键的菜单映射
  processedMenus.forEach(menu => {
    menuMap[menu.id] = menu;
  });
  
  // 将每个菜单放到其父菜单的children数组中
  processedMenus.forEach(menu => {
    // 顶级菜单
    if (menu.parentId === 0 || !menu.parentId) {
      rootMenus.push(menu);
    } 
    // 子菜单
    else {
      const parentMenu = menuMap[menu.parentId];
      if (parentMenu) {
        // 检查是否已存在相同ID的子菜单（避免重复）
        const existingIndex = parentMenu.children.findIndex(child => child.id === menu.id);
        if (existingIndex === -1) {
          parentMenu.children.push(menu);
        } else {
          console.warn(`菜单 ${menu.menuName}(ID: ${menu.id}) 在父菜单 ${parentMenu.menuName} 中重复`);
        }
      } else {
        // 如果找不到父菜单，则作为顶级菜单
        console.warn(`菜单 ${menu.menuName}(ID: ${menu.id}) 的父菜单(ID: ${menu.parentId})不存在，作为顶级菜单处理`);
        rootMenus.push(menu);
      }
    }
  });
  
  // 排序：根据orderNum字段排序
  const sortMenus = (menus) => {
    menus.sort((a, b) => (a.orderNum || 0) - (b.orderNum || 0));
    menus.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        sortMenus(menu.children);
      }
    });
    return menus;
  };
  
  return sortMenus(rootMenus);
};

// 获取菜单选项
const getMenuOptions = () => {
  getMenuTree()
    .then(res => {
      try {
        const rootMenu = { id: 0, menuId: 0, menuName: '主目录', children: [] }
        // 判断是否为数组且不为空
        if (Array.isArray(res.data) && res.data.length > 0) {
          // 处理获取到的菜单树数据
          rootMenu.children = processMenuData(res.data)
        } else {
          console.warn('获取到的菜单树为空或格式不正确')
        }
        menuOptions.value = [rootMenu]
      } catch (error) {
        console.error('处理菜单选项失败:', error)
        ElMessage.error('处理菜单选项失败')
        menuOptions.value = [{ id: 0, menuId: 0, menuName: '主目录', children: [] }]
      }
    })
    .catch(err => {
      console.error('获取菜单选项失败:', err)
      ElMessage.error('获取菜单选项失败')
      menuOptions.value = [{ id: 0, menuId: 0, menuName: '主目录', children: [] }]
    })
}

// 查询按钮
const handleQuery = () => {
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.menuName = ''
  queryParams.status = null
  handleQuery()
}

// 展开/折叠按钮功能
const toggleExpandAll = () => {
  // 更新状态
  isExpandAll.value = !isExpandAll.value;
  
  // 保存用户偏好到本地存储
  localStorage.setItem('menuExpandState', isExpandAll.value.toString());
  
  console.log(`执行${isExpandAll.value ? '展开' : '折叠'}全部菜单操作`);
  
  try {
    // 根据当前状态切换数据源
    if (isExpandAll.value) {
      // 展开：使用带有children的完整树结构
      console.log('切换到展开状态数据');
      menuList.value = JSON.parse(JSON.stringify(expandedMenuData.value));
    } else {
      // 折叠：使用没有children的顶层菜单
      console.log('切换到折叠状态数据');
      menuList.value = JSON.parse(JSON.stringify(collapsedMenuData.value));
    }
    
    // 刷新表格布局
    nextTick(() => {
      if (menuTableRef.value) {
        menuTableRef.value.doLayout();
      }
    });
    
    // 提示用户
    ElMessage.success(isExpandAll.value ? '已展开全部菜单' : '已折叠全部菜单');
  } catch (error) {
    console.error('切换展开/折叠状态失败:', error);
    ElMessage.error('操作失败，请重试');
  }
};

// 新增按钮
const handleAdd = (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '新增菜单'
  dialog.type = 'add'
  
  if (row && row.menuId) {
    form.parentId = row.menuId
    // 如果父菜单是目录类型，默认新增为菜单类型
    if (row.menuType === 'M') {
      form.menuType = 'C'
    } 
    // 如果父菜单是菜单类型，默认新增为按钮类型
    else if (row.menuType === 'C') {
      form.menuType = 'F'
    }
  }
  
  // 确保在对话框完全打开后再执行后续操作
  nextTick(() => {
    // 重置表单验证
    if (menuFormRef.value) {
      menuFormRef.value.clearValidate()
    }
  })
}

// 编辑按钮
const handleUpdate = (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '编辑菜单'
  dialog.type = 'update'
  
  // 确保使用正确的id
  const menuId = row.id || row.menuId
  if (!menuId) {
    ElMessage.error('菜单ID不存在')
    return
  }
  
  getMenuDetail(menuId)
    .then(res => {
      if (res.data) {
        const data = res.data
        // 确保数值类型字段为数值
        if (data.parentId) data.parentId = Number(data.parentId)
        if (data.orderNum) data.orderNum = Number(data.orderNum)
        if (data.status) data.status = Number(data.status)
        if (data.visible) data.visible = Number(data.visible)
        
        // 确保id正确
        form.menuId = data.id || data.menuId
        Object.assign(form, data)
      } else {
        ElMessage.warning('获取菜单详情数据为空')
      }
    })
    .catch(err => {
      console.error('获取菜单详情失败:', err)
      ElMessage.error('获取菜单详情失败')
    })
}

// 删除按钮
const handleDelete = (row) => {
  // 判断是否有子菜单
  if (row.children && row.children.length > 0) {
    ElMessage.warning('存在子菜单，不允许删除')
    return
  }
  
  ElMessageBox.confirm(`确定要删除菜单 ${row.menuName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteMenu(row.menuId)
      .then(() => {
        ElMessage.success('删除成功')
        getList()
      })
      .catch(err => {
        console.error('删除菜单失败:', err)
        ElMessage.error('删除菜单失败')
      })
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  menuFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    // 创建表单数据副本
    const formData = {...form}
    
    // 根据菜单类型进行字段验证
    if (formData.menuType === 'F') {
      // 按钮类型必须设置权限标识
      if (!formData.perms) {
        ElMessage.error('按钮类型菜单必须设置权限标识')
        return
      }
    } else if (formData.menuType === 'M' || formData.menuType === 'C') {
      // 目录和菜单类型必须设置路由地址
      if (!formData.path) {
        ElMessage.error(`${formData.menuType === 'M' ? '目录' : '菜单'}类型必须设置路由地址`)
        return
      }
      
      // 菜单类型必须设置组件路径
      if (formData.menuType === 'C' && !formData.component) {
        ElMessage.error('菜单类型必须设置组件路径')
        return
      }
    }
    
    // 确保数值类型字段为数值
    formData.orderNum = Number(formData.orderNum || 0)
    formData.parentId = Number(formData.parentId || 0)
    formData.status = Number(formData.status || 0)
    formData.visible = Number(formData.visible || 0)
    formData.isFrame = Number(formData.isFrame || 1)
    formData.isCache = Number(formData.isCache || 0)
    
    // 确保路由地址以/开头
    if ((formData.menuType === 'M' || formData.menuType === 'C') && 
        formData.path && !formData.path.startsWith('/')) {
      formData.path = '/' + formData.path
    }
    
    // 确保id一致性
    if (dialog.type === 'update' && formData.menuId) {
      formData.id = formData.menuId
    }
    
    if (dialog.type === 'add') {
      addMenu(formData)
        .then(() => {
          ElMessage.success('添加成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('添加菜单失败:', err)
          ElMessage.error('添加菜单失败: ' + (err.message || '未知错误'))
        })
    } else {
      updateMenu(formData)
        .then(() => {
          ElMessage.success('修改成功')
          dialog.visible = false
          getList()
        })
        .catch(err => {
          console.error('修改菜单失败:', err)
          ElMessage.error('修改菜单失败: ' + (err.message || '未知错误'))
        })
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.menuId = undefined
  form.parentId = 0
  form.menuName = ''
  form.icon = ''
  form.menuType = 'M'
  form.orderNum = 0
  form.path = ''
  form.component = ''
  form.perms = ''
  form.visible = 0
  form.status = 0
  form.isFrame = 1
  form.isCache = 0
  form.query = ''
  form.remark = ''
  
  // 如果表单对象存在，重置校验
  if (menuFormRef.value) {
    menuFormRef.value.resetFields()
  }
}

// 处理图标选择变化
const handleIconChange = (icon) => {
  form.icon = icon
}
</script>

<style lang="scss" scoped>
.menu-container {
  padding: 16px;
  
  .search-card {
    margin-bottom: 16px;
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      
      .el-form-item {
        margin-right: 10px;
        margin-bottom: 15px;
      }
    }
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-title {
        font-size: 16px;
        font-weight: 600;
      }
      
      .header-buttons {
        display: flex;
        gap: 8px;
      }
    }
  }
  
  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    display: flex;
    align-items: center;
    
    i {
      margin-right: 4px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style> 