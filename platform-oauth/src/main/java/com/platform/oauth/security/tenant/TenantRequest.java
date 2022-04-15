package com.platform.oauth.security.tenant;

import com.platform.commons.utils.TenantLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message = "租户[Code]不能为空!取地址CODE码")
    private String code;

    @NotBlank(message = "租户名[name]不能为空!")
    private String name;

    @NotNull(message = "租户父级[ID]不能为空!")
    private Integer pid;

    @NotBlank(message = "租户地址[address]不能为空!")
    private String address;

    private TenantLevel level;
    private Integer tier;
    private String securityCode;

    public static Tenant withId(Integer id) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        return tenant;
    }

    public Tenant toTenant() {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(this, tenant);
        return tenant;
    }

    public TenantRequest id(Integer id) {
        this.setId(id);
        return this;
    }

    public TenantRequest pid(Integer pid) {
        this.setPid(pid);
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

        if (!ObjectUtils.isEmpty(this.getPid()) && this.getPid() > -1) {
            stringBuilder.append(" and pid =").append(this.getPid());
        }

        if (!ObjectUtils.isEmpty(this.getLevel())) {
            stringBuilder.append(" and char_length(code) = ").append(this.getLevel().value());
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