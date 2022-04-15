package com.platform.system.core.configurations;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.system.core.configurations.ConfigurationRequest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Schema(title = "租户配置请求")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationRequest implements Serializable {

    private Long id;

    @NotNull(message = "配置租户[tenantId]不能为空!")
    private Integer tenantId;

    @NotNull(message = "配置租户[tenantCode]不能为空!")
    private String tenantCode;

    @NotBlank(message = "配置名称[name]不能为空!")
    private String name;

    @NotNull(message = "配置项[configuration]不能为空!")
    private JsonNode configuration;

    @NotNull(message = "配置类型[type]不能为空!")
    private String type;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    @NotNull(message = "配置系统类型[system]不能为空!")
    private SystemType system;

    public ConfigurationRequest tenantId(Integer tenantId) {
        this.setTenantId(tenantId);
        return this;
    }

    public Configuration toConfiguration() {
        Configuration configuration = new Configuration();
        BeanUtils.copyProperties(this, configuration);
        return configuration;
    }

    public Criteria toCriteria() {
        Criteria criteria = Criteria.empty();

        if (!ObjectUtils.isEmpty(id)) {
            criteria = criteria.and("id").is(id);
        }

        if (!ObjectUtils.isEmpty(tenantId)) {
            criteria = criteria.and("tenantId").is(tenantId);
        }

        if (!ObjectUtils.isEmpty(tenantCode)) {
            criteria = criteria.and("tenantCode").is(tenantCode);
        }

        if (!ObjectUtils.isEmpty(type)) {
            criteria = criteria.and("type").like(type);
        }

        if (!ObjectUtils.isEmpty(system)) {
            criteria = criteria.and("system").is(system);
        }

        return criteria;
    }

}