package com.platform.gateway.security;

import com.fasterxml.jackson.databind.JsonNode;
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

  private JsonNode extend;

}