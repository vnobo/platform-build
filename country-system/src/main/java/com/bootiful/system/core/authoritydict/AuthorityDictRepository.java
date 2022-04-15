package com.bootiful.system.core.authoritydict;

import com.bootiful.commons.utils.SystemType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/26
 */
public interface AuthorityDictRepository extends R2dbcRepository<AuthorityDict, Integer> {


    /**
     * 根据洗头工类型查找
     *
     * @param system 系统类型
     * @return 资源
     */
    Flux<AuthorityDict> findBySystemOrderBySort(SystemType system);
}