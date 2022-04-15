package com.bootiful.gateway.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.tenant.MemberTenantRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/8
 */
@Data
@Validated
@Builder
public class TenantCutRequest implements Serializable {

    @NotNull(message = "用户[userId]不能为空")
    private Long userId;
    @NotNull(message = "租户[tenantId]不能为空")
    private Integer tenantId;
    @NotNull(message = "租户[isDefault]不能为空")
    private Boolean isDefault;

}