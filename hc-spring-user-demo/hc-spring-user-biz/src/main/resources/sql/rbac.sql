-- 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) COMMENT '角色描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE `sys_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
  `type` VARCHAR(20) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮',
  `path` VARCHAR(200) COMMENT '访问路径',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父权限ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT(20) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化数据
-- 插入默认角色
INSERT INTO `sys_role` (`name`, `code`, `description`) VALUES
('超级管理员', 'admin', '拥有系统所有权限'),
('普通用户', 'user', '拥有基本操作权限');

-- 插入默认权限
INSERT INTO `sys_permission` (`name`, `code`, `type`, `path`, `parent_id`) VALUES
('系统管理', 'sys:manage', 'menu', '/sys', 0),
('用户管理', 'sys:user:manage', 'menu', '/sys/user', 1),
('角色管理', 'sys:role:manage', 'menu', '/sys/role', 1),
('权限管理', 'sys:permission:manage', 'menu', '/sys/permission', 1),
('用户列表', 'sys:user:list', 'button', '/sys/user/list', 2),
('用户添加', 'sys:user:add', 'button', '/sys/user/add', 2),
('用户编辑', 'sys:user:edit', 'button', '/sys/user/edit', 2),
('用户删除', 'sys:user:delete', 'button', '/sys/user/delete', 2);

-- 关联角色权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(2, 1), (2, 2), (2, 5);

-- 插入默认管理员用户
INSERT INTO `sys_user` (`username`, `password`, `name`, `email`, `phone`, `status`) VALUES
('admin', '123456', '管理员', 'admin@example.com', '13800138000', 1),
('user', '123456', '普通用户', 'user@example.com', '13900139000', 1);

-- 关联用户角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2);