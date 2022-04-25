package com.platform.oauth.security.user.authority;

import com.platform.commons.utils.SystemType;
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
     * @param userId 用户ID
     * @return 删除用户租户
     */
    Mono<Integer> deleteByUserId(Long userId);

    /**
     * 根据用户ID 和 系统类型 获取用户权限
     *
     * @param userId 用户ID
     * @param system 系统类型
     * @return 记录
     */
    Flux<AuthorityUser> findByUserIdAndSystem(Long userId, SystemType system);
}