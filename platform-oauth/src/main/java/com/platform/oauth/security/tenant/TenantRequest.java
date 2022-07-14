package com.platform.oauth.security.tenant;

import com.platform.commons.utils.CriteriaUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * com.bootiful.oauth.security.tenant.TenantRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Schema(name = "租户请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantRequest extends Tenant implements Serializable {

    private String securityCode;

    public TenantRequest id(Integer id) {
        this.setId(id);
        return this;
    }

    public TenantRequest securityTenantCode(String securityCode) {
        this.setSecurityCode(securityCode);
        return this;
    }

    public Tenant toTenant() {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(this, tenant);
        return tenant;
    }

    public Criteria toCriteria() {

        Criteria criteria = CriteriaUtils.build(this, List.of("securityCode"));

        if (StringUtils.hasLength(this.securityCode)) {
            criteria = criteria.and("code").like(this.securityCode + "%");
        }

        return criteria;
    }
}