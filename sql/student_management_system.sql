/*
 Navicat Premium Dump SQL

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : student_management_system

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 31/10/2025 23:31:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_offering_id` bigint NOT NULL COMMENT '课程开设ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `status` tinyint NOT NULL COMMENT '状态 0:出勤 1:缺勤 2:迟到 3:早退 4:请假',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_offering_id`(`course_offering_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_attendance_date`(`attendance_date` ASC) USING BTREE,
  CONSTRAINT `fk_attendance_offering` FOREIGN KEY (`course_offering_id`) REFERENCES `course_offering` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of attendance
-- ----------------------------
INSERT INTO `attendance` VALUES (1, 1, 1, '2023-09-11', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (2, 1, 2, '2023-09-11', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (3, 1, 3, '2023-09-11', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (4, 1, 5, '2023-09-11', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (5, 1, 1, '2023-09-13', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (6, 1, 2, '2023-09-13', 2, '迟到10分钟', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (7, 1, 3, '2023-09-13', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (8, 1, 5, '2023-09-13', 1, '无故缺勤', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (9, 1, 1, '2023-09-18', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (10, 1, 2, '2023-09-18', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (11, 1, 3, '2023-09-18', 4, '病假', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (12, 1, 5, '2023-09-18', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (13, 1, 1, '2023-09-20', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (14, 1, 2, '2023-09-20', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (15, 1, 3, '2023-09-20', 4, '病假', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (16, 1, 5, '2023-09-20', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (17, 2, 1, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (18, 2, 2, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (19, 2, 4, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (20, 2, 1, '2023-09-14', 3, '有事提前离开', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (21, 2, 2, '2023-09-14', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (22, 2, 4, '2023-09-14', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (23, 2, 1, '2023-09-19', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (24, 2, 2, '2023-09-19', 2, '迟到5分钟', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (25, 2, 4, '2023-09-19', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (26, 2, 1, '2023-09-21', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (27, 2, 2, '2023-09-21', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (28, 2, 4, '2023-09-21', 1, '无故缺勤', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (29, 4, 1, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (30, 4, 2, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (31, 4, 5, '2023-09-12', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (32, 4, 1, '2023-09-14', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (33, 4, 2, '2023-09-14', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (34, 4, 5, '2023-09-14', 2, '迟到15分钟', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (35, 4, 1, '2023-09-19', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (36, 4, 2, '2023-09-19', 4, '请假', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (37, 4, 5, '2023-09-19', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (38, 4, 1, '2023-09-21', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (39, 4, 2, '2023-09-21', 4, '请假', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (40, 4, 5, '2023-09-21', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (41, 10, 6, '2024-02-26', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (42, 10, 7, '2024-02-26', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (43, 10, 6, '2024-02-28', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (44, 10, 7, '2024-02-28', 2, '迟到8分钟', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (45, 10, 6, '2024-03-04', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (46, 10, 7, '2024-03-04', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (47, 10, 6, '2024-03-06', 4, '事假', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (48, 10, 7, '2024-03-06', 0, NULL, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `attendance` VALUES (49, 7, 5, '2025-06-04', 0, '', '2025-06-05 00:29:51', '2025-06-05 00:29:51');
INSERT INTO `attendance` VALUES (50, 7, 3, '2025-06-04', 0, '', '2025-06-05 00:29:51', '2025-06-05 00:29:51');
INSERT INTO `attendance` VALUES (51, 20, 9, '2025-06-04', 2, '', '2025-06-05 00:30:14', '2025-06-05 00:30:14');
INSERT INTO `attendance` VALUES (52, 4, 5, '2025-06-04', 0, '', '2025-06-05 00:32:16', '2025-06-05 00:32:16');
INSERT INTO `attendance` VALUES (53, 4, 2, '2025-06-04', 0, '', '2025-06-05 00:32:16', '2025-06-05 00:32:16');
INSERT INTO `attendance` VALUES (54, 4, 1, '2025-06-04', 0, '', '2025-06-05 00:32:16', '2025-06-05 00:32:16');
INSERT INTO `attendance` VALUES (55, 2, 4, '2025-06-04', 1, '', '2025-06-05 00:36:14', '2025-06-05 00:36:14');
INSERT INTO `attendance` VALUES (56, 2, 2, '2025-06-04', 2, '', '2025-06-05 00:36:14', '2025-06-05 00:36:14');
INSERT INTO `attendance` VALUES (57, 2, 1, '2025-06-04', 0, '', '2025-06-05 00:36:14', '2025-06-05 00:36:14');
INSERT INTO `attendance` VALUES (58, 6, 4, '2025-06-04', 2, '', '2025-06-05 00:38:30', '2025-06-05 00:38:30');
INSERT INTO `attendance` VALUES (59, 6, 2, '2025-06-04', 0, '', '2025-06-05 00:38:30', '2025-06-05 00:38:30');
INSERT INTO `attendance` VALUES (60, 10, 7, '2025-06-04', 1, '', '2025-06-05 00:40:01', '2025-06-05 00:40:01');
INSERT INTO `attendance` VALUES (61, 10, 6, '2025-06-04', 0, '', '2025-06-05 00:40:01', '2025-06-05 00:40:01');
INSERT INTO `attendance` VALUES (62, 1, 5, '2025-06-04', 1, '', '2025-06-05 00:40:53', '2025-06-05 00:40:53');
INSERT INTO `attendance` VALUES (63, 1, 3, '2025-06-04', 0, '', '2025-06-05 00:40:53', '2025-06-05 00:40:53');
INSERT INTO `attendance` VALUES (64, 1, 2, '2025-06-04', 0, '', '2025-06-05 00:40:53', '2025-06-05 00:40:53');
INSERT INTO `attendance` VALUES (65, 9, 5, '2025-06-04', 1, '', '2025-06-05 00:41:44', '2025-06-05 00:41:44');
INSERT INTO `attendance` VALUES (66, 1, 5, '2025-10-08', 4, '1', '2025-10-08 19:10:52', '2025-10-08 19:10:52');
INSERT INTO `attendance` VALUES (67, 1, 3, '2025-10-08', 1, '2', '2025-10-08 19:10:52', '2025-10-08 19:10:52');

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级编码',
  `major_id` bigint NOT NULL COMMENT '所属专业ID',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级',
  `advisor_id` bigint NULL DEFAULT NULL COMMENT '班主任ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_major_id`(`major_id` ASC) USING BTREE,
  INDEX `idx_advisor_id`(`advisor_id` ASC) USING BTREE,
  CONSTRAINT `fk_class_advisor` FOREIGN KEY (`advisor_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_class_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO `class` VALUES (1, '计算机科学2021级1班', 'CS21-1', 1, '2021', 6, '2025-05-15 02:59:39', '2025-06-19 02:29:51');
INSERT INTO `class` VALUES (2, '计算机科学2021级2班', 'CS21-2', 1, '2021', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (3, '软件工程2021级1班', 'SE21-1', 2, '2021', 3, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (4, '软件工程2021级2班', 'SE21-2', 2, '2021', 4, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (5, '人工智能2021级1班', 'AI21-1', 3, '2021', 5, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (6, '数据科学2021级1班', 'DS21-1', 4, '2021', 6, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (7, '应用数学2021级1班', 'AM21-1', 5, '2021', 7, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (8, '统计学2021级1班', 'STAT21-1', 6, '2021', 8, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (9, '英语2021级1班', 'ENG21-1', 7, '2021', 9, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `class` VALUES (10, '工商管理2021级1班', 'BUS21-1', 9, '2021', 10, '2025-05-15 02:59:39', '2025-05-15 02:59:39');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程编码',
  `credit` decimal(3, 1) NOT NULL COMMENT '学分',
  `hours` int NOT NULL COMMENT '学时',
  `type` tinyint NOT NULL COMMENT '课程类型 0:必修 1:选修',
  `department_id` bigint NOT NULL COMMENT '开课院系ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_course_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '高等数学', 'MATH101', 5.0, 80, 0, 2, '微积分与数学分析基础课程', '2025-05-15 02:59:39', '2025-06-19 02:20:40');
INSERT INTO `course` VALUES (2, '线性代数', 'MATH102', 3.0, 48, 0, 2, '线性代数基础理论课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (3, '概率论与数理统计', 'MATH201', 4.0, 64, 0, 2, '概率与统计学的基础理论课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (4, '程序设计基础(Java)', 'CS101', 4.0, 64, 0, 1, 'Java语言程序设计入门课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (5, '数据结构与算法', 'CS201', 4.0, 64, 0, 1, '数据结构与算法设计基础课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (6, '数据库原理与应用', 'CS301', 3.5, 56, 0, 1, '数据库理论及实践应用课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (7, '操作系统', 'CS302', 4.0, 64, 0, 1, '操作系统原理与设计课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (8, '计算机网络', 'CS303', 3.5, 56, 0, 1, '计算机网络基础理论课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (9, '软件工程导论', 'SE201', 3.0, 48, 0, 1, '软件工程基本概念与方法论', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (10, '人工智能基础', 'AI201', 3.5, 56, 0, 1, '人工智能基础理论与算法', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (11, '深度学习', 'AI301', 4.0, 64, 1, 1, '深度学习理论与实践应用', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (12, '数据挖掘', 'DS301', 3.5, 56, 1, 1, '数据挖掘技术与方法', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (13, '大数据处理技术', 'DS302', 4.0, 64, 1, 1, '大数据存储、处理与分析技术', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (14, '英语听说', 'ENG101', 2.0, 32, 0, 3, '英语听力与口语训练课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (15, '英语阅读与写作', 'ENG201', 2.0, 32, 0, 3, '英语阅读理解与写作技巧课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (16, '专业英语', 'ENG301', 2.0, 32, 1, 3, '计算机专业英语术语与文献阅读', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (17, '管理学原理', 'BUS101', 3.0, 48, 1, 4, '管理学基本原理与实践', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (18, '经济学原理', 'ECO101', 3.0, 48, 1, 4, '经济学基础理论课程', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (19, '数字电路', 'PE101', 3.5, 56, 1, 5, '数字电路设计与实现', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course` VALUES (20, '模拟电路', 'PE102', 3.5, 56, 1, 5, '模拟电路原理与设计', '2025-05-15 02:59:39', '2025-05-15 02:59:39');

-- ----------------------------
-- Table structure for course_offering
-- ----------------------------
DROP TABLE IF EXISTS `course_offering`;
CREATE TABLE `course_offering`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `semester` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `class_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上课时间',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上课地点',
  `exam_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试时间',
  `capacity` int NOT NULL COMMENT '容量',
  `selected_count` int NOT NULL DEFAULT 0 COMMENT '已选人数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:正常 1:已结课',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  CONSTRAINT `fk_offering_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_offering_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程开设表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_offering
-- ----------------------------
INSERT INTO `course_offering` VALUES (1, 1, 3, '2023-2024-1', '周一 1-2节, 周三 1-2节', '教学楼A-101', '2025-01-30 09:00-11:00', 60, 30, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (2, 2, 3, '2023-2024-1', '周二 3-4节, 周四 3-4节', '教学楼A-102', '2025-01-11 09:00-11:00', 60, 28, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (3, 3, 4, '2023-2024-1', '周一 5-6节, 周三 5-6节', '教学楼A-103', '2025-01-22 09:00-11:00', 60, 25, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (4, 4, 1, '2023-2024-1', '周二 7-8节, 周四 7-8节', '教学楼B-201', '2025-01-15 09:00-11:00', 50, 40, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (5, 5, 1, '2023-2024-1', '周一 3-4节, 周三 3-4节', '教学楼B-202', '2025-01-12 09:00-11:00', 50, 38, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (6, 6, 2, '2023-2024-1', '周二 5-6节, 周四 5-6节', '教学楼B-203', '2025-01-13 09:00-11:00', 50, 35, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (7, 7, 2, '2023-2024-1', '周一 7-8节, 周三 7-8节', '教学楼B-204', '2025-01-30 09:00-11:00', 50, 30, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (8, 8, 1, '2023-2024-1', '周二 1-2节, 周四 1-2节', '教学楼B-205', '2025-01-22 09:00-11:00', 50, 32, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (9, 9, 2, '2023-2024-1', '周五 1-4节', '教学楼B-206', '2025-01-16 09:00-11:00', 45, 25, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (10, 10, 5, '2023-2024-2', '周一 1-2节, 周三 1-2节', '教学楼C-301', '2025-06-16 09:00-11:00', 40, 20, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (11, 11, 5, '2023-2024-2', '周二 3-4节, 周四 3-4节', '教学楼C-302', '2025-06-30 09:00-11:00', 40, 18, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (12, 12, 6, '2023-2024-2', '周一 5-6节, 周三 5-6节', '教学楼C-303', '2025-06-11 09:00-11:00', 40, 15, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (13, 13, 6, '2023-2024-2', '周二 7-8节, 周四 7-8节', '教学楼C-304', '2025-06-24 09:00-11:00', 40, 12, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (14, 14, 5, '2023-2024-2', '周五 1-2节', '教学楼A-201', '2025-06-27 09:00-11:00', 30, 22, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (15, 15, 5, '2023-2024-2', '周五 3-4节', '教学楼A-202', '2025-06-2 09:00-11:00', 30, 20, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (16, 16, 6, '2023-2024-2', '周五 5-6节', '教学楼A-203', '2025-06-19 09:00-11:00', 30, 15, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (17, 17, 7, '2023-2024-2', '周一 1-2节, 周三 1-2节', '教学楼D-401', '2025-06-29 09:00-11:00', 35, 10, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (18, 18, 7, '2023-2024-2', '周二 3-4节, 周四 3-4节', '教学楼D-402', '2025-06-26 09:00-11:00', 35, 8, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (19, 19, 9, '2023-2024-2', '周一 5-6节, 周三 5-6节', '教学楼E-501', '2025-06-12 09:00-11:00', 30, 5, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');
INSERT INTO `course_offering` VALUES (20, 20, 9, '2023-2024-2', '周二 7-8节, 周四 7-8节', '教学楼E-502', '2025-06-12 09:00-11:00', 30, 3, 0, '2025-05-15 02:59:39', '2025-05-15 19:31:36');

-- ----------------------------
-- Table structure for course_selection
-- ----------------------------
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_offering_id` bigint NOT NULL COMMENT '课程开设ID',
  `selection_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '分数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:已选 1:已退 2:已修完',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_student_course_offering`(`student_id` ASC, `course_offering_id` ASC) USING BTREE,
  INDEX `idx_course_offering_id`(`course_offering_id` ASC) USING BTREE,
  CONSTRAINT `fk_selection_offering` FOREIGN KEY (`course_offering_id`) REFERENCES `course_offering` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_selection_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '选课表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_selection
-- ----------------------------
INSERT INTO `course_selection` VALUES (1, 1, 1, '2023-09-01 10:00:00', 85.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (2, 1, 2, '2023-09-01 10:05:00', 90.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (3, 1, 4, '2023-09-01 10:10:00', 88.00, 2, '2025-05-15 02:59:39', '2025-05-15 17:26:49');
INSERT INTO `course_selection` VALUES (4, 1, 5, '2023-09-01 10:15:00', 92.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (5, 2, 1, '2023-09-01 10:20:00', 78.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (6, 2, 2, '2023-09-01 10:25:00', 82.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (7, 2, 4, '2023-09-01 10:30:00', 96.50, 2, '2025-05-15 02:59:39', '2025-10-08 18:36:14');
INSERT INTO `course_selection` VALUES (8, 2, 6, '2023-09-01 10:35:00', 87.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (9, 3, 1, '2023-09-01 10:40:00', 91.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (10, 3, 3, '2023-09-01 10:45:00', 86.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (11, 3, 5, '2023-09-01 10:50:00', 79.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (12, 3, 7, '2023-09-01 10:55:00', 84.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (13, 4, 2, '2023-09-01 11:00:00', 93.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (14, 4, 3, '2023-09-01 11:05:00', 88.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (15, 4, 6, '2023-09-01 11:10:00', 81.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (16, 4, 8, '2023-09-01 11:15:00', 76.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (17, 5, 1, '2023-09-01 11:20:00', 89.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (18, 5, 4, '2023-09-01 11:25:00', 94.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (19, 5, 7, '2023-09-01 11:30:00', 85.00, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (20, 5, 9, '2023-09-01 11:35:00', 80.50, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (21, 6, 10, '2024-02-26 09:00:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (22, 6, 11, '2024-02-26 09:05:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (23, 6, 14, '2024-02-26 09:10:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (24, 6, 17, '2024-02-26 09:15:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (25, 7, 10, '2024-02-26 09:20:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (26, 7, 12, '2024-02-26 09:25:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (27, 7, 15, '2024-02-26 09:30:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (28, 7, 18, '2024-02-26 09:35:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (29, 8, 11, '2024-02-26 09:40:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (30, 8, 13, '2024-02-26 09:45:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (31, 8, 16, '2024-02-26 09:50:00', 89.50, 2, '2025-05-15 02:59:39', '2025-06-05 22:07:05');
INSERT INTO `course_selection` VALUES (32, 8, 19, '2024-02-26 09:55:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (33, 9, 12, '2024-02-26 10:00:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (34, 9, 14, '2024-02-26 10:05:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (35, 9, 17, '2024-02-26 10:10:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (36, 9, 20, '2024-02-26 10:15:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (37, 10, 13, '2024-02-26 10:20:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (38, 10, 15, '2024-02-26 10:25:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (39, 10, 18, '2024-02-26 10:30:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (40, 10, 19, '2024-02-26 10:35:00', NULL, 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `course_selection` VALUES (41, 1, 15, '2025-05-15 17:39:26', 90.00, 0, '2025-05-15 17:39:26', '2025-06-05 02:37:57');
INSERT INTO `course_selection` VALUES (42, 1, 13, '2025-06-19 12:04:55', NULL, 0, '2025-06-19 12:04:55', '2025-06-19 12:04:55');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '院系ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '院系名称',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '院系编码',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院系描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '院系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '计算机科学与技术学院', 'CS', '专注于计算机科学与技术领域的教学和研究', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `department` VALUES (2, '数学与统计学院', 'MATH', '专注于数学和统计学的教学和研究', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `department` VALUES (3, '外国语学院', 'FL', '专注于外国语言文学的教学和研究', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `department` VALUES (4, '经济管理学院', 'EM', '专注于经济学和管理学的教学和研究', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `department` VALUES (5, '物理与电子工程学院', 'PE', '专注于物理学和电子工程的教学和研究', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `department` VALUES (6, '院系6', 'DEPT6', '自动创建的院系6', '2025-10-15 22:21:33', '2025-10-15 22:21:33');

-- ----------------------------
-- Table structure for major
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业名称',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业编码',
  `department_id` bigint NOT NULL COMMENT '所属院系ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_major_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专业表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES (1, '计算机科学与技术', 'CS001', 1, '培养具有良好的计算机理论基础和专业知识的高级人才', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (2, '软件工程', 'CS002', 1, '专注于软件开发、测试和项目管理的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (3, '人工智能', 'CS003', 1, '聚焦人工智能技术与应用研究的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (4, '数据科学与大数据技术', 'CS004', 1, '培养大数据分析和处理能力的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (5, '应用数学', 'MATH001', 2, '培养数学应用能力的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (6, '统计学', 'MATH002', 2, '专注于统计理论与应用的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (7, '英语', 'FL001', 3, '培养英语语言文学和跨文化交际能力的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (8, '日语', 'FL002', 3, '培养日语语言文学和跨文化交际能力的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (9, '工商管理', 'EM001', 4, '培养工商企业管理人才的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (10, '金融学', 'EM002', 4, '培养金融理论与实践专业人才的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (11, '电子信息工程', 'PE001', 5, '培养电子信息系统设计与开发人才的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `major` VALUES (12, '物理学', 'PE002', 5, '培养物理学基础理论与应用研究人才的专业', '2025-05-15 02:59:39', '2025-05-15 02:59:39');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '类型（0：普通公告，1：重要公告，2：紧急公告）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0：草稿，1：已发布，2：已下线）',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `publisher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  CONSTRAINT `fk_notice_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '2023-2024学年第一学期开学通知', '各位同学：\n\n2023-2024学年第一学期将于2023年9月1日正式开始，请各位同学按时返校。新生报到时间为8月28日，老生报到时间为8月30日。\n\n请各位同学提前安排好行程，准时返校。', 1, 1, '2023-08-15 10:00:00', 1, '教务处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (2, '选课系统开放通知', '各位同学：\n\n2023-2024学年第一学期选课系统将于2023年8月25日上午10:00开放，请各位同学在规定时间内完成选课。\n\n选课系统网址：http://xk.example.edu.cn', 0, 1, '2023-08-20 14:30:00', 1, '教务处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (3, '教学楼B区装修通知', '各位师生：\n\n教学楼B区将于2023年10月1日至10月7日进行装修，期间B区所有教室暂停使用。原定在B区上课的课程将临时调整到A区和C区，具体安排请关注教务系统通知。', 0, 1, '2023-09-20 09:15:00', 1, '后勤处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (4, '2023年国庆节放假安排', '各位师生：\n\n根据国家法定节假日安排，2023年国庆节放假时间为10月1日至10月7日，共7天。9月30日（星期六）正常上课，10月8日（星期日）正常上课。', 1, 1, '2023-09-25 11:30:00', 1, '校长办公室', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (5, '期中考试安排通知', '各位同学：\n\n2023-2024学年第一学期期中考试将于第9周进行，具体考试科目和时间安排请关注教务系统通知。\n\n请各位同学合理安排时间，认真复习备考。', 1, 1, '2023-10-15 15:45:00', 1, '教务处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (6, '图书馆开放时间调整通知', '各位师生：\n\n自2023年10月15日起，图书馆开放时间调整为：周一至周五 8:00-22:00，周六、周日 9:00-21:00。\n\n特此通知。', 0, 1, '2023-10-10 16:20:00', 1, '图书馆', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (7, '2023-2024学年第二学期选课通知', '各位同学：\n\n2023-2024学年第二学期选课系统将于2024年1月20日上午10:00开放，请各位同学在规定时间内完成选课。\n\n选课系统网址：http://xk.example.edu.cn', 0, 1, '2024-01-15 10:30:00', 1, '教务处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (8, '2024年寒假放假安排', '各位师生：\n\n2024年寒假放假时间为2024年1月22日至2024年2月25日，共35天。2024年春季学期将于2024年2月26日开始。\n\n请各位同学提前安排好行程，按时返校。', 1, 1, '2024-01-10 09:45:00', 1, '校长办公室', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (9, '校园网络升级维护通知', '各位师生：\n\n校园网络将于2024年3月10日凌晨00:00至06:00进行升级维护，期间校园网络将暂时无法使用。\n\n给各位带来的不便，敬请谅解。', 0, 1, '2024-03-05 14:20:00', 1, '信息中心', '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `notice` VALUES (10, '2024年教师节庆祝活动通知', '各位师生：\n\n2024年教师节庆祝活动将于2024年9月10日上午10:00在学校大礼堂举行，请各位老师和学生代表准时参加。', 0, 0, '2024-09-01 11:10:00', 1, '学生处', '2025-05-15 02:59:39', '2025-05-15 02:59:39');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `admission_date` date NOT NULL COMMENT '入学日期',
  `graduation_date` date NULL DEFAULT NULL COMMENT '毕业日期',
  `gender` tinyint(1) NOT NULL COMMENT '性别 0:男 1:女',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:在读 1:毕业 2:休学 3:退学',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_student_no`(`student_no` ASC) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  CONSTRAINT `fk_student_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '2021001001', 13, 1, '2021-09-01', NULL, 0, '110101200301100001', '2003-01-10', '北京市海淀区', 0, '2025-10-15 22:21:47', '2025-10-15 22:21:47');
INSERT INTO `student` VALUES (2, '2021001002', 14, 1, '2021-09-01', NULL, 1, '110101200302150002', '2003-02-15', '北京市朝阳区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (3, '2021001003', 15, 2, '2021-09-01', NULL, 0, '110101200303200003', '2003-03-20', '北京市西城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (4, '2021001004', 16, 2, '2021-09-01', NULL, 1, '110101200304250004', '2003-04-25', '北京市东城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (5, '2021002001', 17, 3, '2021-09-01', NULL, 0, '110101200305300005', '2003-05-30', '北京市丰台区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (6, '2021002002', 18, 3, '2021-09-01', NULL, 1, '110101200306050006', '2003-06-05', '北京市石景山区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (7, '2021002003', 19, 4, '2021-09-01', NULL, 0, '110101200307100007', '2003-07-10', '北京市昌平区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (8, '2021002004', 20, 4, '2021-09-01', NULL, 1, '110101200308150008', '2003-08-15', '北京市海淀区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (9, '2021003001', 21, 5, '2021-09-01', NULL, 0, '110101200309200009', '2003-09-20', '北京市朝阳区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (10, '2021003002', 22, 5, '2021-09-01', NULL, 1, '110101200310250010', '2003-10-25', '北京市西城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (11, '2021004001', 23, 6, '2021-09-01', NULL, 0, '110101200311300011', '2003-11-30', '北京市东城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (12, '2021004002', 24, 6, '2021-09-01', NULL, 1, '110101200312050012', '2003-12-05', '北京市丰台区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (13, '2021005001', 25, 7, '2021-09-01', NULL, 0, '110101200301100013', '2003-01-10', '北京市石景山区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (14, '2021005002', 26, 7, '2021-09-01', NULL, 1, '110101200302150014', '2003-02-15', '北京市昌平区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (15, '2021006001', 27, 8, '2021-09-01', NULL, 0, '110101200303200015', '2003-03-20', '北京市海淀区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (16, '2021006002', 28, 8, '2021-09-01', NULL, 1, '110101200304250016', '2003-04-25', '北京市朝阳区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (17, '2021007001', 29, 9, '2021-09-01', NULL, 0, '110101200305300017', '2003-05-30', '北京市西城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (18, '2021007002', 30, 9, '2021-09-01', NULL, 1, '110101200306050018', '2003-06-05', '北京市东城区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (19, '2021009001', 31, 10, '2021-09-01', NULL, 0, '110101200307100019', '2003-07-10', '北京市丰台区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (20, '2021009002', 32, 10, '2021-09-01', NULL, 1, '110101200308150020', '2003-08-15', '北京市石景山区', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `student` VALUES (22, '20324454', 143, 1, '2025-05-12', '2025-05-31', 0, '520135485461254', '2025-05-20', 'student001', 0, '2025-05-17 18:41:46', '2025-05-17 18:41:46');
INSERT INTO `student` VALUES (23, '20034243', 147, 2, '2025-09-28', NULL, 0, '520103200495829481', '2025-09-28', '', 0, '2025-09-29 16:05:47', '2025-09-29 16:05:47');
INSERT INTO `student` VALUES (24, '2034234', 148, 3, '2025-09-22', '2025-09-30', 0, '520393847284738443', '2025-09-30', '', 0, '2025-09-29 18:31:41', '2025-09-29 18:31:41');
INSERT INTO `student` VALUES (25, 'S202301001', 149, 1, '2023-09-01', '2027-06-30', 0, '110101200301010033', '2003-01-01', '北京市海淀区', 0, '2025-10-08 18:32:27', '2025-10-08 18:32:27');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` int NULL DEFAULT 0 COMMENT '菜单状态（0显示 1隐藏）',
  `status` int NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9504 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', 0, 0, '', 'Setting', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '教务管理', 0, 2, 'education', NULL, '', 1, 0, 'M', 0, 0, '', 'School', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '教务管理目录');
INSERT INTO `sys_menu` VALUES (3, '课程管理', 0, 3, 'course', NULL, '', 1, 0, 'M', 0, 0, '', 'Reading', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '课程管理目录');
INSERT INTO `sys_menu` VALUES (4, '考勤管理', 0, 4, 'attendance', NULL, '', 1, 0, 'M', 0, 0, '', 'Calendar', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '考勤管理目录');
INSERT INTO `sys_menu` VALUES (5, '学生管理', 0, 5, 'student', NULL, '', 1, 0, 'M', 0, 0, '', 'User', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '学生管理目录');
INSERT INTO `sys_menu` VALUES (6, '教师管理', 0, 6, 'teacher', NULL, '', 1, 0, 'M', 0, 0, '', 'UserFilled', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '教师管理目录');
INSERT INTO `sys_menu` VALUES (7, '选课管理', 0, 7, 'selection', NULL, '', 1, 0, 'M', 0, 0, '', 'Calendar', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '选课管理目录');
INSERT INTO `sys_menu` VALUES (8, '成绩管理', 0, 8, 'grade', NULL, '', 1, 0, 'M', 0, 0, '', 'Document', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '成绩管理目录');
INSERT INTO `sys_menu` VALUES (10, '个人中心', 0, 10, 'personal', NULL, '', 1, 0, 'M', 0, 0, '', 'User', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '个人中心目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', 0, 0, 'system:user:list', 'User', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', 0, 0, 'system:role:list', 'UserFilled', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', 0, 0, 'system:menu:list', 'Menu', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '通知管理', 1, 4, 'notice', 'system/notice/index', '', 1, 0, 'C', 0, 0, 'system:notice:list', 'Bell', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '通知管理菜单');
INSERT INTO `sys_menu` VALUES (104, '待办事项', 1, 5, 'todo', 'system/todo/index', '', 1, 0, 'C', 0, 0, 'system:todo:list', 'List', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '待办事项菜单');
INSERT INTO `sys_menu` VALUES (200, '院系管理', 2, 1, 'department', 'education/department/index', '', 1, 0, 'C', 0, 0, 'education:department:list', 'OfficeBuilding', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系管理菜单');
INSERT INTO `sys_menu` VALUES (201, '专业管理', 2, 2, 'major', 'education/major/index', '', 1, 0, 'C', 0, 0, 'education:major:list', 'Collection', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业管理菜单');
INSERT INTO `sys_menu` VALUES (202, '班级管理', 2, 3, 'class', 'education/class/index', '', 1, 0, 'C', 0, 0, 'education:class:list', 'Postcard', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级管理菜单');
INSERT INTO `sys_menu` VALUES (300, '学生列表', 5, 1, 'student-list', 'student/index', '', 1, 0, 'C', 0, 0, 'education:student:list', 'List', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生列表菜单');
INSERT INTO `sys_menu` VALUES (301, '新增学生', 5, 2, 'student-add', 'student/add', '', 1, 0, 'C', 0, 0, 'education:student:add', 'Plus', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '新增学生菜单');
INSERT INTO `sys_menu` VALUES (400, '教师列表', 6, 1, 'teacher-list', 'teacher/index', '', 1, 0, 'C', 0, 0, 'education:teacher:list', 'List', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师列表菜单');
INSERT INTO `sys_menu` VALUES (401, '新增教师', 6, 2, 'teacher-add', 'teacher/add', '', 1, 0, 'C', 0, 0, 'education:teacher:add', 'Plus', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '新增教师菜单');
INSERT INTO `sys_menu` VALUES (500, '课程列表', 3, 1, 'course-list', 'course/index', '', 1, 0, 'C', 0, 0, 'course:course:list', 'List', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程列表菜单');
INSERT INTO `sys_menu` VALUES (501, '新增课程', 3, 2, 'course-add', 'course/add', '', 1, 0, 'C', 0, 0, 'course:course:add', 'Plus', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '新增课程菜单');
INSERT INTO `sys_menu` VALUES (502, '课程开设记录', 3, 3, 'course-offering', 'course/offering/index', '', 1, 0, 'C', 0, 0, 'course:offering:list', 'Notebook', '2025-05-14 15:00:22', '2025-05-17 18:01:49', '课程开设记录菜单');
INSERT INTO `sys_menu` VALUES (600, '选课管理', 7, 1, 'selection-manage', 'selection/manage', '', 1, 0, 'C', 0, 0, 'course:selection:list', 'Setting', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课管理菜单');
INSERT INTO `sys_menu` VALUES (601, '授课列表', 7, 2, 'course-teaching', 'course/offering/index', '', 1, 0, 'C', 0, 0, 'course:teaching:list', 'Reading', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '授课列表菜单');
INSERT INTO `sys_menu` VALUES (650, '我的课程', 10, 1, 'my-courses', 'course/my-courses/index', '', 1, 0, 'C', 0, 0, 'course:my-courses:list', 'Notebook', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '学生课程菜单');
INSERT INTO `sys_menu` VALUES (651, '选课', 10, 2, 'course-select', 'course/selection/index', '', 1, 0, 'C', 0, 0, 'course:selection:list', 'Plus', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课菜单');
INSERT INTO `sys_menu` VALUES (700, '考勤记录', 4, 1, 'attendance-record', 'attendance/record/index', '', 1, 0, 'C', 0, 0, 'attendance:record:list', 'Document', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤记录菜单');
INSERT INTO `sys_menu` VALUES (701, '考勤统计', 4, 2, 'attendance-stats', 'attendance/stats/index', '', 1, 0, 'C', 0, 0, 'attendance:stats:list', 'PieChart', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤统计菜单');
INSERT INTO `sys_menu` VALUES (750, '我的考勤', 10, 3, 'attendance-my', 'attendance/my/index', '', 1, 0, 'C', 0, 0, 'attendance:record:list', 'Search', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '我的考勤菜单');
INSERT INTO `sys_menu` VALUES (800, '成绩录入', 8, 1, 'grade-input', 'grade/input', '', 1, 0, 'C', 0, 0, 'grade:input', 'Edit', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩录入菜单');
INSERT INTO `sys_menu` VALUES (801, '成绩查询', 8, 2, 'grade-check', 'grade/check', '', 1, 0, 'C', 0, 0, 'grade:query', 'Search', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩查询菜单');
INSERT INTO `sys_menu` VALUES (802, '成绩统计', 8, 3, 'grade-stats', 'grade/stats', '', 1, 0, 'C', 0, 0, 'grade:stats', 'DataAnalysis', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩统计菜单');
INSERT INTO `sys_menu` VALUES (850, '我的成绩', 10, 4, 'grade-my', 'grade/my/index', '', 1, 0, 'C', 0, 0, 'grade:query', 'Document', '2025-05-14 15:00:22', '2025-05-14 21:35:46', '我的成绩菜单');
INSERT INTO `sys_menu` VALUES (900, '仪表盘', 0, 0, 'dashboard', 'dashboard/index', '', 1, 0, 'C', 0, 0, '', 'Dashboard', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '仪表盘菜单');
INSERT INTO `sys_menu` VALUES (910, '消息中心', 0, 9, 'message', NULL, '', 1, 0, 'M', 0, 0, '', 'Message', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '消息中心目录');
INSERT INTO `sys_menu` VALUES (911, '通知公告', 910, 1, 'notice', 'message/notice', '', 1, 0, 'C', 0, 0, 'system:notice:list', 'Bell', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (912, '待办事项', 910, 2, 'todo', 'message/todo', '', 1, 0, 'C', 0, 0, 'system:todo:list', 'List', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '待办事项菜单');
INSERT INTO `sys_menu` VALUES (950, '个人资料', 10, 2, 'profile', 'profile/index', '', 1, 0, 'C', 0, 0, 'system:user:profile', 'User', '2025-05-14 16:45:40', '2025-05-14 16:45:40', '个人资料菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:query', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户查询权限');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:add', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户新增权限');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:edit', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户修改权限');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:delete', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户删除权限');
INSERT INTO `sys_menu` VALUES (1005, '用户删除别名', 100, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:remove', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户删除别名权限');
INSERT INTO `sys_menu` VALUES (1006, '用户导出', 100, 6, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:export', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '用户导出权限');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:resetPwd', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '重置密码权限');
INSERT INTO `sys_menu` VALUES (1011, '角色查询', 101, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:role:query', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色查询权限');
INSERT INTO `sys_menu` VALUES (1012, '角色新增', 101, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:role:add', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色新增权限');
INSERT INTO `sys_menu` VALUES (1013, '角色修改', 101, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:role:edit', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色修改权限');
INSERT INTO `sys_menu` VALUES (1014, '角色删除', 101, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'system:role:remove', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色删除权限');
INSERT INTO `sys_menu` VALUES (1015, '角色导出', 101, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'system:role:export', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '角色导出权限');
INSERT INTO `sys_menu` VALUES (1021, '菜单查询', 102, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:menu:query', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '菜单查询权限');
INSERT INTO `sys_menu` VALUES (1022, '菜单新增', 102, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:menu:add', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '菜单新增权限');
INSERT INTO `sys_menu` VALUES (1023, '菜单修改', 102, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:menu:edit', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '菜单修改权限');
INSERT INTO `sys_menu` VALUES (1024, '菜单删除', 102, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'system:menu:remove', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '菜单删除权限');
INSERT INTO `sys_menu` VALUES (1031, '通知查询', 103, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:notice:query', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '通知查询权限');
INSERT INTO `sys_menu` VALUES (1032, '通知新增', 103, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:notice:add', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '通知新增权限');
INSERT INTO `sys_menu` VALUES (1033, '通知修改', 103, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:notice:edit', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '通知修改权限');
INSERT INTO `sys_menu` VALUES (1034, '通知删除', 103, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'system:notice:remove', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '通知删除权限');
INSERT INTO `sys_menu` VALUES (1041, '待办查询', 104, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:todo:query', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '待办查询权限');
INSERT INTO `sys_menu` VALUES (1042, '待办新增', 104, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:todo:add', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '待办新增权限');
INSERT INTO `sys_menu` VALUES (1043, '待办修改', 104, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:todo:edit', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '待办修改权限');
INSERT INTO `sys_menu` VALUES (1044, '待办删除', 104, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'system:todo:remove', '', '2025-05-14 14:59:57', '2025-05-14 14:59:57', '待办删除权限');
INSERT INTO `sys_menu` VALUES (2001, '院系查询', 200, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:department:query', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系查询权限');
INSERT INTO `sys_menu` VALUES (2002, '院系新增', 200, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'education:department:add', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系新增权限');
INSERT INTO `sys_menu` VALUES (2003, '院系修改', 200, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'education:department:edit', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系修改权限');
INSERT INTO `sys_menu` VALUES (2004, '院系删除', 200, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'education:department:delete', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系删除权限');
INSERT INTO `sys_menu` VALUES (2005, '院系删除别名', 200, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'education:department:remove', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '院系删除别名权限');
INSERT INTO `sys_menu` VALUES (2011, '专业查询', 201, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:major:list', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业查询权限');
INSERT INTO `sys_menu` VALUES (2012, '专业新增', 201, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'education:major:add', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业新增权限');
INSERT INTO `sys_menu` VALUES (2013, '专业修改', 201, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'education:major:edit', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业修改权限');
INSERT INTO `sys_menu` VALUES (2014, '专业删除', 201, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'education:major:delete', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业删除权限');
INSERT INTO `sys_menu` VALUES (2015, '专业删除别名', 201, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'education:major:remove', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '专业删除别名权限');
INSERT INTO `sys_menu` VALUES (2021, '班级查询', 202, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:class:list', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级查询权限');
INSERT INTO `sys_menu` VALUES (2022, '班级新增', 202, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'education:class:add', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级新增权限');
INSERT INTO `sys_menu` VALUES (2023, '班级修改', 202, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'education:class:edit', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级修改权限');
INSERT INTO `sys_menu` VALUES (2024, '班级删除', 202, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'education:class:delete', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级删除权限');
INSERT INTO `sys_menu` VALUES (2025, '班级删除别名', 202, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'education:class:remove', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '班级删除别名权限');
INSERT INTO `sys_menu` VALUES (3001, '学生查询', 300, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:list', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生查询权限');
INSERT INTO `sys_menu` VALUES (3002, '学生详情', 300, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:query', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生详情权限');
INSERT INTO `sys_menu` VALUES (3003, '学生编辑', 300, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:edit', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生编辑权限');
INSERT INTO `sys_menu` VALUES (3004, '学生删除', 300, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:delete', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生删除权限');
INSERT INTO `sys_menu` VALUES (3005, '学生删除别名', 300, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:remove', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生删除别名权限');
INSERT INTO `sys_menu` VALUES (3006, '学生导出', 300, 6, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:export', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生导出权限');
INSERT INTO `sys_menu` VALUES (3007, '学生导入', 300, 7, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:import', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生导入权限');
INSERT INTO `sys_menu` VALUES (3011, '学生新增', 301, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:student:add', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '学生新增权限');
INSERT INTO `sys_menu` VALUES (4001, '教师查询', 400, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:list', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师查询权限');
INSERT INTO `sys_menu` VALUES (4002, '教师详情', 400, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:query', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师详情权限');
INSERT INTO `sys_menu` VALUES (4003, '教师编辑', 400, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:edit', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师编辑权限');
INSERT INTO `sys_menu` VALUES (4004, '教师删除', 400, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:delete', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师删除权限');
INSERT INTO `sys_menu` VALUES (4005, '重置密码', 400, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:resetPwd', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '重置密码权限');
INSERT INTO `sys_menu` VALUES (4006, '教师导出', 400, 6, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:export', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师导出权限');
INSERT INTO `sys_menu` VALUES (4007, '教师导入', 400, 7, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:import', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师导入权限');
INSERT INTO `sys_menu` VALUES (4011, '教师新增', 401, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'education:teacher:add', '', '2025-05-14 15:00:06', '2025-05-14 15:00:06', '教师新增权限');
INSERT INTO `sys_menu` VALUES (5001, '课程查询', 500, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:query', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程查询权限');
INSERT INTO `sys_menu` VALUES (5002, '课程编辑', 500, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:edit', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程编辑权限');
INSERT INTO `sys_menu` VALUES (5003, '课程删除', 500, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:delete', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程删除权限');
INSERT INTO `sys_menu` VALUES (5004, '课程导出', 500, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:export', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程导出权限');
INSERT INTO `sys_menu` VALUES (5005, '课程导入', 500, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:import', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程导入权限');
INSERT INTO `sys_menu` VALUES (5011, '课程添加', 501, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'course:course:add', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '课程添加权限');
INSERT INTO `sys_menu` VALUES (5021, '开课记录查询', 502, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'course:offering:list', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '开课记录查询权限');
INSERT INTO `sys_menu` VALUES (5022, '开课记录添加', 502, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'course:offering:add', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '开课记录添加权限');
INSERT INTO `sys_menu` VALUES (5023, '开课记录编辑', 502, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'course:offering:edit', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '开课记录编辑权限');
INSERT INTO `sys_menu` VALUES (5024, '开课记录删除', 502, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'course:offering:delete', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '开课记录删除权限');
INSERT INTO `sys_menu` VALUES (6001, '选课记录查询', 600, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'course:selection:list', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课记录查询权限');
INSERT INTO `sys_menu` VALUES (6002, '选课记录详情', 600, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'course:selection:query', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课记录详情权限');
INSERT INTO `sys_menu` VALUES (6003, '选课操作', 600, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'course:selection:operate', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课操作权限');
INSERT INTO `sys_menu` VALUES (6004, '选课导出', 600, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'course:selection:export', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '选课导出权限');
INSERT INTO `sys_menu` VALUES (6011, '授课查询', 601, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'course:teaching:list', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '授课查询权限');
INSERT INTO `sys_menu` VALUES (6501, '我的课程查询', 650, 1, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:my-courses:list', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '学生课程查询权限');
INSERT INTO `sys_menu` VALUES (6502, '课程详情查询', 650, 2, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:my-courses:detail', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '课程详情查询权限');
INSERT INTO `sys_menu` VALUES (6511, '可选课程查询', 651, 1, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:selection:available', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '可选课程查询权限');
INSERT INTO `sys_menu` VALUES (6512, '已选课程查询', 651, 2, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:selection:selected', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '已选课程查询权限');
INSERT INTO `sys_menu` VALUES (6513, '选课操作', 651, 3, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:selection:select', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '选课操作权限');
INSERT INTO `sys_menu` VALUES (6514, '退选操作', 651, 4, '', NULL, NULL, 1, 0, 'F', 0, 0, 'course:selection:withdraw', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '退选操作权限');
INSERT INTO `sys_menu` VALUES (7001, '考勤查询', 700, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:record:list', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤查询权限');
INSERT INTO `sys_menu` VALUES (7002, '考勤添加', 700, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:record:add', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤添加权限');
INSERT INTO `sys_menu` VALUES (7003, '考勤编辑', 700, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:record:edit', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤编辑权限');
INSERT INTO `sys_menu` VALUES (7004, '考勤删除', 700, 4, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:record:remove', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤删除权限');
INSERT INTO `sys_menu` VALUES (7005, '考勤导出', 700, 5, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:record:export', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤导出权限');
INSERT INTO `sys_menu` VALUES (7011, '考勤统计查询', 701, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'attendance:stats:list', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '考勤统计查询权限');
INSERT INTO `sys_menu` VALUES (8001, '成绩录入权限', 800, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'grade:input', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩录入权限');
INSERT INTO `sys_menu` VALUES (8011, '成绩查询权限', 801, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'grade:query', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩查询权限');
INSERT INTO `sys_menu` VALUES (8021, '成绩统计权限', 802, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'grade:stats', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩统计权限');
INSERT INTO `sys_menu` VALUES (8022, '成绩导出权限', 802, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'grade:export', '', '2025-05-14 15:00:22', '2025-05-14 15:00:22', '成绩导出权限');
INSERT INTO `sys_menu` VALUES (8501, '成绩查询', 850, 1, '', NULL, NULL, 1, 0, 'F', 0, 0, 'grade:my:list', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '学生成绩查询权限');
INSERT INTO `sys_menu` VALUES (8502, '成绩统计查询', 850, 2, '', NULL, NULL, 1, 0, 'F', 0, 0, 'grade:my:stats', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '学生成绩统计查询权限');
INSERT INTO `sys_menu` VALUES (8503, '成绩打印', 850, 3, '', NULL, NULL, 1, 0, 'F', 0, 0, 'grade:my:print', '', '2025-05-14 21:35:46', '2025-05-14 21:35:46', '学生成绩打印权限');
INSERT INTO `sys_menu` VALUES (9001, '基本数据', 900, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'dashboard:basic', '', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '基本数据查询权限');
INSERT INTO `sys_menu` VALUES (9002, '图表数据', 900, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'dashboard:charts', '', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '图表数据查询权限');
INSERT INTO `sys_menu` VALUES (9003, '系统状态', 900, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'dashboard:system', '', '2025-05-14 15:00:15', '2025-05-14 15:00:15', '系统状态查询权限');
INSERT INTO `sys_menu` VALUES (9501, '个人资料查询', 950, 1, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:profile:query', '', '2025-05-14 16:45:40', '2025-05-14 16:45:40', '个人资料查询权限');
INSERT INTO `sys_menu` VALUES (9502, '个人资料修改', 950, 2, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:profile:edit', '', '2025-05-14 16:45:40', '2025-05-14 16:45:40', '个人资料修改权限');
INSERT INTO `sys_menu` VALUES (9503, '头像上传', 950, 3, '', NULL, '', 1, 0, 'F', 0, 0, 'system:user:profile:avatar', '', '2025-05-14 16:45:40', '2025-05-14 16:45:40', '头像上传权限');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `status` int NOT NULL COMMENT '角色状态（0正常 1停用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, 0, '2025-05-14 14:59:57', '2025-05-14 14:59:57', '超级管理员');
INSERT INTO `sys_role` VALUES (2, '教师', 'teacher', 2, 0, '2025-05-14 14:59:57', '2025-05-14 14:59:57', '教师角色');
INSERT INTO `sys_role` VALUES (3, '学生', 'student', 3, 0, '2025-05-14 14:59:57', '2025-05-14 14:59:57', '学生角色');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
  INDEX `fk_role_menu_menu`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `fk_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (1, 6);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (2, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (2, 8);
INSERT INTO `sys_role_menu` VALUES (3, 10);
INSERT INTO `sys_role_menu` VALUES (1, 100);
INSERT INTO `sys_role_menu` VALUES (1, 101);
INSERT INTO `sys_role_menu` VALUES (1, 102);
INSERT INTO `sys_role_menu` VALUES (1, 103);
INSERT INTO `sys_role_menu` VALUES (1, 104);
INSERT INTO `sys_role_menu` VALUES (1, 200);
INSERT INTO `sys_role_menu` VALUES (2, 200);
INSERT INTO `sys_role_menu` VALUES (1, 201);
INSERT INTO `sys_role_menu` VALUES (1, 202);
INSERT INTO `sys_role_menu` VALUES (1, 300);
INSERT INTO `sys_role_menu` VALUES (2, 300);
INSERT INTO `sys_role_menu` VALUES (1, 301);
INSERT INTO `sys_role_menu` VALUES (1, 400);
INSERT INTO `sys_role_menu` VALUES (2, 400);
INSERT INTO `sys_role_menu` VALUES (1, 401);
INSERT INTO `sys_role_menu` VALUES (1, 500);
INSERT INTO `sys_role_menu` VALUES (2, 500);
INSERT INTO `sys_role_menu` VALUES (1, 501);
INSERT INTO `sys_role_menu` VALUES (1, 502);
INSERT INTO `sys_role_menu` VALUES (2, 502);
INSERT INTO `sys_role_menu` VALUES (1, 600);
INSERT INTO `sys_role_menu` VALUES (2, 600);
INSERT INTO `sys_role_menu` VALUES (1, 601);
INSERT INTO `sys_role_menu` VALUES (2, 601);
INSERT INTO `sys_role_menu` VALUES (3, 650);
INSERT INTO `sys_role_menu` VALUES (3, 651);
INSERT INTO `sys_role_menu` VALUES (1, 700);
INSERT INTO `sys_role_menu` VALUES (2, 700);
INSERT INTO `sys_role_menu` VALUES (1, 701);
INSERT INTO `sys_role_menu` VALUES (2, 701);
INSERT INTO `sys_role_menu` VALUES (3, 750);
INSERT INTO `sys_role_menu` VALUES (1, 800);
INSERT INTO `sys_role_menu` VALUES (2, 800);
INSERT INTO `sys_role_menu` VALUES (1, 801);
INSERT INTO `sys_role_menu` VALUES (2, 801);
INSERT INTO `sys_role_menu` VALUES (1, 802);
INSERT INTO `sys_role_menu` VALUES (2, 802);
INSERT INTO `sys_role_menu` VALUES (3, 850);
INSERT INTO `sys_role_menu` VALUES (1, 900);
INSERT INTO `sys_role_menu` VALUES (2, 900);
INSERT INTO `sys_role_menu` VALUES (3, 900);
INSERT INTO `sys_role_menu` VALUES (1, 910);
INSERT INTO `sys_role_menu` VALUES (2, 910);
INSERT INTO `sys_role_menu` VALUES (3, 910);
INSERT INTO `sys_role_menu` VALUES (1, 911);
INSERT INTO `sys_role_menu` VALUES (2, 911);
INSERT INTO `sys_role_menu` VALUES (3, 911);
INSERT INTO `sys_role_menu` VALUES (1, 912);
INSERT INTO `sys_role_menu` VALUES (2, 912);
INSERT INTO `sys_role_menu` VALUES (3, 912);
INSERT INTO `sys_role_menu` VALUES (1, 950);
INSERT INTO `sys_role_menu` VALUES (2, 950);
INSERT INTO `sys_role_menu` VALUES (3, 950);
INSERT INTO `sys_role_menu` VALUES (1, 1001);
INSERT INTO `sys_role_menu` VALUES (1, 1002);
INSERT INTO `sys_role_menu` VALUES (1, 1003);
INSERT INTO `sys_role_menu` VALUES (1, 1004);
INSERT INTO `sys_role_menu` VALUES (1, 1005);
INSERT INTO `sys_role_menu` VALUES (1, 1006);
INSERT INTO `sys_role_menu` VALUES (1, 1007);
INSERT INTO `sys_role_menu` VALUES (1, 1011);
INSERT INTO `sys_role_menu` VALUES (1, 1012);
INSERT INTO `sys_role_menu` VALUES (1, 1013);
INSERT INTO `sys_role_menu` VALUES (1, 1014);
INSERT INTO `sys_role_menu` VALUES (1, 1015);
INSERT INTO `sys_role_menu` VALUES (1, 1021);
INSERT INTO `sys_role_menu` VALUES (1, 1022);
INSERT INTO `sys_role_menu` VALUES (1, 1023);
INSERT INTO `sys_role_menu` VALUES (1, 1024);
INSERT INTO `sys_role_menu` VALUES (1, 1031);
INSERT INTO `sys_role_menu` VALUES (1, 1032);
INSERT INTO `sys_role_menu` VALUES (1, 1033);
INSERT INTO `sys_role_menu` VALUES (1, 1034);
INSERT INTO `sys_role_menu` VALUES (1, 1041);
INSERT INTO `sys_role_menu` VALUES (1, 1042);
INSERT INTO `sys_role_menu` VALUES (1, 1043);
INSERT INTO `sys_role_menu` VALUES (1, 1044);
INSERT INTO `sys_role_menu` VALUES (1, 2001);
INSERT INTO `sys_role_menu` VALUES (2, 2001);
INSERT INTO `sys_role_menu` VALUES (1, 2002);
INSERT INTO `sys_role_menu` VALUES (1, 2003);
INSERT INTO `sys_role_menu` VALUES (1, 2004);
INSERT INTO `sys_role_menu` VALUES (1, 2005);
INSERT INTO `sys_role_menu` VALUES (1, 2011);
INSERT INTO `sys_role_menu` VALUES (1, 2012);
INSERT INTO `sys_role_menu` VALUES (1, 2013);
INSERT INTO `sys_role_menu` VALUES (1, 2014);
INSERT INTO `sys_role_menu` VALUES (1, 2015);
INSERT INTO `sys_role_menu` VALUES (1, 2021);
INSERT INTO `sys_role_menu` VALUES (1, 2022);
INSERT INTO `sys_role_menu` VALUES (1, 2023);
INSERT INTO `sys_role_menu` VALUES (1, 2024);
INSERT INTO `sys_role_menu` VALUES (1, 2025);
INSERT INTO `sys_role_menu` VALUES (1, 3001);
INSERT INTO `sys_role_menu` VALUES (2, 3001);
INSERT INTO `sys_role_menu` VALUES (1, 3002);
INSERT INTO `sys_role_menu` VALUES (1, 3003);
INSERT INTO `sys_role_menu` VALUES (1, 3004);
INSERT INTO `sys_role_menu` VALUES (1, 3005);
INSERT INTO `sys_role_menu` VALUES (1, 3006);
INSERT INTO `sys_role_menu` VALUES (1, 3007);
INSERT INTO `sys_role_menu` VALUES (1, 3011);
INSERT INTO `sys_role_menu` VALUES (1, 4001);
INSERT INTO `sys_role_menu` VALUES (2, 4001);
INSERT INTO `sys_role_menu` VALUES (1, 4002);
INSERT INTO `sys_role_menu` VALUES (1, 4003);
INSERT INTO `sys_role_menu` VALUES (1, 4004);
INSERT INTO `sys_role_menu` VALUES (1, 4005);
INSERT INTO `sys_role_menu` VALUES (1, 4006);
INSERT INTO `sys_role_menu` VALUES (1, 4007);
INSERT INTO `sys_role_menu` VALUES (1, 4011);
INSERT INTO `sys_role_menu` VALUES (1, 5001);
INSERT INTO `sys_role_menu` VALUES (2, 5001);
INSERT INTO `sys_role_menu` VALUES (1, 5002);
INSERT INTO `sys_role_menu` VALUES (1, 5003);
INSERT INTO `sys_role_menu` VALUES (1, 5004);
INSERT INTO `sys_role_menu` VALUES (1, 5005);
INSERT INTO `sys_role_menu` VALUES (1, 5011);
INSERT INTO `sys_role_menu` VALUES (1, 5021);
INSERT INTO `sys_role_menu` VALUES (2, 5021);
INSERT INTO `sys_role_menu` VALUES (1, 5022);
INSERT INTO `sys_role_menu` VALUES (1, 5023);
INSERT INTO `sys_role_menu` VALUES (1, 5024);
INSERT INTO `sys_role_menu` VALUES (1, 6001);
INSERT INTO `sys_role_menu` VALUES (2, 6001);
INSERT INTO `sys_role_menu` VALUES (1, 6002);
INSERT INTO `sys_role_menu` VALUES (2, 6002);
INSERT INTO `sys_role_menu` VALUES (1, 6003);
INSERT INTO `sys_role_menu` VALUES (1, 6004);
INSERT INTO `sys_role_menu` VALUES (1, 6011);
INSERT INTO `sys_role_menu` VALUES (2, 6011);
INSERT INTO `sys_role_menu` VALUES (3, 6501);
INSERT INTO `sys_role_menu` VALUES (3, 6502);
INSERT INTO `sys_role_menu` VALUES (3, 6511);
INSERT INTO `sys_role_menu` VALUES (3, 6512);
INSERT INTO `sys_role_menu` VALUES (3, 6513);
INSERT INTO `sys_role_menu` VALUES (3, 6514);
INSERT INTO `sys_role_menu` VALUES (1, 7001);
INSERT INTO `sys_role_menu` VALUES (2, 7001);
INSERT INTO `sys_role_menu` VALUES (1, 7002);
INSERT INTO `sys_role_menu` VALUES (2, 7002);
INSERT INTO `sys_role_menu` VALUES (1, 7003);
INSERT INTO `sys_role_menu` VALUES (2, 7003);
INSERT INTO `sys_role_menu` VALUES (1, 7004);
INSERT INTO `sys_role_menu` VALUES (1, 7005);
INSERT INTO `sys_role_menu` VALUES (1, 7011);
INSERT INTO `sys_role_menu` VALUES (2, 7011);
INSERT INTO `sys_role_menu` VALUES (1, 8001);
INSERT INTO `sys_role_menu` VALUES (2, 8001);
INSERT INTO `sys_role_menu` VALUES (1, 8011);
INSERT INTO `sys_role_menu` VALUES (2, 8011);
INSERT INTO `sys_role_menu` VALUES (1, 8021);
INSERT INTO `sys_role_menu` VALUES (2, 8021);
INSERT INTO `sys_role_menu` VALUES (1, 8022);
INSERT INTO `sys_role_menu` VALUES (3, 8501);
INSERT INTO `sys_role_menu` VALUES (3, 8502);
INSERT INTO `sys_role_menu` VALUES (3, 8503);
INSERT INTO `sys_role_menu` VALUES (1, 9001);
INSERT INTO `sys_role_menu` VALUES (2, 9001);
INSERT INTO `sys_role_menu` VALUES (3, 9001);
INSERT INTO `sys_role_menu` VALUES (1, 9002);
INSERT INTO `sys_role_menu` VALUES (2, 9002);
INSERT INTO `sys_role_menu` VALUES (1, 9003);
INSERT INTO `sys_role_menu` VALUES (1, 9501);
INSERT INTO `sys_role_menu` VALUES (2, 9501);
INSERT INTO `sys_role_menu` VALUES (3, 9501);
INSERT INTO `sys_role_menu` VALUES (1, 9502);
INSERT INTO `sys_role_menu` VALUES (2, 9502);
INSERT INTO `sys_role_menu` VALUES (3, 9502);
INSERT INTO `sys_role_menu` VALUES (1, 9503);
INSERT INTO `sys_role_menu` VALUES (2, 9503);
INSERT INTO `sys_role_menu` VALUES (3, 9503);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:正常 1:禁用',
  `user_type` tinyint NOT NULL COMMENT '用户类型 0:管理员 1:教师 2:学生',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 150 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '管理员', '/uploads/avatar/2025/05/14/022dec4609a14c59b4c2d1ee36eed050.jpg', '1610494022@qq.com', '18745618542', 0, 0, '2025-05-10 23:23:57', '2025-05-14 18:36:04');
INSERT INTO `sys_user` VALUES (2, 'teacher', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '测试教师', '/uploads/avatar/2025/05/14/edc6a90f3eb44d8aae053aeacb7da204.jpg', 'teacher@example.com', '13800000001', 0, 1, '2025-05-10 23:23:57', '2025-05-14 18:37:03');
INSERT INTO `sys_user` VALUES (3, 'teacher001', '$2a$10$jSsRPHkV3OX5V0g6RkMetO8X/S/gj9rjdsAa0PGczXQLsLeK/ad3u', '张教授1', NULL, 'zhang@example.com', '13800138001', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:02');
INSERT INTO `sys_user` VALUES (4, 'teacher002', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '李教授', NULL, 'li@example.com', '13800138002', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:04');
INSERT INTO `sys_user` VALUES (5, 'teacher003', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '王副教授', NULL, 'wang@example.com', '13800138003', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:03');
INSERT INTO `sys_user` VALUES (6, 'teacher004', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '赵讲师', NULL, 'zhao@example.com', '13800138004', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:05');
INSERT INTO `sys_user` VALUES (7, 'teacher005', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '刘教授', NULL, 'liu@example.com', '13800138005', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:06');
INSERT INTO `sys_user` VALUES (8, 'teacher006', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '孙副教授', NULL, 'sun@example.com', '13800138006', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:07');
INSERT INTO `sys_user` VALUES (9, 'teacher007', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '陈讲师', NULL, 'chen@example.com', '13800138007', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:06');
INSERT INTO `sys_user` VALUES (10, 'teacher008', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '杨教授', NULL, 'yang@example.com', '13800138008', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:08');
INSERT INTO `sys_user` VALUES (11, 'teacher009', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '吴副教授', NULL, 'wu@example.com', '13800138009', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:09');
INSERT INTO `sys_user` VALUES (12, 'teacher010', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '郑讲师', NULL, 'zheng@example.com', '13800138010', 0, 1, '2025-05-15 03:17:12', '2025-05-15 03:19:10');
INSERT INTO `sys_user` VALUES (13, 'student001', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '张三', NULL, 'zhangsan001@example.com', '13900139001', 0, 2, '2025-10-15 22:21:47', '2025-10-15 22:21:47');
INSERT INTO `sys_user` VALUES (14, 'student002', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '李四', NULL, 'lisi@example.com', '13900139002', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:11');
INSERT INTO `sys_user` VALUES (15, 'student003', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '王五', NULL, 'wangwu@example.com', '13900139003', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:11');
INSERT INTO `sys_user` VALUES (16, 'student004', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '赵六', NULL, 'zhaoliu@example.com', '13900139004', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:12');
INSERT INTO `sys_user` VALUES (17, 'student005', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '钱七', NULL, 'qianqi@example.com', '13900139005', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:13');
INSERT INTO `sys_user` VALUES (18, 'student006', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '孙八', NULL, 'sunba@example.com', '13900139006', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:13');
INSERT INTO `sys_user` VALUES (19, 'student007', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '周九', NULL, 'zhoujiu@example.com', '13900139007', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:14');
INSERT INTO `sys_user` VALUES (20, 'student008', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '吴十', NULL, 'wushi@example.com', '13900139008', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:15');
INSERT INTO `sys_user` VALUES (21, 'student009', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '郑十一', NULL, 'zheng11@example.com', '13900139009', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:16');
INSERT INTO `sys_user` VALUES (22, 'student010', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '王十二', NULL, 'wang12@example.com', '13900139010', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:17');
INSERT INTO `sys_user` VALUES (23, 'student011', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '李十三', NULL, 'li13@example.com', '13900139011', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:17');
INSERT INTO `sys_user` VALUES (24, 'student012', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '赵十四', NULL, 'zhao14@example.com', '13900139012', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:18');
INSERT INTO `sys_user` VALUES (25, 'student013', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '钱十五', NULL, 'qian15@example.com', '13900139013', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:18');
INSERT INTO `sys_user` VALUES (26, 'student014', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '孙十六', NULL, 'sun16@example.com', '13900139014', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:19');
INSERT INTO `sys_user` VALUES (27, 'student015', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '周十七', NULL, 'zhou17@example.com', '13900139015', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:19');
INSERT INTO `sys_user` VALUES (28, 'user_28', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '用户28', NULL, 'user28@example.com', '13900000028', 0, 2, '2025-10-15 22:21:33', '2025-10-15 22:21:33');
INSERT INTO `sys_user` VALUES (29, 'user_29', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '用户29', NULL, 'user29@example.com', '13900000029', 0, 2, '2025-10-15 22:21:33', '2025-10-15 22:21:33');
INSERT INTO `sys_user` VALUES (30, 'student016', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '吴十八2', NULL, 'wu18@example.com', '13900139016', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:20');
INSERT INTO `sys_user` VALUES (31, 'student017', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '郑十九0', NULL, 'zheng19@example.com', '13900139017', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:20');
INSERT INTO `sys_user` VALUES (32, 'student018', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '陈二十', NULL, 'chen20@example.com', '13900139018', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:22');
INSERT INTO `sys_user` VALUES (33, 'student019', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '林二一', NULL, 'lin21@example.com', '13900139019', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:21');
INSERT INTO `sys_user` VALUES (34, 'student020', '$2a$10$JJtFy15D9xQm4Sg.Mx43xOPgl6Q4L15krdpRMJXMDww6pvEvEfpym', '黄二二', NULL, 'huang22@example.com', '13900139020', 0, 2, '2025-05-15 03:18:30', '2025-05-15 03:19:22');
INSERT INTO `sys_user` VALUES (141, '00023484', '$2a$10$uaGCnMi1Kh9ab47/rs655O1.lyv8pLEP8VTaAhNMrHBUtSnBLpZzO', '嘻嘻嘻', NULL, '1610494022@qq.com', '18786079386', 0, 1, '2025-05-17 18:35:02', '2025-05-17 18:35:02');
INSERT INTO `sys_user` VALUES (142, 'hahha', '$2a$10$KCled2Mk/IpYJRanAZJH1eNu/uC768XONFs/Gg2IWqUGAahBpaYYq', '哈哈哈哈', NULL, '1610494022@qq.com', '15484484848', 0, 1, '2025-05-17 18:35:32', '2025-05-17 18:35:32');
INSERT INTO `sys_user` VALUES (143, '20324454', '$2a$10$4u9AgZAy75zqSd4SJaOi2Oyj4NMvJ7tSadxryN2lcZxmb20g0pbnW', '张力王', NULL, '1610494022@qq.com', '15481654852', 0, 2, '2025-05-17 18:41:46', '2025-05-17 18:41:46');
INSERT INTO `sys_user` VALUES (144, '203245', '$2a$10$22ykkKeiBNKg5N5AKslF5O6aYdEGGZYOR9fZtn8BPPZukNXaskPZ.', '找那个', NULL, '1610494022@qq.com', '18786079386', 0, 1, '2025-05-17 18:43:32', '2025-05-17 18:43:32');
INSERT INTO `sys_user` VALUES (146, '00215', '$2a$10$MH.H8GIr1vSWAmPCL2heI.MPRtr.mEW3ipYHeWXyWOZ8Oakbwtw7i', 'zhangsan', NULL, '1610494022@qq.com', '15484484848', 0, 1, '2025-05-17 18:46:50', '2025-05-17 18:46:50');
INSERT INTO `sys_user` VALUES (147, 'zhangsan', '$2a$10$dLC9jGpD3Zfb.JJvPPReDuRJVWmnRcv4CBh86Tc5g4OsETeSSiP7S', 'zhangsan', NULL, '', '', 0, 2, '2025-09-29 16:05:47', '2025-09-29 16:05:47');
INSERT INTO `sys_user` VALUES (148, 'lisi', '$2a$10$LzksGKi4ZGne/vZAk20Qf.S5FI3nBz0aYHUcrF1ntvpZyZ5XNTrte', 'lisi', NULL, '', '', 0, 2, '2025-09-29 18:31:41', '2025-09-29 18:31:41');
INSERT INTO `sys_user` VALUES (149, 'S202301001', '$2a$10$.5cwTaYk9e2VEW4QeZufuO3ujIufWmKPzLLDeTOeBzLTIGYe91cNq', '张五', NULL, 'zhangsan@example.com', '13800000001', 0, 2, '2025-10-08 18:32:27', '2025-10-08 18:32:27');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `fk_user_role_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 2);
INSERT INTO `sys_user_role` VALUES (4, 2);
INSERT INTO `sys_user_role` VALUES (5, 2);
INSERT INTO `sys_user_role` VALUES (6, 2);
INSERT INTO `sys_user_role` VALUES (7, 2);
INSERT INTO `sys_user_role` VALUES (8, 2);
INSERT INTO `sys_user_role` VALUES (9, 2);
INSERT INTO `sys_user_role` VALUES (10, 2);
INSERT INTO `sys_user_role` VALUES (11, 2);
INSERT INTO `sys_user_role` VALUES (12, 2);
INSERT INTO `sys_user_role` VALUES (141, 2);
INSERT INTO `sys_user_role` VALUES (142, 2);
INSERT INTO `sys_user_role` VALUES (144, 2);
INSERT INTO `sys_user_role` VALUES (146, 2);
INSERT INTO `sys_user_role` VALUES (13, 3);
INSERT INTO `sys_user_role` VALUES (14, 3);
INSERT INTO `sys_user_role` VALUES (15, 3);
INSERT INTO `sys_user_role` VALUES (16, 3);
INSERT INTO `sys_user_role` VALUES (17, 3);
INSERT INTO `sys_user_role` VALUES (18, 3);
INSERT INTO `sys_user_role` VALUES (19, 3);
INSERT INTO `sys_user_role` VALUES (20, 3);
INSERT INTO `sys_user_role` VALUES (21, 3);
INSERT INTO `sys_user_role` VALUES (22, 3);
INSERT INTO `sys_user_role` VALUES (23, 3);
INSERT INTO `sys_user_role` VALUES (24, 3);
INSERT INTO `sys_user_role` VALUES (25, 3);
INSERT INTO `sys_user_role` VALUES (26, 3);
INSERT INTO `sys_user_role` VALUES (27, 3);
INSERT INTO `sys_user_role` VALUES (28, 3);
INSERT INTO `sys_user_role` VALUES (29, 3);
INSERT INTO `sys_user_role` VALUES (30, 3);
INSERT INTO `sys_user_role` VALUES (31, 3);
INSERT INTO `sys_user_role` VALUES (32, 3);
INSERT INTO `sys_user_role` VALUES (143, 3);
INSERT INTO `sys_user_role` VALUES (147, 3);
INSERT INTO `sys_user_role` VALUES (148, 3);
INSERT INTO `sys_user_role` VALUES (149, 3);

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `teacher_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师编号',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `department_id` bigint NOT NULL COMMENT '所属院系ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称',
  `gender` tinyint(1) NOT NULL COMMENT '性别 0:男 1:女',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `entry_date` date NOT NULL COMMENT '入职日期',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:在职 1:离职 2:退休',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_teacher_no`(`teacher_no` ASC) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_teacher_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, 'T20210001', 3, 1, '教授', 0, '110101198001010001', '1980-01-01', '2010-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (2, 'T20210002', 4, 1, '教授', 0, '110101198103150002', '1981-03-15', '2012-07-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (3, 'T20210003', 5, 2, '副教授', 0, '110101197905200003', '1979-05-20', '2011-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (4, 'T20210004', 6, 2, '讲师', 1, '110101198507120004', '1985-07-12', '2015-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (5, 'T20210005', 7, 3, '教授', 0, '110101197602280005', '1976-02-28', '2008-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (6, 'T20210006', 8, 3, '副教授', 1, '110101198309180006', '1983-09-18', '2013-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (7, 'T20210007', 9, 4, '讲师', 1, '110101198804220007', '1988-04-22', '2017-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (8, 'T20210008', 10, 4, '教授', 0, '110101197508030008', '1975-08-03', '2009-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (9, 'T20210009', 11, 5, '副教授', 0, '110101198211150009', '1982-11-15', '2014-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (10, 'T20210010', 12, 5, '讲师', 1, '110101198706270010', '1987-06-27', '2016-09-01', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `teacher` VALUES (11, '00023484', 141, 6, '教授', 0, '523154652856451', '2025-05-07', '2025-05-12', 0, '2025-05-17 18:35:02', '2025-05-17 18:35:02');
INSERT INTO `teacher` VALUES (12, 'hahha', 142, 6, '教授', 0, '520103200315485164', '2025-05-26', '2025-05-19', 0, '2025-05-17 18:35:32', '2025-05-17 18:35:32');
INSERT INTO `teacher` VALUES (13, '203245', 144, 6, '教授', 0, '520135484615824685', '2025-05-28', '2025-05-15', 0, '2025-05-17 18:43:33', '2025-05-17 18:43:33');
INSERT INTO `teacher` VALUES (15, '00215', 146, 6, '副教授', 0, '520135484561574', '2025-05-20', '2025-06-02', 0, '2025-05-17 18:46:50', '2025-05-17 18:46:50');

-- ----------------------------
-- Table structure for todo_item
-- ----------------------------
DROP TABLE IF EXISTS `todo_item`;
CREATE TABLE `todo_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0：未完成，1：已完成）',
  `due_date` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `priority` tinyint NOT NULL DEFAULT 0 COMMENT '优先级（0：低，1：中，2：高）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_todo_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '待办事项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of todo_item
-- ----------------------------
INSERT INTO `todo_item` VALUES (1, 1, '审核本学期教学计划', '完成所有院系本学期教学计划的审核工作', 0, '2023-08-25 17:00:00', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (2, 1, '安排期末考试监考老师', '为所有期末考试科目安排监考老师', 0, '2023-12-15 17:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (3, 2, '更新教务系统', '将教务系统升级到最新版本，修复已知bug', 1, '2023-07-30 17:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (4, 3, '备课-高等数学', '准备高等数学第一章的教学内容和课件', 1, '2023-08-30 17:00:00', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (5, 3, '批改作业-高等数学', '批改高等数学第一次作业', 0, '2023-09-15 17:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (6, 3, '准备期中考试题目', '准备高等数学期中考试题目', 0, '2023-10-20 17:00:00', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (7, 4, '批改作业-线性代数', '批改线性代数第二次作业', 0, '2023-09-20 17:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (8, 4, '修改教学大纲', '根据新的培养方案修改线性代数教学大纲', 0, '2023-09-30 17:00:00', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (9, 13, '提交高数作业', '完成高等数学第一章习题并提交', 1, '2023-09-10 23:59:59', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (10, 13, '准备线代测验', '复习线性代数第一、二章内容备考', 0, '2023-09-18 08:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (11, 13, '小组讨论-数据结构', '与小组成员讨论数据结构课程项目', 0, '2023-09-22 15:30:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (12, 14, '提交程序设计作业', '完成Java程序设计第三次实验报告', 0, '2023-09-25 23:59:59', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (13, 14, '预约图书馆研讨室', '为小组讨论预约图书馆研讨室', 1, '2023-09-15 10:00:00', 0, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (14, 15, '申请奖学金', '准备并提交奖学金申请材料', 0, '2023-10-15 17:00:00', 2, '2025-05-15 02:59:39', '2025-05-15 02:59:39');
INSERT INTO `todo_item` VALUES (15, 15, '参加编程比赛', '准备参加校级编程比赛', 0, '2023-11-05 09:00:00', 1, '2025-05-15 02:59:39', '2025-05-15 02:59:39');

SET FOREIGN_KEY_CHECKS = 1;
