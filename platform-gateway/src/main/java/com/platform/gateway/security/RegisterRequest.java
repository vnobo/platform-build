package com.platform.gateway.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.commons.utils.SystemType;
import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.gateway.security.RegisterRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/3/28
 */
@Data
public class RegisterRequest implements Serializable {

  private String username;

  private String password;

  private Boolean enabled;

  private String name;

  private String idCard;

  private String email;

  private String phone;

  private String address;

  private Integer tenantId;

  private String tenantCode;

  private SystemType system;

  private Integer groupId;

  private JsonNode extend;
}