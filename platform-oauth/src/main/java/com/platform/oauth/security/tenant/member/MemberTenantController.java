package com.platform.oauth.security.tenant.member;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@Tag(name = "租户用户管理")
@RestController
@RequestMapping("/tenant/member/manager/v1")
@RequiredArgsConstructor
public class MemberTenantController {

  private final MemberTenantManager memberTenantManager;

  @GetMapping("search")
  public Flux<MemberTenantOnly> search(MemberTenantRequest request) {
    return this.memberTenantManager.search(request);
  }

  @PostMapping("batch")
  public Flux<MemberTenantOnly> batch(@RequestBody MemberTenantRequest request) {
    return this.memberTenantManager.batch(request);
  }

  @PostMapping
  public Mono<MemberTenantOnly> post(@Valid @RequestBody MemberTenantRequest request) {
    return this.memberTenantManager.operation(request);
  }

  @DeleteMapping
  public Mono<Void> delete(@RequestBody MemberTenantRequest request) {
    return this.memberTenantManager.delete(request);
  }
}