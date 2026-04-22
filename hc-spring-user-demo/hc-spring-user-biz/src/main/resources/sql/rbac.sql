create table if not exists user.sys_permission
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(50)                        not null comment '权限名称',
    code        varchar(50)                        not null comment '权限编码',
    type        varchar(20)                        not null comment '权限类型：menu-菜单，button-按钮',
    path        varchar(200)                       null comment '访问路径',
    parent_id   bigint   default 0                 null comment '父权限ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_code
        unique (code)
)
    comment '权限表' charset = utf8mb4;

create table if not exists user.sys_role
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(50)                        not null comment '角色名称',
    code        varchar(50)                        not null comment '角色编码',
    description varchar(200)                       null comment '角色描述',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_code
        unique (code)
)
    comment '角色表' charset = utf8mb4;

create table if not exists user.sys_role_permission
(
    id            bigint auto_increment comment '主键'
        primary key,
    role_id       bigint not null comment '角色ID',
    permission_id bigint not null comment '权限ID',
    constraint uk_role_permission
        unique (role_id, permission_id),
    constraint sys_role_permission_ibfk_1
        foreign key (role_id) references user.sys_role (id)
            on delete cascade,
    constraint sys_role_permission_ibfk_2
        foreign key (permission_id) references user.sys_permission (id)
            on delete cascade
)
    comment '角色权限关联表' charset = utf8mb4;

create index permission_id
    on user.sys_role_permission (permission_id);

create table if not exists user.sys_user
(
    id          bigint auto_increment comment '主键'
        primary key,
    username    varchar(50)                          not null comment '用户名',
    password    varchar(100)                         not null comment '密码',
    name        varchar(50)                          not null comment '姓名',
    email       varchar(100)                         null comment '邮箱',
    phone       varchar(20)                          null comment '手机号',
    status      tinyint(1) default 1                 null comment '状态：1-启用，0-禁用',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    creator     varchar(50)                          null comment '创建人id',
    updater     varchar(50)                          null comment '更新人id',
    deleted     tinyint    default 0                 null comment '逻辑删除标志（0-未删除，1-已删除)',
    constraint uk_username
        unique (username)
)
    comment '用户表' charset = utf8mb4;

create table if not exists user.sys_user_role
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint                              not null comment '用户ID',
    role_id     bigint                              not null comment '角色ID',
    creator     varchar(64)                         null,
    create_time datetime  default CURRENT_TIMESTAMP null,
    updater     varchar(64)                         null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    deleted     tinyint   default 0                 null,
    constraint uk_user_role
        unique (user_id, role_id),
    constraint sys_user_role_ibfk_1
        foreign key (user_id) references user.sys_user (id)
            on delete cascade,
    constraint sys_user_role_ibfk_2
        foreign key (role_id) references user.sys_role (id)
            on delete cascade
)
    comment '用户角色关联表' charset = utf8mb4;

create index role_id
    on user.sys_user_role (role_id);

-- ============================================================
-- 初始管理员账号，密码：Admin@123（BCrypt 哈希）
-- 注意：以下 BCrypt 哈希值为示例，首次部署时建议通过应用接口创建用户以确保哈希正确
-- ============================================================
INSERT INTO user.sys_user (username, password, name, status)
VALUES ('admin', '$2a$10$EqKcp1WFKVN0kPMJgE0y3Oe7wYvI6gRQ0m0X5FLhQGqJ3vBmWfDHe', '管理员', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- ============================================================
-- 存量数据迁移：将现有明文密码转为 BCrypt 哈希
-- 注意：此脚本需要根据实际存量用户的明文密码逐条替换
-- 若无法获取原始明文密码，建议要求用户首次登录时强制重置密码
-- ============================================================
-- 示例：UPDATE user.sys_user SET password = '$2a$10$...' WHERE username = 'existing_user';
