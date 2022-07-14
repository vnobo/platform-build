package com.platform.oauth.security.group.member;

import com.platform.commons.utils.CriteriaUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.query.Criteria;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * com.bootiful.oauth.security.user.UserRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Schema(name = "角色用户请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberGroupRequest extends MemberGroup implements Serializable {

    @NotNull(message = "用户[users]不能为空!", groups = Users.class)
    private Set<String> users;

    public static MemberGroup of(String groupCode, String userCode) {
        MemberGroup request = new MemberGroup();
        request.setGroupCode(groupCode);
        request.setUserCode(userCode);
        return request;
    }

    public MemberGroupRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public Criteria toCriteria() {
        return CriteriaUtils.build(this);
    }

    public interface Users {
    }
}