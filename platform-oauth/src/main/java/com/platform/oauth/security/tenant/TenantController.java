package com.platform.oauth.security.tenant;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.tenant.member.MemberTenant;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import com.platform.oauth.security.tenant.member.MemberTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/tenants/v1")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;
    private final MemberTenantService memberTenantService;

    @Operation(summary = "获取租户列表")
    @GetMapping("search")
    public Flux<Tenant> search(TenantRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.tenantService.search(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Operation(summary = "获取租户分页")
    @GetMapping("page")
    public Mono<Page<Tenant>> page(TenantRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails ->
                this.tenantService.page(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Operation(summary = "增加/修改")
    @PostMapping
    public Mono<Tenant> post(@Valid @RequestBody TenantRequest request) {
        return this.tenantService.operation(request);
    }

    @GetMapping("member")
    public Mono<Page<MemberTenant>> memberPage(MemberTenantRequest request, Pageable pageable) {
        return this.memberTenantService.page(request, pageable);
    }

    @PostMapping("member")
    public Flux<MemberTenant> post(@Validated(MemberTenantRequest.Users.class)
                                   @RequestBody MemberTenantRequest request) {
        return this.memberTenantService.operate(request);
    }

    @DeleteMapping("member")
    public Mono<Void> delete(Long id) {
        return this.memberTenantService.delete(id);
    }

}