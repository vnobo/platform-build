package com.platform.oauth.security.tenant.member;

import com.platform.commons.utils.CriteriaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * com.bootiful.oauth.security.tenant.member.MemberTenantRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberTenantRequest extends MemberTenant {

    @NotNull(message = "用户[users]不能为空!", groups = Users.class)
    private Set<String> users;

    public static MemberTenantRequest withUserCode(String userCode) {
        MemberTenantRequest request = new MemberTenantRequest();
        request.setUserCode(userCode);
        return request;
    }

    public static MemberTenantRequest of(String tenantCode, String userCode) {
        MemberTenantRequest request = withUserCode(userCode);
        request.setTenantCode(tenantCode);
        return request;
    }

    public MemberTenantRequest isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public MemberTenant toMemberTenant() {
        MemberTenant memberTenant = new MemberTenant();
        BeanUtils.copyProperties(this, memberTenant);
        return memberTenant;
    }

    public Criteria toCriteria() {
        return CriteriaUtils.build(this);
    }

    public interface Users {
    }
}