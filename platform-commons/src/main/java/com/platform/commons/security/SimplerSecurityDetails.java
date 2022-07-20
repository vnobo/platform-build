package com.platform.commons.security;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * com.bootiful.commons.security.SimplerSecurityDetails
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/10
 */
@Data
public class SimplerSecurityDetails implements SecurityDetails, Serializable {
    private String userCode;
    private String username;
    private Set<String> authorities;
    private Set<Tenant> tenants;
    private Set<Group> groups;

    public static SimplerSecurityDetails of(String userCode, String username) {
        SimplerSecurityDetails securityDetails = new SimplerSecurityDetails();
        securityDetails.setUserCode(userCode);
        securityDetails.setUsername(username);
        return securityDetails;
    }

    public SimplerSecurityDetails authorities(Set<String> authorities) {
        this.authorities = authorities;
        return this;
    }

    public SimplerSecurityDetails tenants(Set<?> tenants) {
        this.tenants = tenants.parallelStream().map(input -> {
            Tenant tenant = TenantImpl.of(null, null, null);
            BeanUtils.copyProperties(input, tenant);
            return tenant;
        }).collect(Collectors.toSet());
        return this;
    }

    public SimplerSecurityDetails groups(Set<?> groups) {
        this.groups = groups.parallelStream().map(input -> {
            Group tenant = GroupImpl.of(null, null);
            BeanUtils.copyProperties(input, tenant);
            return tenant;
        }).collect(Collectors.toSet());
        return this;
    }

    @Override
    public String getTenantCode() {
        return this.tenants.parallelStream().filter(Tenant::isEnabled)
                .findAny().orElse(TenantImpl.of("0", null, false)).getCode();
    }

    @Data(staticConstructor = "of")
    static class TenantImpl implements Tenant {
        private final String code;
        private final String name;
        private final Boolean enabled;

        @Override
        public Boolean isEnabled() {
            return enabled;
        }
    }

    @Data(staticConstructor = "of")
    static class GroupImpl implements Group {
        private final String code;
        private final String name;
    }
}