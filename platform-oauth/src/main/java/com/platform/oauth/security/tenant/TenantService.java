package com.platform.oauth.security.tenant;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
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
public class TenantService extends BaseAutoToolsUtil {
    private final TenantRepository tenantRepository;

    public Flux<Tenant> search(TenantRequest request, Pageable pageable) {
        return super.entityTemplate.select(Query.query(request.toCriteria()).with(pageable), Tenant.class);
    }

    public Mono<Page<Tenant>> page(TenantRequest request, Pageable pageable) {
        return search(request, pageable).collectList()
                .zipWith(super.entityTemplate.count(Query.query(request.toCriteria()), Tenant.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Mono<Tenant> loadByCode(String code) {
        return this.tenantRepository.findByCode(code);
    }

    public Mono<Tenant> operation(TenantRequest tenantRequest) {
        return this.loadByCode(tenantRequest.getCode())
                .flatMap(old -> this.save(tenantRequest.id(old.getId()).toTenant()))
                .switchIfEmpty(this.save(tenantRequest.toTenant()));
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