package com.platform.oauth.security.group.authority;

import com.platform.oauth.security.SimpleAuthority;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * com.alex.oauth.security.GroupAuthorities
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Data
@Table("se_group_authorities")
public class AuthorityGroup implements Persistable<Integer>, SimpleAuthority {

    @Id
    private Integer id;
    private Integer groupId;
    private String authority;

    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }
}