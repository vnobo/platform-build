package com.platform.oauth.security;

import cn.hutool.core.util.ReUtil;
import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.security.SecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
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
@RequestMapping("oauth2/v1/")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityManager userDetailsService;

    @PostMapping("register")
    public Mono<LoginSecurityDetails> register(@RequestBody RegisterRequest regRequest) {
        Assert.isTrue(ReUtil.isMatch("^[a-zA-Z0-9_-]{5,16}$", regRequest.getUsername())
                , "登录用户名[username]必须为5到16位（字母，数字，下划线，减号）!");
        return this.userDetailsService.register(regRequest);
    }

    @GetMapping("login/{username}")
    public Mono<LoginSecurityDetails> login(@PathVariable String username) {
        return this.userDetailsService.login(username);
    }

    @GetMapping("load/security/{username}")
    public Mono<SimplerSecurityDetails> loadSecurity(@PathVariable String username) {
        return this.userDetailsService.loadSecurity(username);
    }

    @PostMapping("tenant/cut")
    public Mono<SimplerSecurityDetails> tenantCut(@Valid @RequestBody MemberTenantRequest request) {
        return this.userDetailsService.tenantCut(request);
    }

    @GetMapping("me")
    public Mono<SecurityDetails> me() {
        return ReactiveSecurityDetailsHolder.getContext()
                .delayUntil(securityDetails -> this.userDetailsService.loginSuccess(securityDetails.getUsername()));
    }

}