package com.platform.gateway.weixin;

import com.platform.commons.utils.SystemType;
import com.platform.gateway.security.RegisterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * com.bootiful.oauth.core.weixin.RegRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxRequest extends RegisterRequest {

  @NotNull(message = "微信用户绑定手机号[phone]不能为空!")
  private String phone;

  @NotBlank(message = "微信用户[openid]不能为空!")
  private String openid;

  private String appId;

  private SystemType system;

  public WxRequest appId(String appId) {
    this.setAppId(appId);
    return this;
  }

  public WxRequest system(SystemType system) {
    this.setSystem(system);
    return this;
  }

  public WxRequest toRegister() {
    this.setUsername(getPhone());
    this.setEnabled(true);
    return this;
  }
}