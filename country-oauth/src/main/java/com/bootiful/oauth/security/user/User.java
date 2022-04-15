package com.bootiful.oauth.security.user;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * qinke-coupons com.alex.web.security.User
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2019/7/3
 */
@Data
@Table("se_users")
public class User implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private Integer tenantId;
    private String tenantCode;
    private String username;
    private String password;
    private Boolean enabled;
    private String email;
    private String phone;
    private String name;
    private String idCard;
    private JsonNode extend;

    private LocalDateTime lastLoginTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;
    @CreatedDate
    private LocalDateTime createdTime;

    public String affirmIdCard() {
        if (StringUtils.hasLength(this.idCard) && this.idCard.length() > 18) {
            return this.idCard.substring(0, 18);
        }
        return this.idCard;
    }

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}