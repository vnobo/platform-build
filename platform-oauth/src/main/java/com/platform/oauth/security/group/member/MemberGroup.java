package com.platform.oauth.security.group.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * com.bootiful.oauth.security.group.GroupMember
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
@Schema(name = "角色用户")
@Data
@Table("se_group_members")
public class MemberGroup implements Serializable, Persistable<Long> {

    @Id
    private Long id;

    @NotBlank(message = "角色[groupCode]不能为空!")
    private String groupCode;

    @NotBlank(message = "用户[userCode]不能为空!")
    private String userCode;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}