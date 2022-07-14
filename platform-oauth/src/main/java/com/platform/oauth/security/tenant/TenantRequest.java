package com.platform.oauth.security.tenant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * com.bootiful.oauth.security.tenant.TenantRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantRequest extends Tenant implements Serializable {

  private String securityCode;

  public Tenant toTenant() {
    Tenant tenant = new Tenant();
    BeanUtils.copyProperties(this, tenant);
    return tenant;
  }

  public TenantRequest id(Integer id) {
    this.setId(id);
    return this;
  }

  public TenantRequest pid(String pid) {
    this.setPCode(pid);
    return this;
  }

  public TenantRequest securityTenantCode(String securityCode) {
    this.setSecurityCode(securityCode);
    return this;
  }

  public String toQuerySql() {
    StringBuilder stringBuilder = new StringBuilder("select * from se_tenants where id>0");
    return buildWhere(stringBuilder);
  }

  public String toCountSql() {
    StringBuilder stringBuilder = new StringBuilder("select count(*) from se_tenants where id > 0");
    return buildWhere(stringBuilder);
  }

  private String buildWhere(StringBuilder stringBuilder) {

    if (!ObjectUtils.isEmpty(this.getId())) {
      stringBuilder.append(" and id =").append(this.getId());
    }

    if (!ObjectUtils.isEmpty(this.getCode())) {
      stringBuilder.append(" and p_code =").append(this.getPCode());
    }

    if (StringUtils.hasLength(this.getName())) {
      stringBuilder.append(" and name ilike ").append("'%").append(getName()).append("%'");
    }

    if (StringUtils.hasLength(this.getCode())) {
      stringBuilder.append(" and code ilike ").append("'").append(getCode()).append("%'");
    }

    if (StringUtils.hasLength(this.getSecurityCode())) {
      stringBuilder.append(" and code ilike ").append("'").append(getSecurityCode()).append("%'");
    }
    return stringBuilder.toString();
  }
}