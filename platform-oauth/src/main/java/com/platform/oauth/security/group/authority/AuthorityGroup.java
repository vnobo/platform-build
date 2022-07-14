package com.platform.oauth.security.group.authority;

import com.platform.oauth.security.SimpleAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;

/**
 * com.alex.oauth.security.GroupAuthorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Schema(name = "角色权限")
@Data
@Table("se_group_authorities")
public class AuthorityGroup implements Persistable<Integer>, SimpleAuthority {

    @Id
    private Integer id;

    @NotBlank(message = "角色[groupCode]不能为空!")
    private String groupCode;

    @NotBlank(message = "权限[authority]不能为空!")
    private String authority;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}