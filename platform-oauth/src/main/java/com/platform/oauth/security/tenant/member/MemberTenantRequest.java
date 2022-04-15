package com.platform.oauth.security.tenant.member;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * com.bootiful.oauth.security.tenant.member.MemberTenantRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberTenantRequest extends MemberTenant {

    @NotNull(message = "用户[userId]不能为空")
    private Long userId;
    @NotNull(message = "租户[tenantId]不能为空")
    private Integer tenantId;

    @NotNull(message = "租户[isDefault]不能为空")
    private Boolean isDefault;

    private List<Long> userIds;

    public static MemberTenantRequest withUserId(Long userId) {
        MemberTenantRequest groupRequest = new MemberTenantRequest();
        groupRequest.setUserId(userId);
        return groupRequest;
    }

    public MemberTenantRequest isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public static MemberTenantRequest of(Integer tenantId, Long userId) {
        MemberTenantRequest memberTenantRequest = new MemberTenantRequest();
        memberTenantRequest.setTenantId(tenantId);
        memberTenantRequest.setUserId(userId);
        return memberTenantRequest;
    }

    public MemberTenant toMemberTenant() {
        MemberTenant memberTenant = new MemberTenant();
        BeanUtils.copyProperties(this, memberTenant);
        return memberTenant;
    }

    public String toWhereSql() {

        StringBuilder criteria = new StringBuilder();

        if (!ObjectUtils.isEmpty(this.getId())) {
            criteria.append(" and se_tenant_members.id=").append(this.getId());
        }

        if (!ObjectUtils.isEmpty(this.getUserId())) {
            criteria.append(" and se_tenant_members.user_id=").append(this.getUserId());
        }

        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            criteria.append(" and se_tenant_members.tenant_id=").append(this.getTenantId());
        }

        return criteria.toString();
    }
}