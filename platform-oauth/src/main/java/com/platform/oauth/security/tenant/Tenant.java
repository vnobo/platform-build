package com.platform.oauth.security.tenant;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * com.bootiful.oauth.security.tenant.Tenant
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@Table("se_tenants")
public class Tenant implements Serializable, Persistable<Integer> {
    @Id
    private Integer id;
    private String code;
    private String name;
    private String address;
    private String description;
    private Integer pid;
    private JsonNode extend;

    @CreatedDate
    private LocalDateTime createdTime;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }

    public Integer getTier() {
        JsonNode jsonNode = this.getExtend().get("addressCode");
        return ObjectUtils.isEmpty(jsonNode) ? 0 : jsonNode.size();
    }
}