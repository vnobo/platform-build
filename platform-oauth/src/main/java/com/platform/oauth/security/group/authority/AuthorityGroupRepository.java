package com.platform.oauth.security.group.authority;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.GroupAuthorityRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/20
 */
public interface AuthorityGroupRepository extends R2dbcRepository<AuthorityGroup, Integer> {

    /**
     * 根据权限ID 查找所有关联权限
     *
     * @param groupCode 角色
     * @return 所有权限
     */
    Flux<AuthorityGroup> findByGroupCode(String groupCode);

    /**
     * 根据权限ID 查找所有关联权限
     *
     * @param groupCode 角色
     * @return 所有权限
     */
    Mono<Integer> deleteByGroupCode(String groupCode);
}