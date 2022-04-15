package com.bootiful.gateway.security;

import com.bootiful.commons.utils.SystemType;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.gateway.security.RegisterRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/3/28
 */
@Data
public class RegisterRequest implements Serializable {

    private String username;

    private String password;

    private Boolean enabled;

    private String name;

    private String idCard;

    private String email;

    private String phone;

    private String address;

    private Integer tenantId;

    private String tenantCode;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    private Integer groupId;

    private JsonNode extend;

}