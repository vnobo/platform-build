package com.platform.system.core.notice;

import com.platform.commons.annotation.UserAuditor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;


/**
 * @author jjh
 * @package com.bootiful.rural.core.announcement
 * @classname AnnouncementSearch
 * @date 2021/7/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Validated
@Schema(title = "通知公告查询")
public class NoticeSearch extends NoticeRequest {

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Schema(title = "创建人Id")
    private UserAuditor creator;

    @Schema(title = "修改人")
    private UserAuditor updater;

    private String securityCode;

    public NoticeSearch securityCode(String securityCode) {
        this.setSecurityCode(securityCode);
        return this;
    }

    public Criteria toCriteria() {
        Criteria toCriteria = Criteria.empty();
        if (!ObjectUtils.isEmpty(this.getId())) {
            toCriteria = toCriteria.and("id").is(this.getId());
        }

        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            toCriteria = toCriteria.and("tenantId").is(this.getTenantId());
        }

        if (StringUtils.hasLength(this.getTenantCode())) {
            toCriteria = toCriteria.and("tenantCode").like(this.getTenantCode() + "%");
        }

        if (StringUtils.hasLength(this.getSecurityCode())) {
            toCriteria = toCriteria.and("tenantCode").like(this.getSecurityCode() + "%");
        }

        if (!ObjectUtils.isEmpty(this.getType())) {
            toCriteria = toCriteria.and("type").is(this.getType());
        }
        if (!ObjectUtils.isEmpty(this.getSystem())) {
            toCriteria = toCriteria.and("system").like("%" + this.getSystem() + "%");
        }
        if (StringUtils.hasLength(this.getTitle())) {
            toCriteria = toCriteria.and("title").like("%" + this.getTitle() + "%");
        }
        return toCriteria;
    }
}