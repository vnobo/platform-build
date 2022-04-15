package com.bootiful.system.core.notice;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * @author jjh
 * @date 2021/6/7
 */
public interface NoticeRepository extends R2dbcRepository<Notice, Integer> {

    /**
     * 根据ID 系统类型删除
     *
     * @param id     ia
     * @param system 系统类型
     * @return 删除条数
     */
    @Query("delete from sys_notifications where id=:id and system ilike :system")
    @Modifying
    Mono<Integer> deleteByIdSys(Integer id, String system);
}