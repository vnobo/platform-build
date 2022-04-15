package com.bootiful.system.core.custom.menu;

import com.bootiful.commons.utils.SystemType;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * com.bootiful.system.core.menu.CustomMenu
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/5
 */
@Schema(title = "用户自定义常用菜单")
@Data
@Table("sys_custom_menus")
public class CustomMenu implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private Long userId;
    private Long menuId;
    private String name;
    private String path;
    private Integer sort;
    private Long pid;
    private Integer type;
    private JsonNode extend;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    @CreatedDate
    private LocalDateTime createdTime;
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}