package com.bootiful.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.ModifyUserRequest
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/12
 */
@Data
@Validated
public class ModifyUserRequest implements Serializable {

    private Long id;

    @NotBlank(message = "登录用户名[username]不能为空!")
    private String username;

    @NotNull(message = "租户[tenantId]不能为空!")
    private Integer tenantId;

    @NotNull(message = "权限组[groupId]不能为空!")
    private Integer groupId;

    @NotNull(message = "是否启用[enabled]不能为空!")
    private Boolean enabled;

    @Email(message = "电子邮箱[email]不合法!")
    private String email;

    private String phone;

    private JsonNode extend;
}