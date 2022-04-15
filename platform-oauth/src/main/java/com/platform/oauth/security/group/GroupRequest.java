package com.platform.oauth.security.group;

import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Schema(title = "角色组请求参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupRequest extends Group implements Serializable {
    @NotNull(message = "系统类型[system]不能为空!")
    private SystemType system;
    @NotBlank(message = "角色名[name]不能为空!")
    private String name;

    @NotBlank(message = "角色描述[description]不能为空!")
    private String description;
    @Schema(title = "角色类型根据不同的系统指定类型!")
    @NotNull(message = "角色类型[type]不能为空!")
    private Integer type;
    private String securityTenantCode;

    public static GroupRequest of(Integer tenantId, String tenantCode, String name,
                                  String description, Integer type, SystemType system) {
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setTenantId(tenantId);
        groupRequest.setName(name);
        groupRequest.setDescription(description);
        groupRequest.setTenantCode(tenantCode);
        groupRequest.setType(type);
        groupRequest.setSystem(system);
        return groupRequest;
    }

    public GroupRequest id(Integer id) {
        this.setId(id);
        return this;
    }

    public GroupRequest securityTenantCode(String tenantCode) {
        this.setSecurityTenantCode(tenantCode);
        return this;
    }

    public Group toGroup() {
        Group group = new Group();
        BeanUtils.copyProperties(this, group);
        return group;
    }

    public Criteria toCriteria() {

        Criteria criteria = Criteria.where("id").isNotNull();

        if (StringUtils.hasLength(this.securityTenantCode)) {
            criteria = criteria.and("tenantCode").like(this.securityTenantCode + "%");
        }

        if (StringUtils.hasLength(this.getTenantCode())) {
            criteria = criteria.and("tenantCode").like(this.getTenantCode() + "%");
        }

        if (StringUtils.hasLength(this.name)) {
            criteria = criteria.and("name").like("%" + this.name + "%");
        }

        if (!ObjectUtils.isEmpty(this.type)) {
            criteria = criteria.and("type").is(this.type);
        }

        if (!ObjectUtils.isEmpty(this.getTenantId())) {
            criteria = criteria.and("tenantId").is(this.getTenantId());
        }

        if (!ObjectUtils.isEmpty(this.system)) {
            criteria = criteria.and("system").is(this.system);
        }

        return criteria;
    }


}