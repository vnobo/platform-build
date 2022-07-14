package com.platform.oauth.security.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * com.alex.oauth.security.Groups
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Schema(name = "角色")
@Data
@Table("se_groups")
public class Group implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;

    @NotBlank(message = "角色[code]不能为空!")
    private String code;

    @NotBlank(message = "租户[tenantCode]不能为空!")
    private String tenantCode;

    @NotBlank(message = "角色[name]不能为空!")
    private String name;

    private String description;

    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}