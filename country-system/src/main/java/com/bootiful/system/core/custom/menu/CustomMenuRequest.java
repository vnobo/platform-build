package com.bootiful.system.core.custom.menu;

import com.bootiful.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * com.bootiful.system.core.menu.CustomMenuRequest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/5
 */
@Schema(title = "用户自定义常用菜单请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomMenuRequest extends CustomMenu {

    @Schema(title = "用户ID")
    @NotNull(message = "菜单用户ID[userId]不能为空!")
    private Long userId;

    @Schema(title = "菜单ID")
    @NotNull(message = "菜单[menuId]不能为空!")
    private Long menuId;

    @NotBlank(message = "菜单资源[name]不能为空!")
    @Schema(title = "菜单名字")
    private String name;

    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    @NotNull(message = "系统[system]不能为空!")
    private SystemType system;

    public CustomMenuRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public CustomMenu toCustomMenu() {
        CustomMenu customMenu = new CustomMenu();
        BeanUtils.copyProperties(this, customMenu);
        return customMenu;
    }

    public Criteria toCriteria() {

        Criteria criteria = Criteria.where("userId").is(this.userId);

        if (!ObjectUtils.isEmpty(this.getId()) && this.getId() > -1) {
            criteria = criteria.and("id").is(this.getId());
        }

        if (!ObjectUtils.isEmpty(this.getSystem())) {
            criteria = criteria.and("system").is(this.getSystem());
        }

        if (!ObjectUtils.isEmpty(this.getPid()) && this.getPid() > -1) {
            criteria = criteria.and("pid").is(this.getPid());
        }

        if (!ObjectUtils.isEmpty(this.menuId) && this.menuId > -1) {
            criteria = criteria.and("menuId").is(this.menuId);
        }

        if (StringUtils.hasLength(this.name)) {
            criteria = criteria.and("name").like("%" + this.name + "%");
        }

        return criteria;
    }
}