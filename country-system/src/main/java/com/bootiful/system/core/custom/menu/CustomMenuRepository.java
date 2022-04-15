package com.bootiful.system.core.custom.menu;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * @author jjh
 * @date 2021/8/2
 */
public interface CustomMenuRepository extends R2dbcRepository<CustomMenu, Long> {

    Mono<CustomMenu> findByMenuIdAndUserId(Long menuId, Long userId);
}