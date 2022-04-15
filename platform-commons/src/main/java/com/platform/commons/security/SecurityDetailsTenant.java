package com.platform.commons.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.SecurityDetailsTenant
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/16
 */
@Data
public class SecurityDetailsTenant implements Serializable {

    private Long userId;
    private String userName;
    private Integer tenantId;
    private String tenantCode;
    private String tenantName;
    private Boolean isDefault;
    private JsonNode tenantExtend;

    public static SecurityDetailsTenant of(Integer tenantId, String code, String tenantName, Boolean isDefault,
                                           Long userId, String name, JsonNode extend) {
        SecurityDetailsTenant detailsTenant = new SecurityDetailsTenant();
        detailsTenant.setTenantId(tenantId);
        detailsTenant.setTenantName(tenantName);
        detailsTenant.setTenantCode(code);
        detailsTenant.setUserName(name);
        detailsTenant.setIsDefault(isDefault);
        detailsTenant.setUserId(userId);
        detailsTenant.setTenantExtend(extend);
        return detailsTenant;
    }

    public static SecurityDetailsTenant withDefault() {
        return SecurityDetailsTenant.of(-1, "0", "访客租户", true, -1L,
                "anonymous", new ObjectMapper().createObjectNode());
    }

    public static SecurityDetailsTenant withGuest() {
        return SecurityDetailsTenant.of(-1, "0", "访客租户", true, -1L,
                "anonymous", new ObjectMapper().createObjectNode());
    }
}