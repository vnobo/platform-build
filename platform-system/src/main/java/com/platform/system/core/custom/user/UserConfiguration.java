package com.platform.system.core.custom.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * com.bootiful.system.core.configurations.Configuration
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Schema(title = "用户自定义配置")
@Data
@Table("sys_user_configurations")
public class UserConfiguration implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    private Integer tenantId;

    private String tenantCode;

    @Schema(title = "用户ID")
    private Long userId;

    @Schema(title = "系统类型")
    private SystemType system;

    @Schema(title = "配置类型")
    private String type;

    @Schema(title = "配置项,JSON")
    private JsonNode configuration;

    @CreatedDate
    private LocalDateTime createdTime;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}