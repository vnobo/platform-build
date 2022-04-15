package com.bootiful.oauth.security.tenant.member;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private Long userId;
    private Integer tenantId;
    private Boolean isDefault;

    @CreatedDate
    private LocalDateTime createdTime;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }

    public MemberTenant isDefault(Boolean bool) {
        this.setIsDefault(bool);
        return this;
    }

}