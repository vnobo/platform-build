package com.platform.system.core.menu;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * com.alex.oauth.security.AuthorityDict
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Schema(title = "资源菜单")
@Data
@Table("sys_authority_dict")
public class MenuDict implements Serializable, Persistable<Integer> {

  @Id private Integer id;

  @Schema(title = "权限名称,比如: ROLE_ADMIN_GROUP")
  private String authority;

  @Schema(title = "资源名称")
  private String name;

  @Schema(title = "资源详细介绍")
  private String description;

  @Schema(title = "资源路径")
  private String path;

  @Schema(title = "资源父级ID")
  private Integer pid;

  @Schema(title = "资源排序")
  private Integer sort;

  @Schema(title = "资源类型(前端)")
  private Integer type;

  @Schema(title = "扩展字段")
  private JsonNode extend;

  @Schema(title = "系统类型[system]不能为空! 如:country, poverty, points, grid, homestead, toilets")
  private SystemType system;

  @CreatedDate private LocalDateTime createdTime;

  @LastModifiedDate private LocalDateTime updatedTime;

  @Override
  public boolean isNew() {
    return ObjectUtils.isEmpty(this.id);
  }
}