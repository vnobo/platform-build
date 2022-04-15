package com.platform.system.core.configurations;

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
@Schema(title = "租户配置")
@Data
@Table("sys_configurations")
public class Configuration implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    @Schema(title = "租户ID")
    private Integer tenantId;

    @Schema(title = "租户编码")
    private String tenantCode;

    @Schema(title = "租户名称")
    private String name;

    @Schema(title = "配置类型")
    private String type;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    @Schema(title = "配置详细信息!比如: {\"type\":1,\"name\":\"二狗租户\"}")
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