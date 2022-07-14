package com.platform.oauth.security.group;

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
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Schema(name = "角色请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupRequest extends Group implements Serializable {

    private String securityTenantCode;

    public GroupRequest securityTenantCode(String securityCode) {
        this.setSecurityTenantCode(securityCode);
        return this;
    }

    public GroupRequest id(Integer id) {
        this.setId(id);
        return this;
    }

    public Group toGroup() {
        Group group = new Group();
        BeanUtils.copyProperties(this, group);
        return group;
    }

    public Criteria toCriteria() {

        Criteria criteria = CriteriaUtils.build(this, List.of("securityTenantCode"));

        if (StringUtils.hasLength(this.securityTenantCode)) {
            criteria = criteria.and("tenantCode").like(this.securityTenantCode + "%");
        }
        return criteria;
    }
}