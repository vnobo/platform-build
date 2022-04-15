package com.bootiful.system.core.configurations;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * com.bootiful.oauth.security.tenant.TenantRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
public interface ConfigurationRepository extends R2dbcRepository<Configuration, Long> {

}