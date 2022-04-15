package com.bootiful.system.core.news;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * @author jjh
 * @date 2021/6/15
 */
public interface NewsRepository extends R2dbcRepository<News, Long> {
    /**
     * 根据ID 系统类型删除
     *
     * @param id     ia
     * @param system 系统类型
     * @return 删除条数
     */
    @Query("delete from sys_news where id=:id and system ilike :system")
    @Modifying
    Mono<Integer> deleteByIdSys(Long id, String system);
}