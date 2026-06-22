-- ============================================================
-- C端/B端用户模块数据库脚本（雪花算法主键，无外键）
-- 所有表均包含 BaseEntity 基础字段：id, creator, updater, create_time, update_time, deleted
-- 数据删除由应用层控制
-- ============================================================

-- ============================================================
-- 1. C端用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.c_user (
    id                   BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    phone                VARCHAR(20)  NOT NULL COMMENT '手机号',
    email                VARCHAR(100) NULL COMMENT '邮箱',
    username             VARCHAR(50)  NULL COMMENT '用户名',
    password             VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    nickname             VARCHAR(50)  NULL COMMENT '昵称',
    avatar               VARCHAR(500) NULL COMMENT '头像URL',
    gender               TINYINT      DEFAULT 0 NULL COMMENT '性别：0-未知，1-男，2-女',
    birthday             DATE         NULL COMMENT '生日',
    status               TINYINT      DEFAULT 1 NOT NULL COMMENT '状态：1-正常，2-锁定，3-禁用',
    password_error_count INT          DEFAULT 0 NOT NULL COMMENT '密码连续错误次数',
    lock_time            DATETIME     NULL COMMENT '账号锁定时间',
    remember_login       TINYINT(1)   DEFAULT 0 NULL COMMENT '是否开启免密登录：0-否，1-是',
    identity_default     VARCHAR(200) NULL COMMENT '默认登录身份(JSON存储)',
    creator              VARCHAR(50)  NULL COMMENT '创建人',
    updater              VARCHAR(50)  NULL COMMENT '更新人',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time          DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted              TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_c_user_phone UNIQUE (phone),
    CONSTRAINT uk_c_user_email UNIQUE (email),
    CONSTRAINT uk_c_user_username UNIQUE (username)
) COMMENT 'C端用户表' CHARSET = utf8mb4;

-- ============================================================
-- 2. C端第三方绑定表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.c_user_third_party (
    id              BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    user_id         BIGINT       NOT NULL COMMENT 'C端用户ID',
    platform        VARCHAR(20)  NOT NULL COMMENT '第三方平台：wechat/alipay/qq',
    open_id         VARCHAR(100) NOT NULL COMMENT '第三方openId',
    union_id        VARCHAR(100) NULL COMMENT '第三方unionId',
    bind_nickname   VARCHAR(100) NULL COMMENT '第三方昵称',
    bind_avatar     VARCHAR(500) NULL COMMENT '第三方头像',
    creator         VARCHAR(50)  NULL COMMENT '创建人',
    updater         VARCHAR(50)  NULL COMMENT '更新人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_third_party_openid UNIQUE (platform, open_id)
) COMMENT 'C端第三方绑定表' CHARSET = utf8mb4;

CREATE INDEX idx_c_user_third_party_user_id ON user.c_user_third_party(user_id);

-- ============================================================
-- 3. B端企业表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.biz_enterprise (
    id                      BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    enterprise_code         VARCHAR(8)   NOT NULL COMMENT '企业编码(8位数字+字母)',
    name                    VARCHAR(100) NOT NULL COMMENT '企业名称',
    contact_person          VARCHAR(50)  NULL COMMENT '联系人',
    contact_phone           VARCHAR(20)  NULL COMMENT '联系电话',
    contact_email           VARCHAR(100) NULL COMMENT '联系邮箱',
    address                 VARCHAR(500) NULL COMMENT '企业地址',
    status                  TINYINT      DEFAULT 1 NOT NULL COMMENT '状态：1-正常，2-过期，3-禁用',
    valid_date              DATE         NULL COMMENT '有效期',
    ip_whitelist            TEXT         NULL COMMENT 'IP白名单(JSON数组)',
    login_mutual_exclusion  TINYINT(1)   DEFAULT 0 NOT NULL COMMENT '是否开启登录互踢：0-否，1-是',
    password_rule           VARCHAR(200) NULL COMMENT '登录密码规则(JSON配置)',
    creator                 VARCHAR(50)  NULL COMMENT '创建人',
    updater                 VARCHAR(50)  NULL COMMENT '更新人',
    create_time             DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time             DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                 TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_enterprise_code UNIQUE (enterprise_code)
) COMMENT 'B端企业表' CHARSET = utf8mb4;

