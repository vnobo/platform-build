package com.bootiful.system.core.custom.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * com.bootiful.oauth.security.tenant.TenantRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
public interface UserConfigurationRepository extends R2dbcRepository<UserConfiguration, Long> {

}