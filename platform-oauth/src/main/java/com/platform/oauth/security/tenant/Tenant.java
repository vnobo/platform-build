package com.platform.oauth.security.tenant;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * com.bootiful.oauth.security.tenant.Tenant
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Schema(name = "租户")
@Data
@Table("se_tenants")
public class Tenant implements Serializable, Persistable<Integer> {
    @Id
    private Integer id;

    @NotBlank(message = "租户[Code]不能为空!")
    private String code;

    @NotNull(message = "租户父级[pCode]不能为空!")
    private String pCode;

    @NotBlank(message = "租户名[name]不能为空!")
    private String name;

    @NotBlank(message = "租户地址[address]不能为空!")
    private String address;

    private String description;

    private JsonNode extend;

    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}