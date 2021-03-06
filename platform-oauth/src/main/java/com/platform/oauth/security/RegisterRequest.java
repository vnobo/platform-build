package com.platform.oauth.security;

import com.platform.oauth.security.user.UserRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * com.bootiful.oauth.core.weixin.RegRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterRequest extends UserRequest implements Serializable {

  @Schema(title = "微信注册微信用户openid")
  private String openid;

  @Schema(title = "微信注册微信APPID")
  private String appId;

  public UserRequest toUserRequest() {
    UserRequest userRequest = new UserRequest();
    BeanUtils.copyProperties(this, userRequest);
    return userRequest;
  }
}