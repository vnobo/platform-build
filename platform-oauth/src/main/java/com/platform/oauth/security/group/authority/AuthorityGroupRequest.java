package com.platform.oauth.security.group.authority;

import com.platform.commons.utils.CriteriaUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * com.bootiful.oauth.security.group.GroupSearchRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/7
 */
@Schema(name = "角色权限请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityGroupRequest extends AuthorityGroup implements Serializable {

    @NotNull(message = "权限[authorities]不能为空!", groups = Authority.class)
    private Set<String> authorities;

    public static AuthorityGroup of(String groupCode, String authority) {
        AuthorityGroup authorityGroup = new AuthorityGroup();
        authorityGroup.setGroupCode(groupCode);
        authorityGroup.setAuthority(authority);
        return authorityGroup;
    }

    public boolean validAllEmpty() {
        int valid = 0;
        if (!ObjectUtils.isEmpty(getGroupCode())) {
            valid++;
        }
        return valid == 0;
    }

    public Criteria toCriteria() {
        return CriteriaUtils.build(this);
    }

    public interface Authority {
    }
}