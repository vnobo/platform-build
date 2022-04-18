package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends User implements Serializable {

  @NotBlank(message = "登录用户名[username]不能为空!")
  @Pattern(regexp = "^[a-zA-Z0-9_-]{5,16}$", message = "登录用户名[username]必须为5到16位（字母，数字，下划线，减号）!")
  private String username;

  @NotBlank(message = "用户密码[password]不能为空!")
  @Pattern(
      regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
      message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
  private String password;

  @NotNull(message = "是否启用[enabled]不能为空!")
  private Boolean enabled;
  @NotNull(message = "租户[tenantId]不能为空!")
  private Integer tenantId;

  @NotNull(message = "租户编码[tenantCode]不能为空!")
  private String tenantCode;

  @Schema(title = "系统类型[system]不能为空!")
  private SystemType system;

  private Integer groupId;

  private UserBinding binding;
  private String securityTenantCode;

  public static UserRequest withUsername(String username) {
    UserRequest userRequest = new UserRequest();
    userRequest.setUsername(username);
    return userRequest;
  }

  public UserRequest id(Long id) {
    this.setId(id);
    return this;
  }

  public UserRequest tenantId(Integer tenantId) {
    this.setTenantId(tenantId);
    return this;
  }

  public UserRequest tenantCode(String tenantCode) {
    this.setTenantCode(tenantCode);
    return this;
  }

  public UserRequest securityTenantCode(String tenantCode) {
    this.setSecurityTenantCode(tenantCode);
    return this;
  }

  public User toUser() {
    User user = new User();
    BeanUtils.copyProperties(this, user);
    if (!ObjectUtils.isEmpty(this.getBinding())) {
      ObjectNode objectNode =
          ObjectUtils.isEmpty(this.getExtend()) || this.getExtend().isNull()
              ? new ObjectMapper().createObjectNode()
              : this.getExtend().deepCopy();
      objectNode.putPOJO("binding", this.getBinding());
      user.setExtend(objectNode);
    }
    return user;
  }
  public Criteria toCriteria() {

    Criteria criteria = Criteria.empty();

    if (!ObjectUtils.isEmpty(this.getId())) {
      criteria = criteria.and(Criteria.where("id").is(this.getId()));
    }

    if (!ObjectUtils.isEmpty(this.tenantId)) {
      criteria = criteria.and("tenantId").is(this.tenantId);
    }

    if (StringUtils.hasLength(this.tenantCode)) {
      criteria = criteria.and("tenantCode").like(this.tenantCode + "%");
    }

    if (StringUtils.hasLength(this.securityTenantCode) && !"0".equals(this.securityTenantCode)) {
      criteria = criteria.and("tenantCode").like(this.securityTenantCode + "%");
    }

    if (StringUtils.hasLength(this.username)) {
      criteria = criteria.and("username").like(this.username).ignoreCase(true);
    }

    return criteria;
  }
}