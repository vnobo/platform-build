package com.platform.system.core.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "资源菜单读取")
@Data
public class MenuDictOnly extends MenuDict implements Serializable {

  private Set<MethodPermissions> permissions;

  public static MenuDictOnly withAuthorityDict(MenuDict menuDict) {
    MenuDictOnly dictMenu = new MenuDictOnly();
    BeanUtils.copyProperties(menuDict, dictMenu);
    if (!ObjectUtils.isEmpty(dictMenu.getExtend())) {
      JsonNode permissionsNode = dictMenu.getExtend().withArray("permissions");
      ObjectMapper objectMapper = new ObjectMapper();
      Set<MethodPermissions> permissions =
          ObjectUtils.isEmpty(permissionsNode)
              ? Set.of()
              : objectMapper.convertValue(permissionsNode, new TypeReference<>() {});
      dictMenu.setPermissions(permissions);
    } else {
      dictMenu.setPermissions(Set.of());
    }
    return dictMenu;
  }
}