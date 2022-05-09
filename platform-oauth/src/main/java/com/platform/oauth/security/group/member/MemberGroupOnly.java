package com.platform.oauth.security.group.member;

import com.platform.oauth.security.group.Group;
import com.platform.oauth.security.user.UserOnly;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * com.bootiful.oauth.security.group.GroupMemberOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberGroupOnly extends MemberGroup implements Serializable {

  private String groupName;
  private String username;

  public static MemberGroupOnly withMemberGroup(MemberGroup memberGroup) {
    MemberGroupOnly memberGroupOnly = new MemberGroupOnly();
    BeanUtils.copyProperties(memberGroup, memberGroupOnly);
    return memberGroupOnly;
  }

  public MemberGroupOnly userOnly(UserOnly userOnly) {
    this.setUsername(userOnly.getUsername());
    return this;
  }

  public MemberGroupOnly groupOnly(Group group) {
    this.setGroupName(group.getName());
    return this;
  }
}