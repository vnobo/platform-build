package com.platform.gateway.security;

import com.platform.commons.security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * com.npc.oauth.security.SecurityController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/14
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/oauth2/v1/")
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityManager securityManager;
    private final ReactiveUserDetailsService userDetailsService;
    private final WebSessionManager webSessionManager;


    @Operation(summary = "获取 CSRF TOKEN", description = "返回认证信息TOKEN")
    @GetMapping("csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        CsrfToken csrfToken = exchange.getRequiredAttribute(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME);
        return Mono.defer(() -> Mono.just(csrfToken));
    }

    @Operation(summary = "登录 获取会话TOKEN", description = "返回认证信息TOKEN")
    @GetMapping("token")
    public Mono<AuthenticationToken> token(WebSession session) {
        return Mono.just(AuthenticationToken.build(session));
    }

    @Operation(summary = "刷新 当前会话TOKEN", description = "返回认证信息TOKEN")
    @GetMapping("refresh")
    public Mono<AuthenticationToken> refresh(ServerWebExchange exchange, Authentication authentication) {
        ServerWebExchange exchangeNew = exchange.mutate().request(exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove("X-Auth-Token");
                    httpHeaders.remove("Cookie");
                }).build()).build();
        Mono<WebSession> webSessionMono = webSessionManager.getSession(exchangeNew);
        return this.userDetailsService.findByUsername(authentication.getName())
                .map(userDetails -> new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities())))
                .flatMap(securityContext -> webSessionMono.doOnNext(webSession -> webSession.getAttributes()
                        .put(WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                                securityContext)))
                .delayUntil(WebSession::save)
                .map(AuthenticationToken::build)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    @GetMapping("me")
    public Mono<SecurityDetails> me() {
        return securityManager.
    }

    @PostMapping("register")
    public Mono<LoginSecurityDetails> register(@Valid @RequestBody RegisterRequest regRequest) {
        return this.securityManager.register(regRequest);
    }

    /**
     * 修改密码
     *
     * @param authentication 当前登录用户认证信息
     * @param password       新密码
     * @param exchange       请求会话
     * @return 返回新的认证信息需要重新更新TOKEN
     */
    @Operation(summary = "用户修改自己密码", description = "返回新的认证信息需要更新当前TOKEN")
    @PostMapping("change/password")
    public Mono<AuthenticationToken> changePassword(@Pattern(
            regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
                                                    String password, ServerWebExchange exchange,
                                                    Authentication authentication) {
        return this.securityManager.updatePassword((UserDetails) authentication.getPrincipal(), password)
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails, authentication.getCredentials(), userDetails.getAuthorities()))
                .flatMap(authenticationToken -> ReactiveSecurityHelper
                        .authenticationTokenMono(exchange, authenticationToken))
                .delayUntil(result -> ReactiveSecurityHelper.removeToken(exchange));
    }

    @PostMapping("tenant/cut")
    @Operation(summary = "切换租户", description = "返回新的认证信息需要更新当前TOKEN")
    public Mono<AuthenticationToken> tenantCut(@Valid @RequestBody TenantCutRequest cutRequest,
                                               ServerWebExchange exchange) {
        return this.securityManager.tenantCut(cutRequest)
                .flatMap(simplerSecurityDetails -> exchange.getSession().map(AuthenticationToken::build))
                .delayUntil(authenticationToken -> ReactiveSecurityHelper.removeToken(exchange));
    }
}