package com.bootiful.system.core.configurations;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "租户配置")
@RestController
@RequestMapping("tenant/configuration/v1")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("search")
    public Flux<Configuration> search(ConfigurationRequest request) {
        return this.configurationService.search(request);
    }

    @GetMapping
    public Mono<Configuration> find(ConfigurationRequest request) {
        return this.configurationService.search(request).singleOrEmpty();
    }

    @Operation(summary = "租户配置创建/修改")
    @PostMapping
    public Mono<Configuration> post(@Valid @RequestBody ConfigurationRequest request) {
        return this.configurationService.operation(request);
    }

    @Operation(summary = "租户配置批量创建/修改", description = "包含当前租户以下的租户配置修改")
    @PostMapping("batch")
    public Mono<Configuration> batch(@Valid @RequestBody BatchConfigurationRequest request) {
        return this.configurationService.batch(request);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return this.configurationService.delete(id);
    }
}