package com.platform.system.core.custom.column;

import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jjh
 * @date 2021/8/2
 */
@Schema(title = "用户自定义列请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomColumnRequest extends CustomColumn {

    @NotNull(message = "关联租户ID[tenantId]不能为空!默认模板为0")
    private Integer tenantId;

    @NotNull(message = "关联用户[userId]不能为空!默认模板为0")
    private Long userId;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    @NotNull(message = "资源ID[menuId]不能为空!")
    private Integer menuId;

    @NotBlank(message = "列字段名称[propName]不能为空!")
    private String propName;

    @NotBlank(message = "列显示名称[label]不能为空!")
    private String label;

    @NotBlank(message = "路径[path]不能为空!")
    private String path;

    private Boolean isDefault;

    public CustomColumnRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public CustomColumnRequest category(Long category) {
        this.setCategory(category);
        return this;
    }

    public CustomColumnRequest tenantId(Integer tenantId) {
        this.setTenantId(tenantId);
        return this;
    }
    public CustomColumnRequest userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public CustomColumnRequest system(SystemType system) {
        this.setSystem(system);
        return this;
    }

    public CustomColumn toCustomColumn() {
        CustomColumn customColumn = new CustomColumn();
        BeanUtils.copyProperties(this, customColumn);
        return customColumn;
    }

    public Criteria toCriteria() {
        Criteria criteria = Criteria.where("system").is(this.getSystem());

        if (!ObjectUtils.isEmpty(this.getId())) {
            criteria = criteria.and("id").is(this.getId());
        }
        if (!ObjectUtils.isEmpty(this.getMenuId())) {
            criteria = criteria.and("menuId").is(this.getMenuId());
        }
        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            criteria = criteria.and("tenantId").is(this.getTenantId());
        }
        if (!ObjectUtils.isEmpty(this.getUserId())) {
            criteria = criteria.and("userId").is(this.getUserId());
        }
        if (!ObjectUtils.isEmpty(this.getPath())) {
            criteria = criteria.and("path").is(this.getPath());
        }
        if (!ObjectUtils.isEmpty(this.getType())) {
            criteria = criteria.and("type").is(this.getType());
        }

        if (!ObjectUtils.isEmpty(this.getMenuId())) {
            criteria = criteria.and("menuId").is(this.getMenuId());
        }

        if (!ObjectUtils.isEmpty(this.getCategory())) {
            criteria = criteria.and("category").is(this.getCategory());
        }

        if (!ObjectUtils.isEmpty(isDefault)) {
            if (isDefault) {
                criteria = criteria.and("tenantId").is(0);
                criteria = criteria.and("userId").is(0);
            } else {
                criteria = criteria.and("tenantId").not(0);
                criteria = criteria.and("userId").not(0);
            }

        }

        return criteria;
    }
}