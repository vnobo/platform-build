package com.platform.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * qinke-coupons com.alex.web.security.User
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/3
 */
@Schema(name = "用户")
@Data
@Table("se_users")
public class User implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    private String code;

    private String tenantCode;

    @NotBlank(message = "登录用户名[username]不能为空!", groups = {UserRequest.Register.class,
            UserRequest.ChangePassword.class, UserRequest.Modify.class})
    @Pattern(regexp = "^[a-zA-Z0-9_-]{5,16}$", message = "登录用户名[username]必须为5到16位（字母，数字，下划线，减号）!")
    private String username;

    @NotBlank(message = "用户密码[password]不能为空!", groups = {UserRequest.Register.class,
            UserRequest.ChangePassword.class})
    @Pattern(regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
    private String password;

    @NotNull(message = "是否启用[enabled]不能为空!", groups = {UserRequest.Register.class, UserRequest.Modify.class})
    private Boolean enabled;

    private JsonNode extend;

    private LocalDateTime lastLoginTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    @CreatedDate
    private LocalDateTime createdTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}