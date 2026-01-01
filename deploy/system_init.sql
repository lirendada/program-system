-- 创建数据库
CREATE DATABASE IF NOT EXISTS oj_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE oj_system;

-- ==================================================================================
-- 1. 系统管理模块 (后台管理员)
-- 简化点：移除了角色表、权限表，仅保留管理员账号
-- ==================================================================================

DROP TABLE IF EXISTS `tb_sys_user`;
CREATE TABLE `tb_sys_user` (
                               `user_id` BIGINT NOT NULL COMMENT '管理员ID',
                               `user_account` VARCHAR(50) NOT NULL COMMENT '账号',
                               `password` VARCHAR(255) NOT NULL COMMENT '密码',
                               `nick_name` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                               `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
                               `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                               PRIMARY KEY (`user_id`),
                               UNIQUE KEY `uk_user_account` (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统管理员表';

-- 初始化唯一的超级管理员 (密码: 123456)
INSERT INTO `tb_sys_user` (`user_id`, `user_account`, `password`, `nick_name`) VALUES
    (1, 'admin', '$2a$10$rd71IhdgCfR6IcjNltq2oOXz9fL9uYtwMO1F7fI4yRSsfiQZFBVP2', '超级管理员');


-- ==================================================================================
-- 2. C端用户模块 (普通做题用户)
-- ==================================================================================

DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                           `user_account` VARCHAR(50) NOT NULL COMMENT '账号',
                           `password` VARCHAR(255) NOT NULL COMMENT '密码',
                           `nick_name` VARCHAR(50) COMMENT '昵称',
                           `avatar` VARCHAR(500) COMMENT '头像URL',
                           `email` VARCHAR(100) COMMENT '邮箱',
                           `phone` VARCHAR(20) COMMENT '手机号',
                           `school` VARCHAR(100) COMMENT '学校',
                           `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                           `submitted_count` INT DEFAULT 0 COMMENT '提交次数',
                           `accepted_count` INT DEFAULT 0 COMMENT '通过次数',
                           `rating` INT DEFAULT 1500 COMMENT 'Rating分数',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                           PRIMARY KEY (`user_id`),
                           UNIQUE KEY `uk_user_account` (`user_account`),
                           UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='C端普通用户表';

-- 初始化测试用户 (密码: 123456)
INSERT INTO `tb_user` (`user_id`, `user_account`, `password`, `nick_name`, `email`, `school`, `rating`) VALUES
                                                                                                            (1001, 'user001', '$2a$10$rd71IhdgCfR6IcjNltq2oOXz9fL9uYtwMO1F7fI4yRSsfiQZFBVP2', '选手小明', 'user001@test.com', '清华大学', 1500),
                                                                                                            (1002, 'user002', '$2a$10$rd71IhdgCfR6IcjNltq2oOXz9fL9uYtwMO1F7fI4yRSsfiQZFBVP2', '选手小红', 'user002@test.com', '北京大学', 1600);


-- ==================================================================================
-- 3. 题库管理模块 (核心业务)
-- ==================================================================================

-- 3.1 题目表
DROP TABLE IF EXISTS `tb_problem`;
CREATE TABLE `tb_problem` (
                              `problem_id` BIGINT NOT NULL COMMENT '题目ID',
                              `title` VARCHAR(200) NOT NULL COMMENT '题目标题',
                              `difficulty` TINYINT NOT NULL COMMENT '难度：1-简单 2-中等 3-困难',
                              `description` TEXT NOT NULL COMMENT '题目描述',
                              `input_description` TEXT COMMENT '输入描述',
                              `output_description` TEXT COMMENT '输出描述',
                              `time_limit` INT NOT NULL DEFAULT 1000 COMMENT '时间限制(ms)',
                              `memory_limit` INT NOT NULL DEFAULT 128 COMMENT '内存限制(MB)',
                              `stack_limit` INT DEFAULT 128 COMMENT '栈限制(MB)',
                              `sample_input` TEXT COMMENT '样例输入',
                              `sample_output` TEXT COMMENT '样例输出',
                              `hint` TEXT COMMENT '提示',
                              `source` VARCHAR(200) COMMENT '来源',
                              `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-隐藏 1-正常',
                              `create_by` BIGINT COMMENT '创建人',
                              `update_by` BIGINT COMMENT '更新人',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                              PRIMARY KEY (`problem_id`),
                              KEY `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- 3.2 题目标签表
DROP TABLE IF EXISTS `tb_problem_tag`;
CREATE TABLE `tb_problem_tag` (
                                  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
                                  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
                                  `tag_color` VARCHAR(20) COMMENT '标签颜色',
                                  PRIMARY KEY (`tag_id`),
                                  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目标签表';

-- 3.3 题目-标签关联表
DROP TABLE IF EXISTS `tb_problem_tag_relation`;
CREATE TABLE `tb_problem_tag_relation` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `problem_id` BIGINT NOT NULL COMMENT '题目ID',
                                           `tag_id` BIGINT NOT NULL COMMENT '标签ID',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_problem_tag` (`problem_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目标签关联表';

-- 3.4 测试用例表
DROP TABLE IF EXISTS `tb_test_case`;
CREATE TABLE `tb_test_case` (
                                `case_id` BIGINT NOT NULL COMMENT '测试用例ID',
                                `problem_id` BIGINT NOT NULL COMMENT '题目ID',
                                `input` TEXT NOT NULL COMMENT '输入数据',
                                `output` TEXT NOT NULL COMMENT '期望输出',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`case_id`),
                                KEY `idx_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试用例表';


-- ==================================================================================
-- 4. 竞赛管理模块 (核心业务)
-- ==================================================================================

-- 4.1 竞赛表
DROP TABLE IF EXISTS `tb_contest`;
CREATE TABLE `tb_contest` (
                              `contest_id` BIGINT NOT NULL COMMENT '竞赛ID',
                              `title` VARCHAR(200) NOT NULL COMMENT '竞赛标题',
                              `description` TEXT COMMENT '竞赛描述',
                              `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未开始 1-进行中 2-已结束',
                              `start_time` DATETIME NOT NULL COMMENT '开始时间',
                              `end_time` DATETIME NOT NULL COMMENT '结束时间',
                              `create_by` BIGINT COMMENT '创建人',
                              `update_by` BIGINT COMMENT '更新人',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                              `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                              PRIMARY KEY (`contest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛表';

-- 4.2 竞赛-题目关联表
DROP TABLE IF EXISTS `tb_contest_problem`;
CREATE TABLE `tb_contest_problem` (
                                      `id` BIGINT NOT NULL COMMENT '主键ID',
                                      `contest_id` BIGINT NOT NULL COMMENT '竞赛ID',
                                      `problem_id` BIGINT NOT NULL COMMENT '题目ID',
                                      `display_id` VARCHAR(10) NOT NULL COMMENT '展示编号(A,B,C...)',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_contest_problem` (`contest_id`, `problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛题目关联表';

-- 4.3 竞赛报名表
DROP TABLE IF EXISTS `tb_contest_registration`;
CREATE TABLE `tb_contest_registration` (
                                           `id` BIGINT NOT NULL COMMENT '主键ID',
                                           `contest_id` BIGINT NOT NULL COMMENT '竞赛ID',
                                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_contest_user` (`contest_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛报名表';


-- ==================================================================================
-- 5. 初始化基础数据 (可选)
-- ==================================================================================

-- 插入标签
INSERT INTO `tb_problem_tag` (`tag_id`, `tag_name`, `tag_color`) VALUES
                                                                     (1, '数组', '#1890ff'), (2, '动态规划', '#faad14'), (3, '数学', '#52c41a');

-- 插入示例题目
INSERT INTO `tb_problem` (`problem_id`, `title`, `difficulty`, `description`, `time_limit`, `memory_limit`, `sample_input`, `sample_output`, `create_by`) VALUES
    (1, '两数之和', 1, '计算A+B', 1000, 128, '1 2', '3', 1);

-- 插入示例用例
INSERT INTO `tb_test_case` (`case_id`, `problem_id`, `input`, `output`) VALUES
                                                                            (1, 1, '1 2', '3'), (2, 1, '10 20', '30');

ALTER TABLE `tb_problem`
    ADD COLUMN `submit_num` int(11) NOT NULL DEFAULT 0 COMMENT '提交数' AFTER `difficulty`;

ALTER TABLE `tb_problem`
    ADD COLUMN `accepted_num` int(11) NOT NULL DEFAULT 0 COMMENT '通过数' AFTER `submit_num`;


-- 1. 确保标签数据丰富 (包含颜色)
TRUNCATE TABLE tb_problem_tag;
INSERT INTO `tb_problem_tag` (`tag_id`, `tag_name`, `tag_color`) VALUES
(1, '数组', '#1890ff'),       -- 蓝色
(2, '动态规划', '#faad14'),   -- 橙色
(3, '二分查找', '#52c41a'),   -- 绿色
(4, '哈希表', '#f5222d');     -- 红色

-- 2. 插入几个不同类型的题目
-- 1001: 只有数组 (简单)
-- 1002: 动态规划 + 数组 (中等)
-- 1003: 动态规划 + 二分查找 (困难)
-- 1004: 哈希表 (简单)
TRUNCATE TABLE tb_problem;
INSERT INTO `tb_problem` (`problem_id`, `title`, `difficulty`, `description`, `status`, `create_time`) VALUES
(1001, '两数之和', 1, '给定一个整数数组...', 1, NOW()),
(1002, '最长递增子序列', 2, '给你一个整数数组 nums...', 1, NOW()),
(1003, '最长递增子序列 II', 3, '这是困难版本...', 1, NOW()),
(1004, '有效的字母异位词', 1, '给定两个字符串 s 和 t...', 1, NOW());

-- 3. 建立关联 (这是最关键的)
TRUNCATE TABLE tb_problem_tag_relation;
INSERT INTO `tb_problem_tag_relation` (`problem_id`, `tag_id`) VALUES
(1001, 1),          -- 两数之和 -> 数组
(1002, 1),          -- 最长递增子序列 -> 数组
(1002, 2),          -- 最长递增子序列 -> 动态规划
(1003, 2),          -- 最长递增子序列 II -> 动态规划
(1003, 3),          -- 最长递增子序列 II -> 二分查找
(1004, 4);          -- 有效的字母异位词 -> 哈希表