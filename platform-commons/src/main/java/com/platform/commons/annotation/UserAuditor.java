package com.platform.commons.annotation;

import com.platform.commons.security.SecurityDetails;
import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.Creators 自动注册认证的用户
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/24
 */
@Data
public class UserAuditor implements Serializable {

    private Long userId;
    private Integer tenantId;
    private String username;

    public static UserAuditor withUserId(Long userId) {
        UserAuditor userAuditor = new UserAuditor();
        userAuditor.setUserId(userId);
        return userAuditor;
    }

    public static UserAuditor withSecurityDetails(SecurityDetails securityDetails) {
        UserAuditor userAuditor = new UserAuditor();
        userAuditor.setUserId(securityDetails.getUserId());
        userAuditor.setUsername(securityDetails.getUsername());
        userAuditor.setTenantId(securityDetails.getTenantId());
        return userAuditor;
    }

}