package com.platform.oauth.security.tenant.member;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.tenant.TenantRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
public interface MemberTenantRepository extends R2dbcRepository<MemberTenant, Long> {

    /**
     * 查询群众租户
     *
     * @param userCode 用户 ID
     * @return 群众租户关系
     */
    Flux<MemberTenant> findByUserCode(String userCode);

    /**
     * 根据用户ID删除
     *
     * @param userCode 用户ID
     * @return 删除用户租户
     */
    Mono<Integer> deleteByUserCode(String userCode);
}