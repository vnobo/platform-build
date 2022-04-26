package com.platform.commons.security;

import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.SimplerSecurityDetails
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/10
 */
@Data
public class SimplerSecurityDetails implements SecurityDetails, Serializable {

  private String username;
  private String[] authorities;
  private Long userId;
  private Integer tenantId;

  public static SimplerSecurityDetails of(Long userId, String username) {

    SimplerSecurityDetails securityDetails = new SimplerSecurityDetails();
    securityDetails.setUserId(userId);
    securityDetails.setUsername(username);
    return securityDetails;
  }

  public SimplerSecurityDetails authorities(String[] authorities) {
    this.setAuthorities(authorities);
    return this;
  }

}