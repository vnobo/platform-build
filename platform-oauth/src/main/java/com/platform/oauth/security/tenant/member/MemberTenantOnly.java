package com.platform.oauth.security.tenant.member;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * com.bootiful.oauth.security.tenant.member.MemberTenantOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberTenantOnly extends MemberTenant implements Serializable {
  private String userName;
  private String tenantCode;
  private String tenantName;
  private JsonNode tenantExtend;
}