-- ============================================================
-- 4. B端企业用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.biz_enterprise_user (
    id                     BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    enterprise_id          BIGINT       NOT NULL COMMENT '企业ID',
    username               VARCHAR(50)  NOT NULL COMMENT '用户名',
    password               VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    name                   VARCHAR(50)  NOT NULL COMMENT '姓名',
    phone                  VARCHAR(20)  NULL COMMENT '手机号',
    email                  VARCHAR(100) NULL COMMENT '邮箱',
    status                 TINYINT      DEFAULT 4 NOT NULL COMMENT '状态：1-正常，2-锁定，3-禁用，4-未激活，5-已离职',
    password_error_count   INT          DEFAULT 0 NOT NULL COMMENT '密码连续错误次数',
    lock_time              DATETIME     NULL COMMENT '账号锁定时间',
    is_first_login         TINYINT(1)   DEFAULT 1 NOT NULL COMMENT '是否首次登录：0-否，1-是',
    activation_expire_time DATETIME     NULL COMMENT '激活有效期(7天)',
    creator                VARCHAR(50)  NULL COMMENT '创建人',
    updater                VARCHAR(50)  NULL COMMENT '更新人',
    create_time            DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time            DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_euser_ent_username UNIQUE (enterprise_id, username)
) COMMENT 'B端企业用户表' CHARSET = utf8mb4;

CREATE INDEX idx_euser_enterprise_id ON user.biz_enterprise_user(enterprise_id);
CREATE INDEX idx_euser_phone ON user.biz_enterprise_user(phone);

