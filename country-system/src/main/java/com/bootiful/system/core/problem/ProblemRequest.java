package com.bootiful.system.core.problem;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2022/3/17
 */
@Data
public class ProblemRequest implements Serializable {

    private Long id;

    @Schema(title = "租户ID")
    private Integer tenantId;

    @Schema(title = "租户编码")
    private String tenantCode;

    @Schema(title = "问题")
    private String title;

    @Schema(title = "解决方法")
    private String solution;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private String system;

    @Schema(title = "保留字段")
    private JsonNode extend;

    public Problem toProblem() {
        Problem problem = new Problem();
        BeanUtils.copyProperties(this, problem);
        return problem;
    }

    public Criteria toCriteria() {
        Criteria criteria = Criteria.empty();

        if (!ObjectUtils.isEmpty(id)) {
            criteria = criteria.and("id").is(id);
        }

        if (!ObjectUtils.isEmpty(tenantId)) {
            criteria = criteria.and("tenantId").is(tenantId);
        }

        if (!ObjectUtils.isEmpty(tenantCode) && !"0".equals(tenantCode)) {
            criteria = criteria.and("tenantCode").like(tenantCode + "%");
        }

        if (!ObjectUtils.isEmpty(title)) {
            criteria = criteria.and("title").like("%" + title + "%");
        }

        if (!ObjectUtils.isEmpty(system)) {
            criteria = criteria.and("system").like("%" + system + "%");
        }

        return criteria;
    }
}
