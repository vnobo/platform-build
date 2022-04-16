package com.platform.commons.security;

import com.fasterxml.jackson.databind.JsonNode;

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
   * 用户认证名字
   *
   * @return user.name
   */
  String getName();

  /**
   * 认证等级 如果用户填写真实姓名,和身份证为一级
   *
   * @return 1, 一级, 2, 二级
   */
  Integer getSecurityLevel();

  /**
   * 租户层级
   *
   * @return int userid
   */
  Integer getTier();

  /**
   * 租户名称
   *
   * @return tenant name
   */
  String getTenantName();

  /**
   * 默认租户ID
   *
   * @return int tenant id
   */
  Integer getTenantId();

  /**
   * 租户code
   *
   * @return int userid
   */
  String getTenantCode();

  /**
   * 租户地址 CODE 数组页面返回!
   *
   * @return 租户地址 CODE
   */
  JsonNode getTenantAddressCode();

  /**
   * 租户地址中文显示数组
   *
   * @return 租户地址中文显示数组
   */
  JsonNode getTenantAddressText();

  /**
   * 认证用户权限信息
   *
   * @return 权限集合
   */
  String[] getAuthorities();
}