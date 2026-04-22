package com.hnhegui.hc.service.enterprise;

import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseCreateBO;
import com.hnhegui.hc.common.enums.EnterpriseStatusEnum;
import com.hnhegui.hc.controller.enterprise.converter.EnterpriseConverter;
import com.hnhegui.hc.entity.enterprise.Enterprise;
import com.hnhegui.hc.mapper.enterprise.EnterpriseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseMapper enterpriseMapper;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 企业状态枚举引用（不再定义常量）
     */

    // ====================== 查询 ======================

    /**
     * 根据ID查询企业
     *
     * @param id 企业ID
     * @return 企业BO
     */
    public EnterpriseBO getEnterpriseById(Long id) {
        Enterprise entity = enterpriseMapper.selectById(id);
        return EnterpriseConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 根据企业编码查询
     *
     * @param enterpriseCode 企业编码
     * @return 企业BO
     */
    public EnterpriseBO getEnterpriseByCode(String enterpriseCode) {
        Enterprise entity = enterpriseMapper.selectByEnterpriseCode(enterpriseCode);
        return EnterpriseConverter.INSTANCE.entityToBo(entity);
    }

    // ====================== 保存/更新 ======================

    /**
     * 生成8位企业编码（数字+字母）
     */
    private String generateEnterpriseCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 创建企业
     *
     * @param createBO 创建参数
     * @return 企业BO
     */
    public EnterpriseBO createEnterprise(EnterpriseCreateBO createBO) {
        // 生成唯一企业编码
        String enterpriseCode;
        do {
            enterpriseCode = generateEnterpriseCode();
        } while (enterpriseMapper.selectByEnterpriseCode(enterpriseCode) != null);

        Enterprise enterprise = EnterpriseConverter.INSTANCE.createBoToEntity(createBO);
        enterprise.setEnterpriseCode(enterpriseCode);
        enterprise.setStatus(EnterpriseStatusEnum.NORMAL.getCode());
        enterprise.setLoginMutualExclusion(createBO.getLoginMutualExclusion() != null ? createBO.getLoginMutualExclusion() : 0);
        enterpriseMapper.insert(enterprise);

        log.info("企业创建成功：code={}, name={}", enterpriseCode, createBO.getName());
        return EnterpriseConverter.INSTANCE.entityToBo(enterprise);
    }

    /**
     * 更新企业信息
     *
     * @param id       企业ID
     * @param createBO 更新参数
     * @return 企业BO
     */
    public EnterpriseBO updateEnterprise(Long id, EnterpriseCreateBO createBO) {
        Enterprise enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new RuntimeException("企业不存在");
        }
        if (createBO.getName() != null) {
            enterprise.setName(createBO.getName());
        }
        if (createBO.getContactPerson() != null) {
            enterprise.setContactPerson(createBO.getContactPerson());
        }
        if (createBO.getContactPhone() != null) {
            enterprise.setContactPhone(createBO.getContactPhone());
        }
        if (createBO.getContactEmail() != null) {
            enterprise.setContactEmail(createBO.getContactEmail());
        }
        if (createBO.getAddress() != null) {
            enterprise.setAddress(createBO.getAddress());
        }
        enterpriseMapper.updateById(enterprise);

        log.info("企业信息更新成功：id={}", id);
        return EnterpriseConverter.INSTANCE.entityToBo(enterprise);
    }

    /**
     * 更新安全设置
     *
     * @param id                   企业ID
     * @param ipWhitelist          IP白名单
     * @param loginMutualExclusion 登录互踢
     * @param passwordRule         密码规则
     */
    public void updateSecuritySettings(Long id, String ipWhitelist,
                                       Integer loginMutualExclusion, String passwordRule) {
        Enterprise enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new RuntimeException("企业不存在");
        }
        if (ipWhitelist != null) {
            enterprise.setIpWhitelist(ipWhitelist);
        }
        if (loginMutualExclusion != null) {
            enterprise.setLoginMutualExclusion(loginMutualExclusion);
        }
        if (passwordRule != null) {
            enterprise.setPasswordRule(passwordRule);
        }
        enterpriseMapper.updateById(enterprise);

        log.info("企业安全设置更新成功：id={}", id);
    }

    /**
     * 更新企业状态
     *
     * @param id     企业ID
     * @param status 状态
     */
    public void updateStatus(Long id, int status) {
        Enterprise enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new RuntimeException("企业不存在");
        }
        enterprise.setStatus(status);
        enterpriseMapper.updateById(enterprise);

        log.info("企业状态更新：id={}, status={}", id, status);
    }

    // ====================== 安全校验 ======================

    /**
     * 校验IP是否在企业白名单内
     *
     * @param enterpriseId 企业ID
     * @param clientIp     客户端IP
     * @return 是否允许
     */
    public boolean isIpAllowed(Long enterpriseId, String clientIp) {
        Enterprise enterprise = enterpriseMapper.selectById(enterpriseId);
        if (enterprise == null) {
            return false;
        }
        String ipWhitelist = enterprise.getIpWhitelist();
        if (ipWhitelist == null || ipWhitelist.isEmpty()) {
            return true;
        }
        String[] allowedIps = ipWhitelist.split(",");
        for (String allowedIp : allowedIps) {
            String trimmed = allowedIp.trim();
            if (trimmed.equals("*") || trimmed.equals(clientIp)) {
                return true;
            }
            if (trimmed.endsWith("*") && clientIp.startsWith(trimmed.substring(0, trimmed.length() - 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查企业是否开启互踢策略
     *
     * @param enterpriseId 企业ID
     * @return 是否开启互踢
     */
    public boolean isLoginMutualExclusion(Long enterpriseId) {
        Enterprise enterprise = enterpriseMapper.selectById(enterpriseId);
        return enterprise != null && enterprise.getLoginMutualExclusion() == 1;
    }

    // ====================== 批量操作 ======================

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertBatch(List<Enterprise> list) {
        return enterpriseMapper.insertBatch(list);
    }

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertOrUpdateBatch(List<Enterprise> list) {
        return enterpriseMapper.insertOrUpdateBatch(list);
    }
}
