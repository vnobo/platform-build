package com.platform.oauth.security.group.member;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.group.GroupMemberRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
public interface MemberGroupRepository extends R2dbcRepository<MemberGroup, Long> {

    /**
     * 根据用户ID删除
     *
     * @param userCode 用户ID
     * @return 删除用户角色
     */
    Mono<Integer> deleteByUserCode(String userCode);

    /**
     * 根据角色ID 删除
     *
     * @param groupCode 角色ID
     * @return 删除用户角色
     */
    Mono<Integer> deleteByGroupCode(String groupCode);
}