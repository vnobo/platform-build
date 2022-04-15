package com.bootiful.system.core.news;

import com.bootiful.commons.annotation.UserAuditor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 新闻类
 *
 * @author jjh
 * @date 2021/6/15
 */
@Schema(title = "新闻查询")
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsSearch extends NewsRequest {

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Schema(title = "创建人Id")
    private UserAuditor creator;

    @Schema(title = "修改人")
    private UserAuditor updater;

    private String securityCode;

    public NewsSearch securityCode(String securityCode) {
        this.setSecurityCode(securityCode);
        return this;
    }

    public Criteria toCriteria() {

        Criteria criteria = Criteria.empty();

        if (!ObjectUtils.isEmpty(this.getId())) {
            criteria = criteria.and("id").is(this.getId());
        }

        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            criteria = criteria.and("tenantId").is(this.getTenantId());
        }

        if (StringUtils.hasLength(this.getTitle())) {
            criteria = criteria.and("title").like("%" + this.getTitle() + "%");
        }

        if (!ObjectUtils.isEmpty(this.getCreator())) {
            criteria = criteria.and("creator").is(this.getCreator());
        }

        if (!ObjectUtils.isEmpty(this.getType())) {
            criteria = criteria.and("type").like(this.getType() + "%");
        }

        if (StringUtils.hasLength(this.getSystem())) {
            criteria = criteria.and("system").like("%" + this.getSystem() + "%");
        }

        if (StringUtils.hasLength(this.getStatus())) {
            criteria = criteria.and("status").in(StringUtils.commaDelimitedListToStringArray(getStatus()));
        }

        return criteria;
    }
}