package com.bootiful.oauth.security.group.member;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.group.GroupMemberRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
public interface MemberGroupRepository extends R2dbcRepository<MemberGroup, Integer> {

    /**
     * 根据权限查找
     *
     * @param groupId 组id
     * @param userId  用户id
     * @return 用户所属组
     */
    Mono<MemberGroup> findByGroupIdAndUserId(Integer groupId, Long userId);

    /**
     * 根据用户ID删除
     *
     * @param userId 用户ID
     * @return 删除用户角色
     */
    Mono<Integer> deleteByUserId(Long userId);

    /**
     * 根据角色ID 删除
     *
     * @param groupId 角色ID
     * @return 删除用户角色
     */
    Mono<Integer> deleteByGroupId(Integer groupId);
}