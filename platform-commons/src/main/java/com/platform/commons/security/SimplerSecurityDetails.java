package com.platform.commons.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import org.springframework.util.ObjectUtils;

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
  private String tenantName;
  private String tenantCode;
  private Integer tier;
  private JsonNode tenantAddressCode;
  private JsonNode tenantAddressText;
  private Set<SecurityDetailsTenant> tenants;

  public static SimplerSecurityDetails of(Long userId, String username) {

    SimplerSecurityDetails securityDetails = new SimplerSecurityDetails();
    securityDetails.setUserId(userId);
    securityDetails.setUsername(username);
    securityDetails.setSecurityLevel(securityLevel);
    return securityDetails;
  }

  public static SimplerSecurityDetails withDefault() {

    return SimplerSecurityDetails.of(-1L, "anonymous", null, -1)
        .tenants(Set.of(SecurityDetailsTenant.withGuest()));
  }

  public SimplerSecurityDetails authorities(String[] authorities) {
    this.setAuthorities(authorities);
    return this;
  }

  public SimplerSecurityDetails tenants(Set<SecurityDetailsTenant> tenants) {

    SecurityDetailsTenant detailsTenant =
        tenants.parallelStream()
            .filter(SecurityDetailsTenant::getIsDefault)
            .findAny()
            .orElse(SecurityDetailsTenant.withDefault());
    this.setTenants(tenants);

    this.setTenantId(detailsTenant.getTenantId());
    this.setTenantCode(detailsTenant.getTenantCode());
    this.setTenantName(detailsTenant.getTenantName());

    var node = detailsTenant.getTenantExtend();
    this.setTenantAddressCode(
        ObjectUtils.isEmpty(node)
            ? new ObjectMapper().createArrayNode()
            : node.withArray("addressCode"));
    this.setTenantAddressText(
        ObjectUtils.isEmpty(node)
            ? new ObjectMapper().createArrayNode()
            : node.withArray("addressText"));
    this.setTier(this.getTenantAddressCode().size());

    if (detailsTenant.getTenantId() > 1000) {
      this.setSecurityLevel(this.getSecurityLevel() + 1);
    }
    return this;
  }
}