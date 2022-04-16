package com.platform.oauth.security.user.authority;

import com.platform.commons.utils.SystemType;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

/**
 * com.alex.oauth.security.Authorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityUserRequest extends AuthorityUser {

  @NotNull(message = "授权系统[system]名称不能为空!")
  private SystemType system;

  @NotNull(message = "授权的权限[rules]不能为空!")
  private List<String> rules;

  public static AuthorityUserRequest withAuthorities(List<String> authorities) {
    AuthorityUserRequest authorizingRequest = new AuthorityUserRequest();
    authorizingRequest.setRules(authorities);
    return authorizingRequest;
  }

  public static AuthorityUserRequest withUserId(Long userId) {
    AuthorityUserRequest groupRequest = new AuthorityUserRequest();
    groupRequest.setUserId(userId);
    return groupRequest;
  }

  public static AuthorityUserRequest of(SystemType system, Long userId, String authority) {
    AuthorityUserRequest authorityUser = new AuthorityUserRequest();
    authorityUser.setUserId(userId);
    authorityUser.setAuthority(authority);
    authorityUser.setSystem(system);
    return authorityUser;
  }

  public AuthorityUserRequest system(SystemType system) {
    this.setSystem(system);
    return this;
  }

  public Criteria toCriteria() {

    Criteria criteria = Criteria.empty();

    if (!ObjectUtils.isEmpty(this.getAuthority())) {
      criteria = criteria.and("userId").is(this.getUserId());
    }

    if (!ObjectUtils.isEmpty(this.getSystem())) {
      criteria = criteria.and("system").is(this.getSystem());
    }

    return criteria;
  }
}