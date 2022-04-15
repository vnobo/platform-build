package com.bootiful.system.core.news;

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
 * 新闻类
 *
 * @author jjh
 * @date 2021/6/15
 */
@Data
@Table("sys_news")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "平台新闻")
public class News implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    private Integer tenantId;

    private String tenantCode;

    @Schema(title = "系统类型")
    private String system;

    @Schema(title = "新闻标题")
    private String title;

    @Schema(title = "新闻类型")
    private String type;

    @Schema(title = "文章状态-审核使用 01新提交需要审核 02新提交不审核 03已审核 04审核不通过")
    private String status;

    @Schema(title = "新闻内容")
    private String content;

    @Schema(title = "新闻来源")
    private String source;

    @Schema(title = "审核人")
    private UserAuditor auditor;

    @Schema(title = "审核时间")
    private LocalDateTime auditTime;

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