package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

/**
 * qinke-coupons com.alex.web.security.User
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/3
 */
@Data
@Table("se_users")
public class User implements Serializable, Persistable<Long> {

  @Id private Long id;
  private Integer tenantId;
  private String tenantCode;
  private String username;
  private String password;
  private Boolean enabled;
  private JsonNode extend;

  private LocalDateTime lastLoginTime;

  @LastModifiedDate private LocalDateTime updatedTime;
  @CreatedDate private LocalDateTime createdTime;

  @Override
  public boolean isNew() {
    return ObjectUtils.isEmpty(this.id);
  }
}