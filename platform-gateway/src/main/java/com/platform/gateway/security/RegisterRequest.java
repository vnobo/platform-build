package com.platform.gateway.security;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * com.bootiful.gateway.security.RegisterRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/3/28
 */
@Data
public class RegisterRequest implements Serializable {

    @NotBlank(message = "注册登录账户[username]不能为空!")
    private String username;

    @NotBlank(message = "注册登录密码[password]不能为空!")
    private String password;

    @NotBlank(message = "是否启用[enabled]不能为空!")
    private Boolean enabled;

    private JsonNode extend;

}