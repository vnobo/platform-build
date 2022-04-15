package com.bootiful.gateway.security;

import com.bootiful.commons.utils.SystemType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.oauth.core.weixin.RegRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends RegisterRequest implements Serializable {

    @NotBlank(message = "手机号[phone]不能为空!")
    private String phone;

    @NotBlank(message = "验证码[code]不能为空!")
    private String code;

    @NotNull(message = "需要登录的系统[system]不能为空!")
    private SystemType system;

    public LoginRequest system(SystemType system) {
        this.setSystem(system);
        return this;
    }

    public LoginRequest toRegister() {
        this.setUsername(getPhone());
        this.setEnabled(true);
        return this;
    }
}