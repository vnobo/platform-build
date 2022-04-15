package com.platform.oauth.security.group.authority;

import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * com.bootiful.oauth.security.group.GroupSearchRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityGroupRequest extends AuthorityGroup implements Serializable {
    @Schema(title = "角色类型")
    @NotNull(message = "角色类型:[type]不能为空!")
    private Integer type;

    @NotNull(message = "授权系统[system]名称不能为空!")
    private SystemType system;

    @NotNull(message = "授权的权限[rules]不能为空!")
    private List<String> rules;

    private Long userId;

    public static AuthorityGroupRequest withAuthorities(List<String> authorities) {
        AuthorityGroupRequest authorizingRequest = new AuthorityGroupRequest();
        authorizingRequest.setRules(authorities);
        return authorizingRequest;
    }

    public static AuthorityGroup of(Integer groupId, String authority) {
        AuthorityGroup authorityGroup = new AuthorityGroup();
        authorityGroup.setGroupId(groupId);
        authorityGroup.setAuthority(authority);
        return authorityGroup;
    }

    public boolean validAllEmpty() {
        int valid = 0;
        if (!ObjectUtils.isEmpty(getGroupId())) {
            valid++;
        }

        if (!ObjectUtils.isEmpty(getType())) {
            valid++;
        }

        if (!ObjectUtils.isEmpty(getUserId())) {
            valid++;
        }

        return valid == 0;
    }

    public static AuthorityGroupRequest withGroupId(Integer groupId) {
        AuthorityGroupRequest groupRequest = new AuthorityGroupRequest();
        groupRequest.setGroupId(groupId);
        return groupRequest;
    }

    public static AuthorityGroupRequest withUserId(Long userId) {
        AuthorityGroupRequest groupRequest = new AuthorityGroupRequest();
        groupRequest.setUserId(userId);
        return groupRequest;
    }

    public String toWhereSql() {
        StringBuilder whereSql = new StringBuilder(" where id > 0 ");
        if (!ObjectUtils.isEmpty(getGroupId())) {
            whereSql.append(" and group_id =").append(getGroupId());
        }
        if (!ObjectUtils.isEmpty(type)) {
            Assert.notNull(system, "系统类型[system]不能为空!");
            whereSql.append(" and group_id in (select id from se_groups where tenant_id=0 and " +
                    "type=").append(type).append(" and system='").append(system.name()).append("')");
        }

        if (!ObjectUtils.isEmpty(userId)) {
            Assert.notNull(system, "系统类型[system]不能为空!");
            whereSql.append(" and group_id in (select se_group_members.group_id " +
                            "from se_group_members,se_users,se_groups  " +
                            "where se_group_members.user_id=se_users.id " +
                            "and se_group_members.group_id=se_groups.id " +
                            "and se_users.tenant_id=se_groups.tenant_id " +
                            "and se_group_members.user_id = ").append(userId)
                    .append(" and se_groups.system='").append(system.name()).append("')");
        }
        return whereSql.toString();
    }
}