-- ============================================================
-- 5. 统一登录日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_login_log (
    id              BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    user_type       VARCHAR(10)  NOT NULL COMMENT '用户类型：C-C端，B-B端，P-平台',
    user_id         BIGINT       NOT NULL COMMENT '用户ID',
    account         VARCHAR(100) NOT NULL COMMENT '登录账号',
    login_time      DATETIME     NOT NULL COMMENT '登录时间',
    login_ip        VARCHAR(50)  NULL COMMENT '登录IP',
    login_location  VARCHAR(100) NULL COMMENT '登录地点',
    login_device    VARCHAR(200) NULL COMMENT '登录设备信息',
    login_status    TINYINT      NOT NULL COMMENT '登录状态：1-成功，0-失败',
    fail_reason     VARCHAR(200) NULL COMMENT '失败原因',
    creator         VARCHAR(50)  NULL COMMENT '创建人',
    updater         VARCHAR(50)  NULL COMMENT '更新人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '统一登录日志表' CHARSET = utf8mb4;

CREATE INDEX idx_login_log_user ON user.sys_login_log(user_type, user_id);
CREATE INDEX idx_login_log_time ON user.sys_login_log(login_time);

-- ============================================================
-- 6. B端操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_operation_log (
    id                BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    enterprise_id     BIGINT       NOT NULL COMMENT '企业ID',
    user_id           BIGINT       NOT NULL COMMENT '操作用户ID',
    username          VARCHAR(50)  NOT NULL COMMENT '操作用户名',
    operation_type    VARCHAR(50)  NOT NULL COMMENT '操作类型',
    operation_content VARCHAR(500) NOT NULL COMMENT '操作内容',
    operation_ip      VARCHAR(50)  NULL COMMENT '操作IP',
    operation_result  TINYINT      NOT NULL COMMENT '操作结果：1-成功，0-失败',
    creator           VARCHAR(50)  NULL COMMENT '创建人',
    updater           VARCHAR(50)  NULL COMMENT '更新人',
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT 'B端操作日志表' CHARSET = utf8mb4;

CREATE INDEX idx_operation_log_ent ON user.sys_operation_log(enterprise_id);
CREATE INDEX idx_operation_log_time ON user.sys_operation_log(create_time);

-- ============================================================
-- 7. 验证码记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_verification_code (
    id          BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    target      VARCHAR(100) NOT NULL COMMENT '发送目标(手机号/邮箱)',
    code        VARCHAR(10)  NOT NULL COMMENT '验证码',
    scene       VARCHAR(20)  NOT NULL COMMENT '场景：login/register/reset',
    status      TINYINT      DEFAULT 1 NOT NULL COMMENT '状态：1-有效，2-已使用，3-已过期',
    request_ip  VARCHAR(50)  NULL COMMENT '请求IP',
    expire_time DATETIME     NOT NULL COMMENT '过期时间',
    creator     VARCHAR(50)  NULL COMMENT '创建人',
    updater     VARCHAR(50)  NULL COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '验证码记录表' CHARSET = utf8mb4;

CREATE INDEX idx_verify_code_target ON user.sys_verification_code(target, scene);

-- ============================================================
-- 8. 账号锁定记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_account_lock (
    id          BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    user_type   VARCHAR(10)  NOT NULL COMMENT '用户类型：C-C端，B-B端',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    account     VARCHAR(100) NOT NULL COMMENT '锁定账号',
    lock_reason VARCHAR(50)  NOT NULL COMMENT '锁定原因：password_error/abnormal_login',
    unlock_time DATETIME     NULL COMMENT '预计解锁时间',
    status      TINYINT      DEFAULT 1 NOT NULL COMMENT '状态：1-锁定中，2-已解锁',
    creator     VARCHAR(50)  NULL COMMENT '创建人',
    updater     VARCHAR(50)  NULL COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '账号锁定记录表' CHARSET = utf8mb4;

CREATE INDEX idx_account_lock_user ON user.sys_account_lock(user_type, user_id);

-- ============================================================
-- 9. 部门表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_dept (
    id            BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    parent_id     BIGINT       DEFAULT 0 NOT NULL COMMENT '父部门ID（0=顶级部门）',
    dept_name     VARCHAR(100) NOT NULL COMMENT '部门名称',
    ancestors     VARCHAR(500) NULL COMMENT '祖级ID列表（逗号分隔，如 0,1,5）',
    sort_order    INT          DEFAULT 0 NOT NULL COMMENT '排序',
    status        TINYINT      DEFAULT 1 NOT NULL COMMENT '状态：0-禁用，1-正常',
    enterprise_id BIGINT       NOT NULL COMMENT '企业ID（租户隔离）',
    creator       VARCHAR(50)  NULL COMMENT '创建人',
    updater       VARCHAR(50)  NULL COMMENT '更新人',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '部门表' CHARSET = utf8mb4;

CREATE INDEX idx_sys_dept_parent ON user.sys_dept(parent_id);
CREATE INDEX idx_sys_dept_enterprise ON user.sys_dept(enterprise_id);

-- ============================================================
-- 10. 用户-部门关联表（多对多）
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_user_dept (
    id            BIGINT NOT NULL COMMENT '主键' PRIMARY KEY,
    user_id       BIGINT NOT NULL COMMENT '用户ID',
    dept_id       BIGINT NOT NULL COMMENT '部门ID',
    enterprise_id BIGINT NOT NULL COMMENT '企业ID（租户隔离）',
    CONSTRAINT uk_user_dept UNIQUE (user_id, dept_id)
) COMMENT '用户-部门关联表' CHARSET = utf8mb4;

CREATE INDEX idx_sys_user_dept_dept ON user.sys_user_dept(dept_id);
CREATE INDEX idx_sys_user_dept_enterprise ON user.sys_user_dept(enterprise_id);

-- ============================================================
-- 11. 角色-权限数据范围表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_role_permission_data_scope (
    id            BIGINT       NOT NULL COMMENT '主键' PRIMARY KEY,
    role_id       BIGINT       NOT NULL COMMENT '角色ID',
    permission_id BIGINT       NOT NULL COMMENT '菜单/权限ID',
    data_scope    VARCHAR(50)  NOT NULL COMMENT '数据范围：ALL|DEPT_AND_CHILDREN|CUSTOM_DEPT|CURRENT_DEPT|SELF',
    enterprise_id BIGINT       NOT NULL COMMENT '企业ID（租户隔离）',
    creator       VARCHAR(50)  NULL COMMENT '创建人',
    updater       VARCHAR(50)  NULL COMMENT '更新人',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_role_perm_scope UNIQUE (role_id, permission_id)
) COMMENT '角色-权限数据范围表' CHARSET = utf8mb4;

CREATE INDEX idx_scope_enterprise ON user.sys_role_permission_data_scope(enterprise_id);

-- ============================================================
-- 12. 角色-权限自定义部门范围明细表
-- ============================================================
CREATE TABLE IF NOT EXISTS user.sys_role_perm_dept_scope (
    id       BIGINT NOT NULL COMMENT '主键' PRIMARY KEY,
    scope_id BIGINT NOT NULL COMMENT '关联 sys_role_permission_data_scope.id',
    dept_id  BIGINT NOT NULL COMMENT '指定可访问的部门ID'
) COMMENT '角色-权限自定义部门范围明细表' CHARSET = utf8mb4;

CREATE INDEX idx_dept_scope_scope ON user.sys_role_perm_dept_scope(scope_id);
CREATE INDEX idx_dept_scope_dept ON user.sys_role_perm_dept_scope(dept_id);
