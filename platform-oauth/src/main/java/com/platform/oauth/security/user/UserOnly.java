package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * com.bootiful.oauth.security.user.UserOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Data
public class UserOnly implements Serializable {

  private Long id;
  private Integer tenantId;
  private String tenantCode;
  private String username;
  private Boolean enabled;
  private String name;
  private String idCard;
  private String email;
  private String phone;
  private JsonNode extend;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
  private LocalDateTime lastLoginTime;

  public static UserOnly withUser(User user) {
    UserOnly userOnly = new UserOnly();
    BeanUtils.copyProperties(user, userOnly);
    return userOnly;
  }
}