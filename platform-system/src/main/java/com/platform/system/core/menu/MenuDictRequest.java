package com.platform.system.core.menu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Schema(title = "资源菜单请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuDictRequest extends MenuDict {

    @NotBlank(message = "权限名[authority]不能为空!")
    private String authority;

    @NotBlank(message = "菜单名[name]不能为空!")
    private String name;

    @NotNull(message = "系统类型[system]不能为空!")
    @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
    private SystemType system;

    @Valid
    private Set<MethodPermissions> permissions;

    @Min(value = 0, message = "权限父级ID[pid]最小为0!")
    @Max(Integer.MAX_VALUE)
    private Integer pid;

    public MenuDictRequest system(SystemType system) {
        this.setSystem(system);
        return this;
    }

    public MenuDict toDict() {
        MenuDict menuDict = new MenuDict();
        BeanUtils.copyProperties(this, menuDict);
        if (!ObjectUtils.isEmpty(this.getPermissions())) {
            ObjectNode objectNode = ObjectUtils.isEmpty(this.getExtend()) ?
                    new ObjectMapper().createObjectNode() : this.getExtend().deepCopy();
            JsonNode permissions = new ObjectMapper().convertValue(this.getPermissions(), JsonNode.class);
            objectNode.set("permissions", permissions);
            menuDict.setExtend(objectNode);
        }
        return menuDict;
    }
}