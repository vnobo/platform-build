package com.bootiful.system.core.menu;

import com.bootiful.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Schema(title = "资源菜单查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class DictSearchRequest extends MenuDictRequest {

    @Schema(title = "是否查询父级菜单")
    private Boolean isParent;

    @Schema(title = "不包含这个ID")
    private Integer noId;

    private Set<String> roles;

    @Override
    public DictSearchRequest system(SystemType system) {
        this.setSystem(system);
        return this;
    }

    public Criteria toCriteria() {
        Criteria authorityDict = Criteria.where("system").is(getSystem());

        if (!ObjectUtils.isEmpty(getPid())) {
            authorityDict = authorityDict.and("pid").is(getPid());
        }

        if (!ObjectUtils.isEmpty(roles)) {
            authorityDict = authorityDict.and("authority").in(roles);
        }

        if (!ObjectUtils.isEmpty(getName())) {
            authorityDict = authorityDict.and("name").like("%" + getName() + "%");
        }

        if (!ObjectUtils.isEmpty(getAuthority())) {
            authorityDict = authorityDict.and("authority").like("%" + getName() + "%");
        }

        if (!ObjectUtils.isEmpty(getId())) {
            authorityDict = authorityDict.and("id").is(getId());
        }

        if (!ObjectUtils.isEmpty(noId)) {
            authorityDict = authorityDict.and("pid").not(noId);
        }

        if (!ObjectUtils.isEmpty(isParent)) {
            if (isParent) {
                authorityDict = authorityDict.and("pid").is(0);
            } else {
                authorityDict = authorityDict.and("pid").greaterThan(0);
            }

        }

        return authorityDict;
    }

}