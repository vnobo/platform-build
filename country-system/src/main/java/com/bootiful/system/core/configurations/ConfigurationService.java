package com.bootiful.system.core.configurations;

import com.bootiful.commons.client.CountryClient;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.bootiful.commons.utils.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
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
public class ConfigurationService extends BaseAutoToolsUtil {

    private final ConfigurationRepository configurationRepository;
    private final CountryClient countryClient;

    public Flux<Configuration> search(ConfigurationRequest request) {
        return entityTemplate.select(Query.query(request.toCriteria()), Configuration.class);
    }

    public Mono<Configuration> batch(BatchConfigurationRequest request) {
        return this.operation(request.toRequest())
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(configuration -> {
                    Flux<Tenant> batchTenants = this.countryClient
                            .loadTenant(Tenant.builder().id(configuration.getTenantId()).build());
                    if (request.getIsBatch()) {
                        batchTenants = this.countryClient
                                .loadTenant(Tenant.builder().code(configuration.getTenantCode()).build());
                    }
                    batchTenants.flatMap(tenant -> this.operation(ConfigurationRequest.builder().tenantId(tenant.getId())
                                    .tenantCode(tenant.getCode()).name(request.getName()).type(request.getType())
                                    .configuration(request.getConfiguration()).system(request.getSystem()).build()))
                            .subscribe(result -> log.debug("租户[{}]批量修改成功 {}!",
                                    result.getTenantCode(), result.getName()));
                });

    }

    public Mono<Configuration> operation(ConfigurationRequest request) {
        Mono<ConfigurationRequest> existsBool = this.search(request)
                .map(oldConfig -> {
                    request.setId(oldConfig.getId());
                    return request;
                }).last(request);
        return existsBool.flatMap(req -> this.save(req.toConfiguration()));
    }

    public Mono<Configuration> save(Configuration configuration) {
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