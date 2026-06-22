-- ============================================================
-- 订单模块数据库脚本（雪花算法主键，无外键）
-- 所有表均包含 BaseEntity 基础字段：id, creator, updater, create_time, update_time, deleted
-- 数据删除由应用层控制
-- ============================================================

-- ============================================================
-- 1. 订单表
-- ============================================================
CREATE TABLE IF NOT EXISTS order_info (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单编号',
    user_id         BIGINT        NOT NULL COMMENT '用户ID',
    order_status    VARCHAR(20)   NOT NULL COMMENT '订单状态',
    total_amount    DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    pay_amount      DECIMAL(10,2) NOT NULL COMMENT '应付金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 NOT NULL COMMENT '优惠金额',
    pay_type        VARCHAR(20)   NULL COMMENT '支付方式',
    pay_time        DATETIME      NULL COMMENT '支付时间',
    delivery_time   DATETIME      NULL COMMENT '发货时间',
    finish_time     DATETIME      NULL COMMENT '完成时间',
    cancel_time     DATETIME      NULL COMMENT '取消时间',
    remark          VARCHAR(500)  NULL COMMENT '订单备注',
    creator         VARCHAR(50)   NULL COMMENT '创建人',
    updater         VARCHAR(50)   NULL COMMENT '更新人',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_order_no UNIQUE (order_no)
) COMMENT '订单表' CHARSET = utf8mb4;

CREATE INDEX idx_order_info_user_id ON order_info(user_id);
CREATE INDEX idx_order_info_status ON order_info(order_status);
CREATE INDEX idx_order_info_pay_time ON order_info(pay_time);
CREATE INDEX idx_order_info_create_time ON order_info(create_time);

-- ============================================================
-- 2. 订单商品明细表
-- ============================================================
CREATE TABLE IF NOT EXISTS order_item (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单编号',
    product_id      BIGINT        NOT NULL COMMENT '商品ID',
    product_name    VARCHAR(200)  NOT NULL COMMENT '商品名称',
    product_image   VARCHAR(500)  NULL COMMENT '商品图片',
    price           DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    quantity        INT           NOT NULL COMMENT '购买数量',
    total_price     DECIMAL(10,2) NOT NULL COMMENT '商品小计',
    creator         VARCHAR(50)   NULL COMMENT '创建人',
    updater         VARCHAR(50)   NULL COMMENT '更新人',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '订单商品明细表' CHARSET = utf8mb4;

CREATE INDEX idx_order_item_order_no ON order_item(order_no);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

-- ============================================================
-- 3. 订单收货地址表
-- ============================================================
CREATE TABLE IF NOT EXISTS order_address (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单编号',
    receiver        VARCHAR(50)   NOT NULL COMMENT '收件人',
    phone           VARCHAR(20)   NOT NULL COMMENT '手机号码',
    province        VARCHAR(50)   NULL COMMENT '省份',
    city            VARCHAR(50)   NULL COMMENT '城市',
    area            VARCHAR(50)   NULL COMMENT '区县',
    detail_address  VARCHAR(200)  NULL COMMENT '详细地址',
    creator         VARCHAR(50)   NULL COMMENT '创建人',
    updater         VARCHAR(50)   NULL COMMENT '更新人',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '订单收货地址表' CHARSET = utf8mb4;

CREATE INDEX idx_order_address_order_no ON order_address(order_no);

-- ============================================================
-- 4. 订单支付记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS order_pay (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单编号',
    pay_no          VARCHAR(64)   NULL COMMENT '第三方支付单号',
    pay_amount      DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_status      VARCHAR(20)   NOT NULL COMMENT '支付状态',
    pay_type        VARCHAR(20)   NOT NULL COMMENT '支付方式',
    pay_time        DATETIME      NULL COMMENT '支付时间',
    creator         VARCHAR(50)   NULL COMMENT '创建人',
    updater         VARCHAR(50)   NULL COMMENT '更新人',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '订单支付记录表' CHARSET = utf8mb4;

CREATE INDEX idx_order_pay_order_no ON order_pay(order_no);
CREATE INDEX idx_order_pay_pay_no ON order_pay(pay_no);

-- ============================================================
-- 5. 订单操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS order_log (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL COMMENT '订单编号',
    operate_type    VARCHAR(50)   NOT NULL COMMENT '操作类型',
    operate_desc    VARCHAR(500)  NOT NULL COMMENT '操作描述',
    creator         VARCHAR(50)   NULL COMMENT '创建人',
    updater         VARCHAR(50)   NULL COMMENT '更新人',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
) COMMENT '订单操作日志表' CHARSET = utf8mb4;

CREATE INDEX idx_order_log_order_no ON order_log(order_no);

-- ============================================================
-- 6. MQ 事务消息日志表（通用 Checker 回查用）
-- ============================================================
CREATE TABLE IF NOT EXISTS mq_transaction_log (
    id              BIGINT         NOT NULL COMMENT '主键' PRIMARY KEY,
    msg_id          VARCHAR(64)    NOT NULL COMMENT '消息ID',
    topic           VARCHAR(128)   NOT NULL COMMENT '消息主题',
    tag             VARCHAR(64)    NULL COMMENT '消息标签',
    data_json       TEXT           NOT NULL COMMENT '业务数据JSON',
    status          VARCHAR(16)    DEFAULT 'PENDING' NOT NULL COMMENT '状态：PENDING/COMMITTED/ROLLED_BACK',
    creator         VARCHAR(50)    NULL COMMENT '创建人',
    updater         VARCHAR(50)    NULL COMMENT '更新人',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT        DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT uk_mq_tx_log_msg_id UNIQUE (msg_id)
) COMMENT 'MQ事务消息日志表' CHARSET = utf8mb4;

CREATE INDEX idx_mq_tx_log_status ON mq_transaction_log(status);
CREATE INDEX idx_mq_tx_log_create_time ON mq_transaction_log(create_time);
