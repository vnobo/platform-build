package com.platform.gateway.security;

import com.platform.commons.annotation.RestServerException;
import com.platform.commons.security.AuthenticationToken;
import com.platform.commons.security.SecurityTokenHelper;
import com.platform.gateway.client.SystemClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * com.npc.oauth.security.SecurityController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/14
 */
@Tag(name = "用户操作管理")
@RestController
@RequestMapping("/oauth2/v1/")
@RequiredArgsConstructor
public class SecurityController {
  private final ReactiveSecurityManager detailsService;
  private final ReactiveUserDetailsService userDetailsService;
  private final WebSessionManager webSessionManager;
  private final SystemClient systemClient;

  @Operation(summary = "手机验证码发送", description = "返回认证信息TOKEN")
  @GetMapping("phone/code")
  public Mono<CsrfToken> phoneCode(String phone, ServerWebExchange exchange) {
    CsrfToken csrfToken =
        exchange.getRequiredAttribute(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME);
    return systemClient.smsSend(phone).flatMap(code -> Mono.defer(() -> Mono.just(csrfToken)));
  }

  @Operation(summary = "手机登录", description = "返回认证信息TOKEN")
  @PostMapping("phone/login")
  public Mono<AuthenticationToken> phoneLogin(
      @Valid @RequestBody LoginRequest loginRequest, ServerWebExchange exchange) {
    return systemClient
        .smsCheck(loginRequest)
        .filter(res -> res)
        .switchIfEmpty(
            Mono.defer(() -> Mono.error(RestServerException.withMsg(1401, "验证码错误,请重试!"))))
        .publishOn(Schedulers.boundedElastic())
        .doOnNext(
            res ->
                exchange
                    .getSession()
                    .doOnNext(webSession -> webSession.setMaxIdleTime(Duration.ofDays(2)))
                    .subscribe())
        .flatMap(
            res ->
                detailsService.appLogin(
                    loginRequest.system(SecurityTokenHelper.systemForHeader(exchange))))
        .flatMap(
            authentication -> SecurityTokenHelper.authenticationTokenMono(exchange, authentication))
        .delayUntil(res -> SecurityTokenHelper.removeToken(exchange));
  }

  @Operation(summary = "获取用户 CSRF TOKEN", description = "返回认证信息TOKEN")
  @GetMapping("csrf")
  public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
    CsrfToken csrfToken =
        exchange.getRequiredAttribute(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME);
    return Mono.defer(() -> Mono.just(csrfToken));
  }

  @Operation(summary = "用户登录,获取会话TOKEN", description = "返回认证信息TOKEN")
  @GetMapping("token")
  public Mono<AuthenticationToken> token(WebSession session) {
    return Mono.just(AuthenticationToken.build(session));
  }

  @Operation(summary = "用户刷新当前TOKEN", description = "返回认证信息TOKEN")
  @GetMapping("refresh")
  public Mono<AuthenticationToken> refresh(
      ServerWebExchange exchange, Authentication authentication) {
    ServerWebExchange exchangeNew =
        exchange
            .mutate()
            .request(
                exchange
                    .getRequest()
                    .mutate()
                    .headers(
                        httpHeaders -> {
                          httpHeaders.remove("X-Auth-Token");
                          httpHeaders.remove("Cookie");
                        })
                    .build())
            .build();
    Mono<WebSession> webSessionMono = webSessionManager.getSession(exchangeNew);
    return this.userDetailsService
        .findByUsername(authentication.getName())
        .map(
            userDetails ->
                new SecurityContextImpl(
                    new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities())))
        .flatMap(
            securityContext ->
                webSessionMono.doOnNext(
                    webSession ->
                        webSession
                            .getAttributes()
                            .put(
                                WebSessionServerSecurityContextRepository
                                    .DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                                securityContext)))
        .delayUntil(WebSession::save)
        .map(AuthenticationToken::build)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
  }

  /**
   * 修改密码
   *
   * @param authentication 当前登录用户认证信息
   * @param password 新密码
   * @param exchange 请求会话
   * @return 返回新的认证信息需要重新更新TOKEN
   */
  @Operation(summary = "用户修改自己密码", description = "返回新的认证信息需要更新当前TOKEN")
  @PostMapping("change/password")
  public Mono<AuthenticationToken> changePassword(
      @Pattern(
              regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*$",
              message = "登录密码[password]必须为,最少6位,包括至少1个大写字母，1个小写字母，1个数字.")
          String password,
      ServerWebExchange exchange,
      Authentication authentication) {
    return this.detailsService
        .updatePassword((UserDetails) authentication.getPrincipal(), password)
        .map(
            userDetails ->
                new UsernamePasswordAuthenticationToken(
                    userDetails, authentication.getCredentials(), userDetails.getAuthorities()))
        .flatMap(
            authenticationToken ->
                SecurityTokenHelper.authenticationTokenMono(exchange, authenticationToken))
        .delayUntil(result -> SecurityTokenHelper.removeToken(exchange));
  }

  @PostMapping("tenant/cut")
  @Operation(summary = "用户切换租户", description = "返回新的认证信息需要更新当前TOKEN")
  public Mono<AuthenticationToken> tenantCut(
      @Valid @RequestBody TenantCutRequest cutRequest, ServerWebExchange exchange) {
    return this.detailsService
        .tenantCut(cutRequest)
        .flatMap(simplerSecurityDetails -> exchange.getSession().map(AuthenticationToken::build))
        .delayUntil(authenticationToken -> SecurityTokenHelper.removeToken(exchange));
  }
}