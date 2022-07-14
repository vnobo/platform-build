package com.platform.oauth.security.tenant.member;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.tenant.member.MemberTenant
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Data
@Table("se_tenant_members")
public class MemberTenant implements Serializable, Persistable<Long> {
    @Id
    private Long id;

    @NotBlank(message = "用户[userCode]不能为空!")
    private String userCode;

    @NotBlank(message = "租户[tenantCode]不能为空!")
    private String tenantCode;

    private Boolean isDefault;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }


}