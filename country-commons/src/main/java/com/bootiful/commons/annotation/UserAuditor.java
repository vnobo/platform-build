package com.bootiful.commons.annotation;

import com.bootiful.commons.security.SecurityDetails;
import com.bootiful.commons.utils.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.Creators
 * 自动注册认证的用户
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/24
 */
@Data
public class UserAuditor implements Serializable {

    private Long userId;
    private Integer tenantId;
    private String tenantCode;
    private String username;
    private String email;
    private String phone;
    private String name;
    private String idCard;

    public static UserAuditor withUser(User user) {
        UserAuditor userAuditor = new UserAuditor();
        BeanUtils.copyProperties(user, userAuditor);
        userAuditor.setUserId(user.getId());
        return userAuditor;
    }

    public static UserAuditor withUserId(Long userId) {
        UserAuditor userAuditor = new UserAuditor();
        userAuditor.setUserId(userId);
        return userAuditor;
    }

    public static UserAuditor withSecurityDetails(SecurityDetails securityDetails) {
        UserAuditor userAuditor = new UserAuditor();
        userAuditor.setUserId(securityDetails.getUserId());
        userAuditor.setName(securityDetails.getName());
        userAuditor.setTenantCode(securityDetails.getTenantCode());
        userAuditor.setUsername(securityDetails.getUsername());
        userAuditor.setTenantId(securityDetails.getTenantId());
        return userAuditor;
    }

    public UserAuditor name(String name) {
        this.setName(name);
        return this;
    }

}