package com.bootiful.system.core.custom.user;

import com.bootiful.commons.client.CountryClient;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.bootiful.commons.utils.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * com.bootiful.system.core.configurations.ConfigurationService
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserConfigurationService extends BaseAutoToolsUtil {

    private final UserConfigurationRepository configurationRepository;
    private final CountryClient countryClient;

    public Flux<UserConfiguration> search(UserConfigurationRequest request) {
        return entityTemplate.select(Query.query(request.toCriteria()), UserConfiguration.class);
    }

    public Mono<UserConfiguration> batch(UserBatchConfigurationRequest request) {
        return this.operation(UserConfigurationRequest.of(request.getTenantId(), request.getTenantCode(),
                        request.getSystem(), request.getConfiguration(), request.getType()))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(result -> {
                    Flux<Tenant> batchTenants = this.countryClient
                            .loadTenant(Tenant.builder().id(request.getTenantId()).build());
                    if (request.getIsBatch()) {
                        batchTenants = this.countryClient
                                .loadTenant(Tenant.builder().code(request.getTenantCode()).build());
                    }
                    batchTenants
                            .flatMap(tenant -> this.operation(UserConfigurationRequest.of(tenant.getId(), tenant.getCode(),
                                    request.getSystem(), request.getConfiguration(), request.getType())))
                            .subscribe(result1 -> log.debug("租户[{}]批量修改成功 {}!",
                                    result1.getTenantCode(), request.getSystem()));
                });

    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<UserConfiguration> operation(UserConfigurationRequest request) {
        Mono<UserConfigurationRequest> existsBool = this.search(UserConfigurationRequest
                        .of(request.getTenantId(), request.getSystem(), request.getType()))
                .map(oldConfig -> {
                    request.setId(oldConfig.getId());
                    return request;
                }).last(request);
        return existsBool.flatMap(req -> this.save(req.toConfiguration()));
    }

    public Mono<UserConfiguration> save(UserConfiguration configuration) {
        if (configuration.isNew()) {
            return this.configurationRepository.save(configuration);
        } else {
            assert configuration.getId() != null;
            return this.configurationRepository.findById(configuration.getId())
                    .flatMap(old -> {
                        configuration.setCreatedTime(old.getCreatedTime());
                        return this.configurationRepository.save(configuration);
                    });
        }

    }

    public Mono<Void> delete(Long id) {
        return this.configurationRepository.deleteById(id);
    }
}