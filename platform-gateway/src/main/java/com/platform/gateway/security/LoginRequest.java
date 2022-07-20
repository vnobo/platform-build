package com.platform.gateway.security;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
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

    @NotBlank(message = "验证码[code]不能为空!")
    private String code;

    @NotBlank(message = "微信用户[openid]不能为空!")
    private String openid;

    private String appId;

    public LoginRequest toRegister() {
        this.setEnabled(true);
        return this;
    }
}