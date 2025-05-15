# 高校学生信息管理系统 - 前端

## 项目介绍

本项目是一个基于Vue 3 + Element Plus开发的高校学生信息管理系统前端。系统具有良好的用户体验和丰富的功能，主要实现了用户管理、院系管理、专业管理、班级管理、学生管理、教师管理、课程管理和考勤管理等功能。

## 技术栈

- **前端框架**: Vue 3
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **HTTP客户端**: Axios
- **CSS预处理器**: SCSS
- **图表库**: ECharts

## 项目结构

```
├── public             # 静态资源
├── src                # 源代码
│   ├── api            # API请求
│   ├── assets         # 主题、字体、图片等静态资源
│   ├── components     # 全局公用组件
│   ├── directives     # 全局指令
│   ├── layout         # 全局布局
│   ├── router         # 路由
│   ├── stores         # 全局状态管理
│   ├── utils          # 全局工具函数
│   ├── views          # 页面
│   ├── App.vue        # 入口页面
│   └── main.js        # 入口文件
├── .env.development   # 开发环境配置
├── .env.production    # 生产环境配置
├── package.json       # 项目依赖
└── vite.config.js     # Vite配置
```

## 功能模块

1. **系统管理**
   - 用户管理
   - 角色管理
   - 菜单管理

2. **教务管理**
   - 院系管理
   - 专业管理
   - 班级管理
   - 学生管理
   - 教师管理

3. **课程管理**
   - 课程列表
   - 课程安排

4. **考勤管理**
   - 考勤记录
   - 考勤统计

## 开发指南

### 环境准备

- Node.js >= 16.0.0
- npm >= 7.0.0

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 代码格式校验

```bash
npm run lint
```

### 权限指令使用最佳实践

在Vue3中，指令（如`v-hasPerms`）应该只应用在原生DOM元素上，而不是组件上。对于组件（如Element Plus的组件），应使用以下方法之一：

1. 使用`v-if`和权限检查函数：

```vue
<!-- 推荐用法 -->
<el-dropdown-item v-if="hasPermission('system:user:edit')">编辑</el-dropdown-item>

<script setup>
import { useUserStore } from '@/stores/user'

// 权限检查函数
const hasPermission = (permission) => {
  const userStore = useUserStore()
  const permissions = userStore.permissions
  
  if (typeof permission === 'string') {
    return permissions.includes(permission)
  }
  
  if (permission instanceof Array && permission.length > 0) {
    return permissions.some(p => permission.includes(p))
  }
  
  return false
}
</script>
```

2. 或者将组件包裹在原生DOM元素中，并在该元素上使用指令：

```vue
<!-- 另一种可行方案 -->
<div v-hasPerms="['system:user:edit']">
  <el-dropdown-item>编辑</el-dropdown-item>
</div>
```

避免直接在组件上使用指令，这可能会导致警告：`Runtime directive used on component with non-element root node. The directives will not function as intended.`

## 项目约定

1. 组件命名采用PascalCase
2. 文件夹和普通文件采用kebab-case
3. API方法命名规则为：动词 + 名词，例如：getStudentList
4. API函数封装在对应的模块下
5. 使用ESLint保证代码质量和风格统一

## 浏览器支持

- Chrome
- Firefox
- Safari
- Edge

## 开发团队

感谢所有为本项目做出贡献的开发人员！ 