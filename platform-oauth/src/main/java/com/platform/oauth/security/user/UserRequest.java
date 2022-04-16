package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest implements Serializable {

  private Long id;

  @NotBlank(message = "登录用户名[username]不能为空!")
  @Pattern(regexp = "^[a-zA-Z0-9_-]{5,16}$", message = "登录用户名[username]必须为5到16位（字母，数字，下划线，减号）!")
  private String username;

  @Pattern(
      regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
      message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
  private String password;

  @NotNull(message = "是否启用[enabled]不能为空!")
  private Boolean enabled;

  @Pattern(regexp = "[\\u4e00-\\u9fa5]+", message = "用户实名[name]不能有其它符号!")
  private String name;

  private String idCard;

  @Email(message = "电子邮箱[email]不合法!")
  private String email;

  private String phone;

  private String address;

  @NotNull(message = "租户[tenantId]不能为空!")
  private Integer tenantId;

  private String tenantCode;

  private String securityTenantCode;

  @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
  private SystemType system;

  @NotNull(message = "权限组[groupId]不能为空!")
  private Integer groupId;

  private JsonNode extend;

  private UserBinding binding;

  public static UserRequest withUsername(String username) {
    UserRequest userRequest = new UserRequest();
    userRequest.setUsername(username);
    return userRequest;
  }

  public String affirmIdCard() {
    if (StringUtils.hasLength(this.idCard)) {
      if (this.idCard.length() > 18) {
        return this.idCard.substring(0, 18);
      }
    }
    return this.idCard;
  }

  public UserRequest id(Long id) {
    this.setId(id);
    return this;
  }

  public UserRequest name(String name) {
    this.setName(name);
    return this;
  }

  public UserRequest tenantId(Integer tenantId) {
    this.setTenantId(tenantId);
    return this;
  }

  public UserRequest address(String address) {
    this.setAddress(address);
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

    if (!ObjectUtils.isEmpty(this.id)) {
      criteria = criteria.and(Criteria.where("id").is(this.id));
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

    if (StringUtils.hasLength(this.name)) {
      criteria = criteria.and("name").like("%" + this.name + "%");
    }

    if (StringUtils.hasLength(this.idCard)) {
      criteria = criteria.and("idCard").like(this.idCard + "%");
    }

    if (StringUtils.hasLength(this.email)) {
      criteria = criteria.and("email").like(this.email + "%");
    }

    if (StringUtils.hasLength(this.phone)) {
      criteria = criteria.and("phone").like(this.phone + "%");
    }
    return criteria;
  }
}