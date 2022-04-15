package com.platform.oauth.security.tenant.member;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * com.bootiful.oauth.security.tenant.TenantService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Service
@RequiredArgsConstructor
public class MemberTenantManager extends BaseAutoToolsUtil {
    private final MemberTenantRepository memberTenantRepository;

    public Flux<MemberTenantOnly> search(MemberTenantRequest request) {
        String querySql = "select se_tenant_members.*,se_tenants.code as tenant_code, se_tenants.name as tenant_name, " +
                "se_users.name as user_name,se_tenants.extend as tenant_extend " +
                "from se_tenant_members,se_tenants,se_users " +
                "where se_tenant_members.tenant_id = se_tenants.id " +
                "and se_tenant_members.user_id = se_users.id ";
        return super.entityTemplate.getDatabaseClient()
                .sql(querySql + request.toWhereSql())
                .map((row, metadata) -> super.mappingR2dbcConverter.read(MemberTenantOnly.class, row, metadata))
                .all();
    }

    public Flux<MemberTenantOnly> batch(MemberTenantRequest request) {
        return Flux.fromStream(request.getUserIds().parallelStream()
                        .map(id -> MemberTenantRequest.of(request.getTenantId(), id).isDefault(request.getIsDefault())))
                .flatMap(this::operation);
    }

    public Mono<MemberTenantOnly> operation(MemberTenantRequest memberTenantRequest) {
        return this.save(memberTenantRequest.toMemberTenant())
                .flatMap(memberTenant -> this.search(memberTenantRequest).singleOrEmpty());
    }


    public Mono<Void> delete(MemberTenantRequest request) {
        return Flux.fromStream(request.getUserIds().parallelStream()
                        .map(id -> MemberTenantRequest.of(request.getTenantId(), id)))
                .flatMap(memberTenantRequest -> this.memberTenantRepository
                        .findOne(Example.of(memberTenantRequest.toMemberTenant())))
                .flatMap(this.memberTenantRepository::delete)
                .then();
    }
    public Mono<Integer> deleteByUserId(Long userId) {
        return this.memberTenantRepository.deleteByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<MemberTenant> save(MemberTenant memberTenant) {

        Flux<MemberTenant> source = this.memberTenantRepository.findByUserId(memberTenant.getUserId());

        Flux<MemberTenant> updateFalse = source.filter(old -> !old.getTenantId().equals(memberTenant.getTenantId()))
                .filter(MemberTenant::getIsDefault)
                .flatMap(old -> this.memberTenantRepository.save(old.isDefault(false)));

        Flux<MemberTenant> newUpdate = source.filter(old -> old.getTenantId().equals(memberTenant.getTenantId()));

        return newUpdate.count().filter(count -> count > 0)
                .flatMap(count -> newUpdate.filter(old -> !old.getIsDefault())
                        .flatMap(old -> this.memberTenantRepository.save(old.isDefault(true))).next()
                        .defaultIfEmpty(memberTenant))
                .switchIfEmpty(this.memberTenantRepository.save(memberTenant))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(result -> updateFalse.subscribe());

    }
}