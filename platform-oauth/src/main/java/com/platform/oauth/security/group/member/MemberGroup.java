package com.platform.oauth.security.group.member;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * com.bootiful.oauth.security.group.GroupMember
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
@Data
@Table("se_group_members")
public class MemberGroup implements Serializable, Persistable<Integer> {

  @Id private Integer id;
  private Integer groupId;
  private Long userId;

  @CreatedDate private LocalDateTime createdTime;

  @LastModifiedDate private LocalDateTime updatedTime;

  @Override
  public boolean isNew() {
    return ObjectUtils.isEmpty(this.id);
  }
}