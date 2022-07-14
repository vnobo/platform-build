package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.platform.commons.utils.CriteriaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.groups.Default;
import java.io.Serializable;
import java.util.List;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequest extends User implements Serializable {

    private String securityTenantCode;

    public UserRequest securityTenantCode(String tenantCode) {
        this.setSecurityTenantCode(tenantCode);
        return this;
    }

    public static UserRequest withUsername(String username) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(username);
        return userRequest;
    }

    public UserRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        if (!ObjectUtils.isEmpty(this.getBinding())) {
            ObjectNode objectNode = ObjectUtils.isEmpty(this.getExtend()) || this.getExtend().isNull()
                    ? new ObjectMapper().createObjectNode() : this.getExtend().deepCopy();
            objectNode.putPOJO("binding", this.getBinding());
            user.setExtend(objectNode);
        }
        return user;
    }

    public Criteria toCriteria() {

        Criteria criteria = CriteriaUtils.build(this, List.of("securityTenantCode", "binding", "newPassword"));

        if (StringUtils.hasLength(this.securityTenantCode)) {
            criteria = criteria.and("tenantCode").like(this.securityTenantCode + "%");
        }

        return criteria;
    }

    interface Register extends Default {
    }

    interface Modify extends Default {
    }

    interface ChangePassword extends Default {
    }
}