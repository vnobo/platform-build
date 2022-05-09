package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends User implements Serializable {

    @NotBlank(message = "确认密码[newPassword]不能为空!", groups = ChangePassword.class)
    @Pattern(regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
    private String newPassword;

    private UserBinding binding;
    private String securityTenantCode;

    public static UserRequest withUsername(String username) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(username);
        return userRequest;
    }

    public UserRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public UserRequest tenantCode(String tenantCode) {
        this.setTenantCode(tenantCode);
        return this;
    }

    public UserRequest securityTenantCode(String tenantCode) {
        this.setSecurityTenantCode(tenantCode);
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

        Criteria criteria = Criteria.empty();

        if (!ObjectUtils.isEmpty(this.getId())) {
            criteria = criteria.and(Criteria.where("id").is(this.getId()));
        }

        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            criteria = criteria.and("tenantId").is(this.getTenantId());
        }

        if (StringUtils.hasLength(this.getTenantCode())) {
            criteria = criteria.and("tenantCode").like(this.getTenantCode() + "%");
        }

        if (StringUtils.hasLength(this.securityTenantCode) && !"0".equals(this.securityTenantCode)) {
            criteria = criteria.and("tenantCode").like(this.securityTenantCode + "%");
        }

        if (StringUtils.hasLength(this.getUsername())) {
            criteria = criteria.and("username").like(this.getUsername()).ignoreCase(true);
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