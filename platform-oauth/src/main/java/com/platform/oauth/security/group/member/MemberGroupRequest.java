package com.platform.oauth.security.group.member;

import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Schema(title = "角色用户请求参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberGroupRequest extends MemberGroupOnly implements Serializable {

  private Integer tenantId;
  private String tenantCode;
  private String securityTenantCode;

  @Schema(title = "角色名")
  private String name;

  @Schema(title = "角色类型")
  private Integer type;

  @Schema(title = "系统类型")
  private SystemType system;

  public static MemberGroup of(Integer groupId, Long userId) {
    MemberGroup memberGroup = new MemberGroup();
    memberGroup.setGroupId(groupId);
    memberGroup.setUserId(userId);
    return memberGroup;
  }

  public MemberGroupRequest id(Integer id) {
    this.setId(id);
    return this;
  }

  public String toMemberWhere() {

    StringBuilder stringBuilder = new StringBuilder(" where se_group_members.id > 0");

    if (StringUtils.hasLength(this.securityTenantCode)) {
      stringBuilder
          .append(" and se_groups.tenant_code ilike '")
          .append(this.securityTenantCode)
          .append("%'");
    }

    if (StringUtils.hasLength(this.tenantCode)) {
      stringBuilder
          .append(" and se_groups.tenant_code ilike '")
          .append(this.tenantCode)
          .append("%'");
    }

    if (StringUtils.hasLength(this.getName())) {
      stringBuilder.append(" and se_groups.name ilike '%").append(this.getName()).append("%'");
    }

    if (!ObjectUtils.isEmpty(this.getType())) {
      stringBuilder.append(" and se_groups.type = ").append(this.type);
    }

    if (!ObjectUtils.isEmpty(this.tenantId)) {
      stringBuilder.append(" and se_groups.tenant_id = ").append(this.tenantId);
    }

    if (!ObjectUtils.isEmpty(this.getId())) {
      stringBuilder.append(" and se_groups.id = ").append(this.getId());
    }

    if (!ObjectUtils.isEmpty(this.system)) {
      stringBuilder.append(" and se_groups.system = '").append(this.system.name()).append("'");
    }

    return stringBuilder.toString();
  }
}