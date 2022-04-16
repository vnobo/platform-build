package com.platform.oauth.security.group.member;

import com.platform.oauth.security.group.GroupOnly;
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
  private String uname;
  private String username;
  private String email;
  private String phone;
  private String idCard;
  private Boolean enabled;

  public static MemberGroupOnly withMemberGroup(MemberGroup memberGroup) {
    MemberGroupOnly memberGroupOnly = new MemberGroupOnly();
    BeanUtils.copyProperties(memberGroup, memberGroupOnly);
    return memberGroupOnly;
  }

  public MemberGroupOnly userOnly(UserOnly userOnly) {
    this.setUname(userOnly.getName());
    this.setPhone(userOnly.getPhone());
    this.setUsername(userOnly.getUsername());
    this.setEmail(userOnly.getEmail());
    this.setEnabled(userOnly.getEnabled());
    return this;
  }

  public MemberGroupOnly groupOnly(GroupOnly groupOnly) {
    this.setGroupName(groupOnly.getName());
    return this;
  }
}