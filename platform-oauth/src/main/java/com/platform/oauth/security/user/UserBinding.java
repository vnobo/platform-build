package com.platform.oauth.security.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.Binding
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@Data
public class UserBinding implements Serializable {

  private WeiXin weiXin;

  public static UserBinding withWeiXin(String appId, String openid) {
    UserBinding userBinding = new UserBinding();
    userBinding.setWeiXin(
        WeiXin.builder().appId(appId).openid(openid).type(BindingType.WEIXIN).build());
    return userBinding;
  }

  public enum BindingType {
    /** 微信绑定 */
    WEIXIN
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WeiXin implements Serializable {
    private BindingType type;
    private String appId;
    private String openid;
  }
}