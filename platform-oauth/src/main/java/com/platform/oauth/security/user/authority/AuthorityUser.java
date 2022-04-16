package com.platform.oauth.security.user.authority;

import com.platform.commons.utils.SystemType;
import com.platform.oauth.security.SimpleAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * com.alex.oauth.security.Authorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Schema(title = "用户授权信息")
@Data
@Table("se_authorities")
public class AuthorityUser implements SimpleAuthority, Persistable<Integer> {

  @Id private Integer id;
  private Long userId;
  private String authority;

  @Schema(title = "系统类型")
  private SystemType system;

  @CreatedDate private LocalDateTime createdTime;

  @LastModifiedDate private LocalDateTime updatedTime;

  public static AuthorityUser of(SystemType system, Long userId, String authority) {
    AuthorityUser authorityUser = new AuthorityUser();
    authorityUser.setUserId(userId);
    authorityUser.setAuthority(authority);
    authorityUser.setSystem(system);
    return authorityUser;
  }

  @Override
  public boolean isNew() {
    return ObjectUtils.isEmpty(this.id);
  }
}