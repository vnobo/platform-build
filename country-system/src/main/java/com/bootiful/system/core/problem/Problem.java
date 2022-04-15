package com.bootiful.system.core.problem;

import com.fasterxml.jackson.databind.JsonNode;
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
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2022/3/17
 */
@Data
@Schema(title = "常见问题")
@Table("sys_common_problem")
public class Problem implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    @Schema(title = "租户ID")
    private Integer tenantId;

    @Schema(title = "租户编码")
    private String tenantCode;

    @Schema(title = "问题标题")
    private String title;

    @Schema(title = "解决方法")
    private String solution;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private String system;

    @Schema(title = "保留字段")
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

