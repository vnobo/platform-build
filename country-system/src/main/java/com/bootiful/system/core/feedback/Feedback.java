package com.bootiful.system.core.feedback;

import com.bootiful.commons.utils.SystemType;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
@Data
@Table("sys_feed_back")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "意见反馈")
public class Feedback implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private Integer tenantId;
    private String tenantCode;

    @Schema(title = "租户名称")
    private String tenantName;

    @Schema(title = "创建人Id")
    private Long userId;

    @Schema(title = "意见反馈标题")
    private String title;

    @Schema(title = "意见反馈类型")
    private String type;

    @Schema(title = "意见反馈状态")
    private Integer status;

    @Schema(title = "意见反馈内容")
    private String content;

    @Schema(title = "回复人id")
    private Long replyId;

    @Schema(title = "回复时间")
    private LocalDateTime replyTime;

    @Schema(title = "回复内容")
    private String replyContent;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

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