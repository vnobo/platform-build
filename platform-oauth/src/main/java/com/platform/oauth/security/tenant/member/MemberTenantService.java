package com.platform.oauth.security.tenant.member;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.tenant.TenantService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Service
@RequiredArgsConstructor
public class MemberTenantService extends BaseAutoToolsUtil {
    private final MemberTenantRepository memberTenantRepository;

    public Mono<Page<MemberTenant>> page(MemberTenantRequest request, Pageable pageable) {
        return super.entityTemplate.select(Query.query(request.toCriteria()).with(pageable), MemberTenant.class)
                .collectList().zipWith(entityTemplate.count(Query.query(request.toCriteria()), MemberTenant.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<MemberTenant> operate(MemberTenantRequest request) {
        return Flux.fromStream(request.getUsers().parallelStream())
                .map(user -> MemberTenantRequest.of(request.getTenantCode(), user))
                .flatMap(this::save);
    }

    public Mono<Void> delete(Long id) {
        return this.memberTenantRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberTenant> save(MemberTenant memberTenant) {

        Flux<MemberTenant> source = this.memberTenantRepository.findByUserCode(memberTenant.getUserCode());

        Flux<MemberTenant> updateFalse = source.filter(old -> !old.getTenantCode().equals(memberTenant.getTenantCode()))
                .flatMap(old -> {
                    old.setIsDefault(false);
                    return this.memberTenantRepository.save(old);
                });

        Flux<MemberTenant> updateTrue = source.filter(old -> old.getTenantCode().equals(memberTenant.getTenantCode()));

        return updateTrue.delayUntil(result -> updateFalse).flatMap(old -> {
            old.setIsDefault(true);
            return this.memberTenantRepository.save(old);
        }).singleOrEmpty().switchIfEmpty(this.memberTenantRepository.save(memberTenant));
    }
}