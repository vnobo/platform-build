package com.bootiful.oauth.security.tenant;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.tenant.TenantRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
public interface TenantRepository extends R2dbcRepository<Tenant, Integer> {

    /**
     * 根据租户code查找租户
     *
     * @param code 租户CODE
     * @return 租户
     */
    Mono<Tenant> findByCode(String code);

}