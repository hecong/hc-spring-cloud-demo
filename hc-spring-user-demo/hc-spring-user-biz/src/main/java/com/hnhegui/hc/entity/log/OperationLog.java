package com.hnhegui.hc.entity.log;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_operation_log")
public class OperationLog extends BaseEntity {

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作结果：1-成功，0-失败
     */
    private Integer operationResult;
}
