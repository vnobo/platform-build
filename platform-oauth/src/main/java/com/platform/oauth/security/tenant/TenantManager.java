package com.platform.oauth.security.tenant;

import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.commons.utils.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.tenant.TenantService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TenantManager extends BaseAutoToolsUtil {
    private final TenantRepository tenantRepository;

    public Flux<Tenant> search(TenantRequest tenantRequest, Pageable pageable) {
        return super.entityTemplate
                .getDatabaseClient()
                .sql(tenantRequest.toQuerySql() + SqlUtils.applyPage(pageable))
                .map((row, rowMetadata) -> mappingR2dbcConverter.read(Tenant.class, row, rowMetadata))
                .all();
    }

    public Mono<Page<Tenant>> page(TenantRequest tenantRequest, Pageable pageable) {
        return search(tenantRequest, pageable)
                .collectList()
                .zipWith(
                        super.entityTemplate
                                .getDatabaseClient()
                                .sql(tenantRequest.toCountSql())
                                .map(row -> mappingR2dbcConverter.read(Long.class, row))
                                .one())
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Mono<Tenant> loadById(Integer id) {
        return this.tenantRepository.findById(id);
    }

    public Mono<Tenant> loadByCode(String code) {
        return this.tenantRepository.findByCode(code);
    }

    public Mono<Tenant> add(TenantRequest tenantRequest) {
        return this.loadByCode(tenantRequest.getCode()).switchIfEmpty(this.operation(tenantRequest));
    }

    public Mono<Tenant> operation(TenantRequest groupRequest) {
        return this.save(groupRequest.toTenant());
    }

    public Mono<Tenant> save(Tenant tenant) {
        if (tenant.isNew()) {
            return this.tenantRepository.save(tenant);
        } else {
            assert tenant.getId() != null;
            return this.tenantRepository.findById(tenant.getId()).flatMap(
                    old -> {
                        tenant.setCreatedTime(old.getCreatedTime());
                        return this.tenantRepository.save(tenant);
                    });
        }
    }
}