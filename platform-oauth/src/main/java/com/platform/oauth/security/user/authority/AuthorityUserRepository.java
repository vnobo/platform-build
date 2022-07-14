package com.platform.oauth.security.user.authority;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.AuthorityRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/27
 */
public interface AuthorityUserRepository extends R2dbcRepository<AuthorityUser, Integer> {

    /**
     * 根据用户ID删除
     *
     * @param userCode 用户ID
     * @return 删除用户租户
     */
    Mono<Integer> deleteByUserCode(String userCode);

    /**
     * 根据用户ID删除
     *
     * @param userCode 用户ID
     * @return 删除用户租户
     */
    Flux<AuthorityUser> findByUserCode(String userCode);
}