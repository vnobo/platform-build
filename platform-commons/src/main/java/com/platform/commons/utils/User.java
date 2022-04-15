package com.platform.commons.utils;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * com.bootiful.commons.utils.User
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long id;
    private Integer tenantId;
    private String tenantCode;
    private String username;
    private Boolean enabled;
    private String email;
    private String phone;
    private String name;
    private String idCard;
    private JsonNode extend;
    private LocalDateTime lastLoginTime;
    private LocalDateTime updatedTime;
    private LocalDateTime createdTime;

    public static User withId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public MultiValueMap<String, String> toQueryParams() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(10);
        Map<String, Object> objectMap = BeanUtil.beanToMap(this, false, true);
        objectMap.forEach((k, v) -> multiValueMap.set(k, String.valueOf(v)));
        return multiValueMap;
    }
}