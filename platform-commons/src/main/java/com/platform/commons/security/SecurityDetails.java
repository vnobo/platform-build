package com.platform.commons.security;

/**
 * com.bootiful.commons.security.SecurityDetails
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/16
 */
public interface SecurityDetails {

    /**
     * 用户user id
     *
     * @return int userid
     */
    Long getUserId();

    /**
     * 用户登录名
     *
     * @return user.username
     */
    String getUsername();

    /**
     * 默认租户ID
     *
     * @return int tenant id
     */
    Integer getTenantId();

    /**
     * 认证用户权限信息
     *
     * @return 权限集合
     */
    String[] getAuthorities();
}