package com.platform.oauth.security.tenant;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.bootiful.oauth.security.tenant.TenanatController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/7
 */
@Tag(name = "租户信息管理")
@RestController
@RequestMapping("/tenant/manager/v1")
@RequiredArgsConstructor
public class TenantController {
    private final TenantManager tenantManager;

    @Operation(summary = "获取租户列表")
    @GetMapping("search")
    public Flux<Tenant> search(TenantRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.tenantManager.search(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Operation(summary = "获取租户分页")
    @GetMapping("page")
    public Mono<Page<Tenant>> page(TenantRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails ->
                this.tenantManager.page(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Hidden
    @GetMapping
    public Flux<Tenant> get(TenantRequest request, @PageableDefault(size = 100) Pageable pageable) {
        return this.tenantManager.search(request, pageable);
    }

    @Operation(summary = "增加租户")
    @PostMapping
    public Mono<Tenant> post(@Valid @RequestBody TenantRequest request) {
        return ReactiveSecurityDetailsHolder.getContext()
                .map(securityDetails -> request.pid(securityDetails.getTenantId()))
                .flatMap(this.tenantManager::add);
    }

    @Operation(summary = "修改租户")
    @PutMapping("{id}")
    public Mono<Tenant> put(@PathVariable Integer id, @Valid @RequestBody TenantRequest request) {
        request.setId(id);
        return this.tenantManager.operation(request);
    }
}