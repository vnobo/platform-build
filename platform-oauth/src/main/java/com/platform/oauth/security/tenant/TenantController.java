package com.platform.oauth.security.tenant;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.utils.InitializingTenantUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
  private final InitializingTenantUtils initializingTenantUtils;

  @GetMapping("initializing/{code}")
  public Flux<Tenant> initializing(@PathVariable String code) {
    return this.initializingTenantUtils.initializing(code);
  }

  @GetMapping("search")
  public Flux<Tenant> search(TenantRequest request, Pageable pageable) {
    return ReactiveSecurityDetailsHolder.getContext()
        .flatMapMany(
            securityDetails ->
                this.tenantManager.search(
                    request.securityTenantCode(securityDetails.getTenantCode()), pageable));
  }

  @GetMapping("page")
  public Mono<Page<Tenant>> page(TenantRequest request, Pageable pageable) {
    return ReactiveSecurityDetailsHolder.getContext()
        .flatMap(
            securityDetails ->
                this.tenantManager.page(
                    request.securityTenantCode(securityDetails.getTenantCode()), pageable));
  }

  @GetMapping
  public Flux<Tenant> get(TenantRequest request, Pageable pageable) {
    return this.tenantManager.search(request, pageable);
  }

  @PostMapping
  public Mono<Tenant> post(@Valid @RequestBody TenantRequest request) {
    return ReactiveSecurityDetailsHolder.getContext()
        .map(securityDetails -> request.pid(securityDetails.getTenantId()))
        .flatMap(this.tenantManager::add);
  }

  @PutMapping("{id}")
  public Mono<Tenant> put(@PathVariable Integer id, @Valid @RequestBody TenantRequest request) {
    request.setId(id);
    return this.tenantManager.operation(request);
  }
}