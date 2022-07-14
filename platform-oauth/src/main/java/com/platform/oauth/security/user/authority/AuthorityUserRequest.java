package com.platform.oauth.security.user.authority;

import com.platform.commons.utils.CriteriaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * com.alex.oauth.security.Authorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityUserRequest extends AuthorityUser {

    @NotNull(message = "权限[authorities]不能为空!", groups = Authorities.class)
    private List<String> authorities;

    public static AuthorityUserRequest withUserCode(String userCode) {
        AuthorityUserRequest groupRequest = new AuthorityUserRequest();
        groupRequest.setUserCode(userCode);
        return groupRequest;
    }

    public static AuthorityUser of(String userCode, String authority) {
        AuthorityUser authorityUser = withUserCode(userCode);
        authorityUser.setAuthority(authority);
        return authorityUser;
    }

    public Criteria toCriteria() {
        return CriteriaUtils.build(this);
    }

    public interface Authorities {
    }
}