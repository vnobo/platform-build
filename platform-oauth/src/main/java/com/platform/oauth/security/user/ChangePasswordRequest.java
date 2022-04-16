package com.platform.oauth.security.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.UserChangePasswordRequest
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/8/4
 */
@Data
public class ChangePasswordRequest implements Serializable {

  @NotBlank(message = "登录用户名[username]不能为空!")
  private String username;

  @NotBlank(message = "新密码[password]不能为空!")
  @Pattern(
      regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
      message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
  private String password;

  @NotBlank(message = "确认密码[newPassword]不能为空!")
  @Pattern(
      regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
      message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
  private String newPassword;
}