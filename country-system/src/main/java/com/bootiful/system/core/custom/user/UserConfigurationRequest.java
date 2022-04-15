package com.bootiful.system.core.custom.user;

import com.bootiful.commons.utils.SystemType;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.system.core.configurations.ConfigurationRequest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Schema(title = "用户自定义配置请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserConfigurationRequest extends UserConfiguration implements Serializable {

    @NotNull(message = "配置租户[tenantId]不能为空!")
    private Integer tenantId;

    @NotNull(message = "配置租户[tenantCode]不能为空!")
    private String tenantCode;

    @NotNull(message = "配置用户[userId]不能为空!")
    private Long userId;

    @NotNull(message = "配置名称[system]不能为空!")
    private SystemType system;

    @NotNull(message = "配置项[configuration]不能为空!")
    private JsonNode configuration;

    @NotNull(message = "配置类型[type]不能为空!")
    private String type;

    public static UserConfigurationRequest of(Integer tenantId, SystemType system, String type) {
        UserConfigurationRequest configuration1 = new UserConfigurationRequest();
        configuration1.setTenantId(tenantId);
        configuration1.setType(type);
        configuration1.setSystem(system);
        return configuration1;
    }

    public static UserConfigurationRequest of(Integer tenantId, String tenantCode, SystemType name,
                                              JsonNode configuration, String type) {
        UserConfigurationRequest configuration1 = new UserConfigurationRequest();
        configuration1.setTenantId(tenantId);
        configuration1.setTenantCode(tenantCode);
        configuration1.setSystem(name);
        configuration1.setConfiguration(configuration);
        configuration1.setType(type);
        return configuration1;
    }

    public UserConfigurationRequest tenantId(Integer tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public UserConfiguration toConfiguration() {
        UserConfiguration configuration1 = new UserConfiguration();
        BeanUtils.copyProperties(this, configuration1);
        return configuration1;
    }

    public Criteria toCriteria() {
        Criteria criteria = Criteria.empty();

        if (!ObjectUtils.isEmpty(getId())) {
            criteria = criteria.and("id").is(getId());
        }
        if (!ObjectUtils.isEmpty(getTenantId())) {
            criteria = criteria.and("tenantId").is(getTenantId());
        }
        if (!ObjectUtils.isEmpty(getTenantCode())) {
            criteria = criteria.and("tenantCode").is(getTenantCode());
        }
        if (!ObjectUtils.isEmpty(getSystem())) {
            criteria = criteria.and("system").like(getSystem()).ignoreCase(true);
        }
        if (!ObjectUtils.isEmpty(getType())) {
            criteria = criteria.and("type").like(getType()).ignoreCase(true);
        }
        return criteria;
    }

}