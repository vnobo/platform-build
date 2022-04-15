package com.bootiful.system.core.custom.column;

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
 * 自定义列类
 *
 * @author jjh
 * @date 2021/8/2
 */
@Schema(title = "用户自定义列")
@Data
@Table("sys_custom_column")
public class CustomColumn implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private Integer tenantId;
    private Long userId;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    private Integer menuId;
    /**
     * 字段名
     */
    private String propName;
    /**
     * 列名称
     */
    private String label;
    /**
     * 是否固定
     */
    private Boolean isFixed;
    /**
     * 是否换行
     */
    private Boolean isHide;
    /**
     * 是否自定义内容
     */
    private Boolean isCustom;
    /**
     * 是否排序
     */
    private Boolean isSortable;
    /**
     * 是否显示
     */
    private Boolean isShow;
    /**
     * 图标
     */
    private String icon;
    /**
     * 类型
     */
    private String type;
    /**
     * 相同自定义列母版ID
     */
    private Long category;
    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 列宽度
     */
    private String width;

    /**
     * 是否过滤
     */
    private Boolean filtration;
    /**
     * 是否可编辑
     */
    private Boolean isEdit;
    /**
     * 字典类型
     */
    private String dictionaryType;
    /**
     * 资源类型
     */
    private String path;
    /**
     * 单元格样式
     */
    private String cellStyle;
    /**
     * 数据来源
     */
    private Integer fromType;


    private JsonNode extend;


    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }

}