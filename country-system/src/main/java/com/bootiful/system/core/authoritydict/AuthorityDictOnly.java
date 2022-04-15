package com.bootiful.system.core.authoritydict;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "资源菜单读取")
@Data
public class AuthorityDictOnly extends AuthorityDict implements Serializable {

    private Set<MethodPermissions> permissions;

    public static AuthorityDictOnly withAuthorityDict(AuthorityDict authorityDict) {
        AuthorityDictOnly dictMenu = new AuthorityDictOnly();
        BeanUtils.copyProperties(authorityDict, dictMenu);
        if (!ObjectUtils.isEmpty(dictMenu.getExtend())) {
            JsonNode permissionsNode = dictMenu.getExtend().withArray("permissions");
            ObjectMapper objectMapper = new ObjectMapper();
            Set<MethodPermissions> permissions = ObjectUtils.isEmpty(permissionsNode) ? Set.of()
                    : objectMapper.convertValue(permissionsNode, new TypeReference<>() {
            });
            dictMenu.setPermissions(permissions);
        } else {
            dictMenu.setPermissions(Set.of());
        }
        return dictMenu;
    }
}