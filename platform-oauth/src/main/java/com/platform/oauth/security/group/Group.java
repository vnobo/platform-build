package com.platform.oauth.security.group;

import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

/**
 * com.alex.oauth.security.Groups
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Data
@Table("se_groups")
public class Group implements Serializable, Persistable<Integer> {

  @Id private Integer id;
  private Integer tenantId;
  private String tenantCode;
  private String name;
  private String description;

  @Schema(title = "角色类型")
  private Integer type;

  @Schema(title = "系统类型")
  private SystemType system;

  @CreatedDate private LocalDateTime createdTime;
  @LastModifiedDate private LocalDateTime updatedTime;

  @Override
  public boolean isNew() {
    return ObjectUtils.isEmpty(this.id);
  }
}