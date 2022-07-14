package com.platform.oauth.security.user.authority;

import com.platform.oauth.security.SimpleAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;

/**
 * com.alex.oauth.security.Authorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Schema(name = "用户权限表")
@Data
@Table("se_authorities")
public class AuthorityUser implements SimpleAuthority, Persistable<Integer> {

    @Id
    private Integer id;

    @NotBlank(message = "用户[userCode]不能为空!")
    private String userCode;

    @NotBlank(message = "授权[authority]不能为空!")
    private String authority;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}