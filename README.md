# 🎓 学生信息管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![JDK](https://img.shields.io/badge/JDK-17%2B-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

> 2025大三下帮助室友完成他的作业手搓完成的基于Spring Boot + Spring Security + JWT + MyBatis-Plus的完整学生信息管理系统

## 📑 目录

- [项目介绍](#-项目介绍)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [功能模块](#-功能模块)
- [环境要求](#-环境要求)
- [运行说明](#-运行说明)
- [API文档](#-api文档)
- [前端项目](#-前端项目)
- [主要数据表结构](#-主要数据表结构)
- [系统角色](#-系统角色)
- [常见问题](#-常见问题)
- [开发团队](#-开发团队)

## 📋 项目介绍
本项目是一个基于Spring Boot + Spring Security + JWT + MyBatis-Plus的学生信息管理系统，提供完整的学生信息管理功能，包括用户认证、学生管理、课程管理、成绩管理等。

## 🔧 技术栈
- 🍃 Spring Boot 3.1.5
- 🔒 Spring Security 6.1.5
- 🔑 JWT (JSON Web Token)
- ⚡ MyBatis-Plus 3.5.3.2
- 🐬 MySQL 8.0
- 🧮 Redis 缓存
- 📝 Knife4j 4.3.0 (基于Swagger的API文档)
- 🧰 Lombok
- 🛠️ Hutool 工具集
- 📊 EasyExcel (Excel导入导出)

## 📂 项目结构
```
student-management-system
├── config - 配置类
│   ├── Knife4jConfig.java - API文档配置
│   ├── SecurityConfig.java - 安全配置
│   ├── RedisConfig.java - Redis配置
│   └── WebMvcConfig.java - Web配置
├── controller - 控制器
│   ├── AdminController.java - 管理员控制器
│   ├── AuthController.java - 认证控制器
│   ├── StudentController.java - 学生控制器
│   ├── TeacherController.java - 教师控制器
│   └── ... - 其他控制器
├── service - 服务层
│   └── impl - 服务实现
├── mapper - 数据访问层
├── entity - 实体类
├── dto - 数据传输对象
├── vo - 视图对象
├── common - 公共类
│   ├── constant - 常量
│   ├── exception - 异常类
│   └── result - 统一响应结果
├── util - 工具类
├── filter - 过滤器
│   └── JwtAuthenticationFilter.java - JWT认证过滤器
├── handler - 处理器
├── security - 安全相关
│   ├── UserDetailsServiceImpl.java - 用户详情服务实现
│   └── ... - 其他安全组件
└── resources - 资源文件
    ├── mapper - Mapper XML文件
    ├── static - 静态资源
    │   └── uploads - 文件上传目录
    └── application.yml - 应用配置文件
```

## ✨ 功能模块
### 1️⃣ 用户认证与授权
   - 🔐 基于JWT的身份认证
   - 👮 基于RBAC的权限控制
   - 🚪 登录、注销、刷新令牌
   - 🔢 登录验证码支持
   - 👑 多角色权限管理
   - 🔒 账户锁定机制
   - 🛡️ 密码加密存储
   - 📝 登录日志记录
   - 🚨 异常登录检测

### 2️⃣ 学生管理
   - 📝 学生信息CRUD
   - 🔍 学生分页查询与筛选
   - 📤 导入导出学生信息
   - 🖼️ 学生头像上传
   - 📦 学生信息批量处理
   - 📁 学生档案管理
   - 📋 学籍变更记录
   - 🔎 多维度学生查询（按班级、专业、年级等）
   - ✅ 学生信息校验机制
   - 🔗 学生账号关联管理

### 3️⃣ 教师管理
   - 📝 教师信息CRUD
   - 🔍 教师分页查询
   - 🔗 教师与课程关联
   - 📜 教师资质管理
   - 📚 教师授课记录
   - 📊 教师绩效统计
   - 👑 教师角色分配
   - ⏱️ 教师课时统计
   - 🌟 教师考评管理
   - 🏢 部门与职称管理

### 4️⃣ 班级管理
   - 📝 班级信息CRUD
   - 👨‍👩‍👧‍👦 班级学生管理
   - 📊 班级统计信息
   - 👨‍🏫 班主任分配
   - 📅 班级课表管理
   - 📋 班级考勤统计
   - 📈 班级成绩分析
   - 📊 班级学生异动记录
   - 📢 班级公告管理
   - 🎯 班级活动记录

### 5️⃣ 课程管理
   - 📝 课程信息CRUD
   - 📋 课程选课管理
   - 📅 课程表生成
   - 📑 课程大纲管理
   - 📁 课程资源上传与下载
   - 🏷️ 课程类型管理
   - 🔢 学分与学时设置
   - ⏰ 课程开设时间管理
   - 🔢 课程容量控制
   - 🔄 先修课程关联
   - 🔀 跨专业选课支持

### 6️⃣ 成绩管理
   - 📝 成绩录入与修改
   - 📊 成绩统计与分析
   - 📃 成绩单生成
   - 📤 成绩导入导出
   - 📊 平时成绩与考试成绩分开管理
   - ✅ 成绩审核流程
   - 🏆 成绩排名计算
   - 🧮 不同成绩计算方式支持
   - 📈 GPA计算统计
   - 🔢 学分绩点换算
   - 📉 成绩趋势分析
   - 📊 成绩分布图表显示

### 7️⃣ 考勤管理
   - 📝 考勤记录添加与查询
   - 📊 考勤统计分析（出勤率、缺勤率等）
   - 🔍 学生个人考勤查询
   - 📈 考勤数据可视化（图表展示）
   - 📤 考勤记录导出
   - 📋 按课程、班级、日期维度统计展示
   - 🏷️ 多种考勤状态支持（出勤、缺勤、迟到、早退、请假）
   - 📊 教师可批量录入考勤
   - 👤 支持单个学生考勤记录维护
   - 📝 考勤记录备注功能

### 8️⃣ 系统管理
   - 👥 用户管理
   - 👑 角色管理
   - 📋 菜单管理
   - 🔒 权限管理

### 9️⃣ 通知公告
   - 📢 发布通知
   - 👁️ 查看通知

## 💻 环境要求
- ☕ JDK 17+
- 🐬 MySQL 8.0+
- 🧮 Redis 6.0+
- 🔧 Maven 3.8+

## 🚀 运行说明
1. 导入数据库脚本 `sql/student_management_system.sql`
2. 修改 `application.yml` 中的数据库和Redis配置
3. 运行 `StudentManagementSystemApplication.java`
4. 访问 Swagger 文档：http://localhost:8080/api/doc.html

## 📚 API文档
项目集成了Knife4j，启动后访问 http://localhost:8080/api/doc.html 查看API文档

<!-- 在这里放置API文档截图 -->
![API文档截图](docs/images/api-doc.png)

## 🖥️ 前端项目
前端使用Vue3 + ElementPlus开发，主要模块：
- 🔐 登录与认证
- 👤 个人中心
- 👨‍🎓 学生管理
- 📋 考勤管理
- 📊 成绩管理
- 📚 课程选择与查看

<!-- 在这里放置系统截图 -->
![系统截图](docs/images/system-screenshot.png)

## 💾 主要数据表结构
系统主要包含以下关键数据表：
- `sys_user`: 用户表，存储所有系统用户，包含账号、密码、状态等基本信息
- `sys_role`: 角色表，定义系统角色
- `sys_menu`: 菜单表，定义系统功能菜单
- `sys_user_role`: 用户角色关联表，实现多角色分配
- `sys_role_menu`: 角色菜单关联表，实现权限控制
- `student`: 学生表，关联用户表的user_id，存储学生详细信息
- `teacher`: 教师表，关联用户表的user_id，存储教师详细信息
- `class`: 班级表，存储班级信息
- `major`: 专业表，存储专业信息
- `department`: 院系表，存储院系信息
- `course`: 课程表，存储课程基本信息
- `course_offering`: 课程开设表，记录每学期开设的具体课程信息
- `course_selection`: 学生选课表，记录学生选课信息
- `attendance`: 考勤记录表，存储学生考勤数据
- `grade`: 成绩表，存储学生课程成绩
- `notice`: 通知公告表，存储系统通知
- `todo_item`: 待办事项表，存储用户待办事项

## 👥 系统角色
- 👨‍💼 管理员：拥有所有权限
- 👨‍🏫 教师：拥有课程、学生和考勤管理权限
- 👨‍🎓 学生：拥有查看课程、成绩和考勤的权限

## ❓ 常见问题

### 1. 关于头像上传与访问
- 📁 头像上传后保存在`E:/rzb/后端/uploads`目录下
- 🔗 通过`/api/uploads/**`路径可以访问上传的头像
- 🖼️ 前端使用`image.js`中的`formatImageUrl`函数处理头像URL
- 🗜️ 支持图片压缩和裁剪功能
- 📏 文件上传大小限制为5MB
- 🖼️ 支持jpg、png、jpeg等常见图片格式

### 2. Spring Security相关
- 🚫 未登录访问受保护资源会返回401错误
- 🔒 无权限访问会返回403错误
- 🔑 测试账号：
  - 管理员：admin/123456
  - 教师：teacher/123456
  - 学生：student/123456
- ⏱️ JWT令牌默认有效期为24小时
- 🔄 系统支持令牌刷新机制
- 💾 使用Redis存储在线用户会话信息
- 🔐 密码经过BCrypt加密存储

### 3. 考勤模块注意事项
- 🆔 考勤记录是按照`student_id`字段而非`user_id`查询的
- 👨‍🎓 学生前端访问自己考勤记录时，需要使用正确的学生ID
- 🔄 后端接口会自动将用户ID转换为对应的学生ID，确保学生只能查看自己的考勤记录
- 📊 考勤统计支持多种维度：出勤、缺勤、迟到、早退、请假
- 📈 前端展示包括数据表格和饼图可视化
- 🔒 考勤管理权限控制：教师可管理自己课程的考勤，学生只能查看自己的考勤
- 📊 支持多种统计维度：按课程统计、按班级统计、按日期统计
- 📤 考勤数据可导出，方便教师做后续处理
- 🔍 支持日期范围筛选，以及按学生姓名、学号等条件筛选
- 💾 考勤数据库结构设计合理，使用status字段标识不同考勤状态（0:出勤，1:缺勤，2:迟到，3:早退，4:请假）

### 4. 常见问题修复记录
- 🔧 修复了个人考勤页面数据不显示问题：调整了前端查询参数和后端接口逻辑，确保使用正确的学生ID而非用户ID查询
- 🔧 修复了成绩页面考试时间和排名不显示问题：更新SQL查询确保返回相关字段
- 🔧 修复了成绩构成列删除问题：清理前端代码中所有相关的成绩构成字段
- 🔧 修复了成绩趋势图和分布图数据显示问题：调整前端筛选条件
- 🔧 修复了学生选课时课程容量计算问题：优化了并发选课情况下的容量控制
- 🔧 修复了用户头像上传后不显示问题：调整了文件保存路径和访问URL配置
- 🔧 修复了教师查看课程学生列表分页问题：完善了分页参数处理
- 🔧 解决了Excel导入学生信息时的数据校验问题：增强了数据格式验证
- 🔧 修复了学生管理页面新增按钮跳转问题：统一路由格式，将'/student/add'改为'/student-add'，确保路径一致性
- 🔧 修复了教师管理新增页面返回按钮跳转问题：统一路由格式，将'/teacher/list'改为'/teacher-list'，解决了页面跳转404问题
- 🔧 修复了管理员考勤记录没有相关数据和权限问题

### 5. Knife4j文档访问
- 📚 访问地址：http://localhost:8080/api/doc.html
- 🆓 无需登录即可访问
- 🔍 支持在线调试API
- 📑 文档按功能模块分组
- 📝 接口参数和响应示例完善
- 🔐 包含认证接口的调用说明

### 6. 前后端交互说明
- 🌐 统一使用RESTful API风格
- 🔄 前端使用axios封装的请求工具
- 📦 响应数据统一格式：{code, message, data}
- 🔍 全局请求拦截器处理认证令牌
- 🚫 全局响应拦截器处理错误信息
- 🔄 支持请求取消和重试机制

### 7. 系统性能优化
- 🚀 Redis缓存热点数据
- 📑 分页查询优化
- ⚡ MyBatis-Plus性能配置
- 📊 批量操作优化
- 📤 大数据量导出优化
- 🖼️ 图片资源CDN加速

## 👨‍💻 开发团队
- 👑 七七（项目负责人）
- 👨‍💻 后端开发团队
- 👨‍🎨 前端开发团队
- 🔍 测试团队

---

<p align="center">© 2025 学生信息管理系统. 保留所有权利.</p>

<p align="center">
  <a href="#">
    <img src="https://img.shields.io/badge/回到顶部-↑-blue" alt="回到顶部" />
  </a>
</p>