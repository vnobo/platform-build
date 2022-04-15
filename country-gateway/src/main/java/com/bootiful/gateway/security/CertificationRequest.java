package com.bootiful.gateway.security;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * com.bootiful.oauth.core.weixin.Certification
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/18
 */
@Data
public class CertificationRequest implements Serializable {

    @NotBlank(message = "实名认证[name]不能为空!")
    @Pattern(regexp = "[\\u4e00-\\u9fa5]+", message = "用户实名[name]不能有其它符号!")
    private String name;

    @NotBlank(message = "实名认证[tenantCode]不能为空!")
    private String tenantCode;

    @NotBlank(message = "实名认证[idCard]不能为空!")
    private String idCard;

    private JsonNode extend;

    public String affirmIdCard() {
        if (StringUtils.hasLength(this.idCard)) {
            if (this.idCard.length() > 18) {
                return this.idCard.substring(0, 18);
            }
        }
        return this.idCard;
    }
}