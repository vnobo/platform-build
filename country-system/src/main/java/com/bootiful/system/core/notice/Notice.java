package com.bootiful.system.core.notice;

import com.bootiful.commons.annotation.UserAuditor;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知公告类
 *
 * @author jjh
 * @date 2021/6/7
 */
@Data
@Table("sys_notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "通知公告")
public class Notice implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;

    @Schema(title = "租户ID")
    private Integer tenantId;

    @Schema(title = "租户code")
    private String tenantCode;

    @Schema(title = "系统类型")
    private String system;

    @Schema(title = "类型")
    private String type;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "内容")
    private String content;

    @Schema(title = "保留字段")
    private JsonNode extend;

    @Schema(title = "创建人Id")
    @CreatedBy
    private UserAuditor creator;

    @Schema(title = "修改人")
    @LastModifiedBy
    private UserAuditor updater;

    @CreatedDate
    private LocalDateTime createdTime;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}