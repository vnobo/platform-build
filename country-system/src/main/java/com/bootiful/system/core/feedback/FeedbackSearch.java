package com.bootiful.system.core.feedback;

import com.bootiful.commons.utils.SqlUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * @author jjh
 * @classname FeedbackSearch
 * @date 2021/9/30 create
 */
@Data
@Schema(title = "意见反馈搜索")
public class FeedbackSearch {
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

    public String whereSql(Pageable pageable) {
        StringBuilder sql = new StringBuilder("select rhf.*,su.name,su2.name reply_name")
                .append(" from sys_feed_back rhf left join se_users su on rhf.user_id=su.id")
                .append(" left join se_users su2 on rhf.reply_id=su2.id where rhf.id>-1 ");
        setSql(sql);
        return sql.append(SqlUtils.applyPage(pageable)).toString();
    }

    public StringBuilder countSql() {
        StringBuilder sql = new StringBuilder("select count(*) from sys_feed_back rhf left join se_users su")
                .append(" on rhf.user_id=su.id left join se_users su2 on rhf.reply_id=su2.id where rhf.id>-1");
        setSql(sql);
        return sql;
    }

    private void setSql(StringBuilder sql) {
        if (!ObjectUtils.isEmpty(this.id)) {
            sql.append(" and rhf.id =").append(this.id);
        }
        if (!ObjectUtils.isEmpty(this.type)) {
            sql.append("  and rhf.type = '").append(this.type).append("'");
        }
        if (!ObjectUtils.isEmpty(this.status)) {
            sql.append("  and rhf.status =").append(this.status);
        }
        if (!ObjectUtils.isEmpty(this.userId)) {
            sql.append("  and rhf.user_id =").append(this.userId);
        }
        if (!ObjectUtils.isEmpty(this.tenantId)) {
            sql.append("  and rhf.tenant_id =").append(this.tenantId);
        }
        if (!ObjectUtils.isEmpty(this.tenantCode)) {
            sql.append("  and rhf.tenant_code like '").append(this.tenantCode).append("%'");
        }
        if (!ObjectUtils.isEmpty(this.tenantName)) {
            sql.append("  and rhf.tenant_code like '%").append(this.tenantName).append("%'");
        }
    }

}