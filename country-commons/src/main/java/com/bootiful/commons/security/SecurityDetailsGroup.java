package com.bootiful.commons.security;

import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.SecurityDetailsGroup
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/16
 */
@Data
public class SecurityDetailsGroup implements Serializable {
    Integer id;
    Integer tenantId;
    String tenantName;
    String name;
}