-- 创建数据库
CREATE DATABASE IF NOT EXISTS renlian DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE renlian;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    `password` VARCHAR(64) NOT NULL COMMENT '登录密码(MD5)',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色: admin/teacher/student',
    `student_no` VARCHAR(30) DEFAULT NULL COMMENT '学号(学生)',
    `phone` VARCHAR(20) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级(学生)',
    `face_registered` TINYINT DEFAULT 0 COMMENT '0-未注册人脸 1-已注册',
    `status` TINYINT DEFAULT 1 COMMENT '0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (`role`),
    INDEX idx_student_no (`student_no`),
    INDEX idx_class_name (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `course_code` VARCHAR(50) DEFAULT NULL COMMENT '课程编号',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `teacher_name` VARCHAR(50) NOT NULL COMMENT '授课教师姓名',
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '上课班级',
    `classroom` VARCHAR(100) DEFAULT NULL COMMENT '上课教室',
    `week_day` VARCHAR(10) DEFAULT NULL COMMENT '星期几(1-7)',
    `start_time` VARCHAR(10) DEFAULT NULL COMMENT '开始时间 HH:mm',
    `end_time` VARCHAR(10) DEFAULT NULL COMMENT '结束时间 HH:mm',
    `week_start` INT DEFAULT 1 COMMENT '起始周',
    `week_end` INT DEFAULT 20 COMMENT '结束周',
    `status` TINYINT DEFAULT 1 COMMENT '0-未开课 1-已开课',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_teacher_id (`teacher_id`),
    INDEX idx_class_name (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 课程-学生关联表
CREATE TABLE IF NOT EXISTS `course_student` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
    `student_no` VARCHAR(30) DEFAULT NULL COMMENT '学号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_course_id (`course_id`),
    INDEX idx_student_id (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学生关联表';

-- 人脸信息表
CREATE TABLE IF NOT EXISTS `face_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '学生用户ID',
    `face_encoding` TEXT COMMENT '人脸128维特征向量(JSON)',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '人脸照片URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸信息表';

-- 考勤记录表
CREATE TABLE IF NOT EXISTS `attendance_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `course_name` VARCHAR(100) DEFAULT NULL COMMENT '课程名称',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
    `student_no` VARCHAR(30) DEFAULT NULL COMMENT '学号',
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
    `status` TINYINT DEFAULT 0 COMMENT '0-缺勤 1-已签到 2-迟到',
    `sign_time` DATETIME DEFAULT NULL COMMENT '签到时间',
    `week_num` INT DEFAULT NULL COMMENT '第几周',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_course_id (`course_id`),
    INDEX idx_student_id (`student_id`),
    INDEX idx_course_week (`course_id`, `week_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- =============================================
-- 插入默认数据
-- =============================================

-- 默认管理员 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin', 1);

-- 默认教师 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `status`)
VALUES ('teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张老师', 'teacher', '13800138001', 1);

-- 默认学生 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `student_no`, `phone`, `class_name`, `status`)
VALUES ('student01', 'e10adc3949ba59abbe56e057f20f883e', '王小明', 'student', '2024001', '13900139001', '人工智能1班', 1);
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `student_no`, `phone`, `class_name`, `status`)
VALUES ('student02', 'e10adc3949ba59abbe56e057f20f883e', '李小红', 'student', '2024002', '13900139002', '人工智能1班', 1);
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `student_no`, `phone`, `class_name`, `status`)
VALUES ('student03', 'e10adc3949ba59abbe56e057f20f883e', '赵大勇', 'student', '2024003', '13900139003', '人工智能1班', 1);

-- 默认课程
INSERT INTO `course` (`course_name`, `course_code`, `teacher_id`, `teacher_name`, `class_name`, `classroom`, `week_day`, `start_time`, `end_time`, `week_start`, `week_end`)
VALUES ('人工智能导论', 'AI101', 2, '张老师', '人工智能1班', '教学楼301', '1', '08:00', '09:40', 1, 18);

-- 学生选课
INSERT INTO `course_student` (`course_id`, `student_id`, `student_name`, `student_no`)
VALUES (1, 3, '王小明', '2024001');
INSERT INTO `course_student` (`course_id`, `student_id`, `student_name`, `student_no`)
VALUES (1, 4, '李小红', '2024002');
INSERT INTO `course_student` (`course_id`, `student_id`, `student_name`, `student_no`)
VALUES (1, 5, '赵大勇', '2024003');
