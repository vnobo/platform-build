package com.platform.oauth.security;

import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.security.SecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.npc.oauth.security.SecurityController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/14
 */
@Hidden
@RestController
@RequestMapping("/security/v1/")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityManager securityManager;


    @GetMapping("login/{username}")
    public Mono<LoginSecurityDetails> login(@PathVariable String username) {
        return this.securityManager.login(username);
    }

    @GetMapping("load/security/{username}")
    public Mono<SimplerSecurityDetails> loadSecurity(@PathVariable String username) {
        return this.securityManager.loadSecurity(username);
    }

    @PostMapping("tenant/cut")
    public Mono<SimplerSecurityDetails> tenantCut(@Valid @RequestBody MemberTenantRequest request) {
        return this.securityManager.tenantCut(request);
    }

    @GetMapping("me")
    public Mono<SecurityDetails> me() {
        return ReactiveSecurityDetailsHolder.getContext()
                .delayUntil(securityDetails -> this.securityManager.loginSuccess(securityDetails.getUsername()));
    }
}