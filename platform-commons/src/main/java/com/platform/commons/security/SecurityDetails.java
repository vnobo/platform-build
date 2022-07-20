package com.platform.commons.security;

import java.util.Set;

/**
 * com.bootiful.commons.security.SecurityDetails
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/16
 */
public interface SecurityDetails {

    /**
     * 用户user code
     *
     * @return user code
     */
    String getUserCode();

    /**
     * 用户登录名
     *
     * @return user.username
     */
    String getUsername();

    /**
     * 默认租户CODE
     *
     * @return String Tenant Code
     */
    String getTenantCode();

    /**
     * 认证用户权限信息
     *
     * @return 权限集合
     */
    Set<String> getAuthorities();

    /**
     * 认证用户权限信息
     *
     * @return 权限集合
     */
    Set<Group> getGroups();

    /**
     * 认证用户权限信息
     *
     * @return 权限集合
     */
    Set<Tenant> getTenants();

    interface Tenant {
        /**
         * 租户code
         *
         * @return code
         */
        String getCode();

        /**
         * 租户名称
         *
         * @return name
         */
        String getName();

        /**
         * 是否启用
         *
         * @return 是否启用
         */
        Boolean isEnabled();
    }

    interface Group {
        /**
         * 租户code
         *
         * @return code
         */
        String getCode();

        /**
         * 租户名称
         *
         * @return name
         */
        String getName();
    }
}