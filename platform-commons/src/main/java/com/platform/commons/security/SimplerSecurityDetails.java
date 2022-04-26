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
public class SimplerSecurityDetails implements SecurityDetails, Serializable {
    private Long userId;
    private String username;
    private Set<String> authorities;
    private Set<Tenant> tenants;

    public static SimplerSecurityDetails of(Long userId, String username) {
        SimplerSecurityDetails securityDetails = new SimplerSecurityDetails();
        securityDetails.setUserId(userId);
        securityDetails.setUsername(username);
        return securityDetails;
    }

    public SimplerSecurityDetails authorities(Set<String> authorities) {
        this.authorities = authorities;
        return this;
    }

    public SimplerSecurityDetails tenants(Set<?> tenants) {
        this.tenants = tenants.parallelStream().map(input -> {
            Tenant tenant = Tenant.of();
            BeanUtils.copyProperties(input, tenant);
            return tenant;
        }).collect(Collectors.toSet());
        return this;
    }

    @Override
    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Integer getTenantId() {
        return this.tenants.parallelStream().filter(Tenant::getIsDefault)
                .findAny().orElse(new Tenant()).getId();
    }

    @Override
    public String getTenantCode() {
        return this.tenants.parallelStream().filter(Tenant::getIsDefault)
                .findAny().orElse(new Tenant()).getCode();
    }

    @Override
    public Set<String> getAuthorities() {
        return this.authorities;
    }

    @Data(staticConstructor = "of")
    private static class Tenant {
        private Integer id;
        private String code;
        private String name;
        private Boolean isDefault;
    }
}