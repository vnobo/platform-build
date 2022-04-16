package com.platform.oauth.security.group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * com.bootiful.oauth.security.group.GroupOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupOnly extends Group implements Serializable {

  private List<String> authorities;

  public static GroupOnly withGroup(Group group) {
    GroupOnly groupOnly = new GroupOnly();
    BeanUtils.copyProperties(group, groupOnly);
    return groupOnly;
  }

  public GroupOnly authorities(List<String> authorities) {
    this.setAuthorities(authorities);
    return this;
  }
}