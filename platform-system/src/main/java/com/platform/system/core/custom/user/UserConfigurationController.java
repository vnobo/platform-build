package com.platform.system.core.custom.user;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.bootiful.system.core.configurations.ConfigurationController
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Tag(name = "用户自定义配置")
@RestController
@RequestMapping("user/configuration/v1")
@RequiredArgsConstructor
public class UserConfigurationController {

    private final UserConfigurationService configurationService;

    @GetMapping("search")
    public Flux<UserConfiguration> search(UserConfigurationRequest request) {
        return this.configurationService.search(request);
    }

    @GetMapping
    public Flux<UserConfiguration> find(UserConfigurationRequest request) {
        return ReactiveSecurityDetailsHolder.getContext()
                .flatMapMany(securityDetails -> this.configurationService.search(request
                        .tenantId(securityDetails.getTenantId())));
    }

    @PostMapping
    public Mono<UserConfiguration> post(@Valid @RequestBody UserConfigurationRequest request) {
        return this.configurationService.operation(request);
    }

    @PostMapping("batch")
    public Mono<UserConfiguration> batch(@Valid @RequestBody UserBatchConfigurationRequest request) {
        return this.configurationService.batch(request);
    }

    @PutMapping("{id}")
    public Mono<UserConfiguration> put(@PathVariable Long id, @Valid @RequestBody UserConfigurationRequest request) {
        request.setId(id);
        return this.configurationService.operation(request);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return this.configurationService.delete(id);
    }
}