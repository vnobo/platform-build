package com.platform.oauth.security;

import java.io.Serializable;

/**
 * com.alex.oauth.security.AuthorityPermissions 权限定义接口,子权限和权限整合
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/20
 */
public interface SimpleAuthority extends Serializable {

  /**
   * 获取权限
   *
   * @return 权限
   */
  String getAuthority();
}