package com.platform.system.core.custom.column;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * @author jjh
 * @date 2021/8/2
 */
public interface CustomColumnRepository extends R2dbcRepository<CustomColumn, Long> {

    /**
     * 删除同类型母版菜单
     *
     * @param category 母版菜单ID
     * @return 删除数量
     */
    Mono<Integer> deleteByCategory(long category);
}