package com.platform.gateway.weixin;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.platform.commons.security.AuthenticationToken;
import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.gateway.security.SecurityManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * com.bootiful.oauth.core.weixin.WxMaUserService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@Service
public record WxMaUserService(SecurityManager reactiveUserDetailsService) {

  /**
   * 微信 匿名登录用户
   *
   * @param session  微信 openid
   * @param exchange 会话
   * @return 登录token
   */
  public Mono<AuthenticationToken> anonymousLogin(
          WxMaJscode2SessionResult session, ServerWebExchange exchange) {

    Mono<UserDetails> userDetails =
            Mono.just(
                    org.springframework.security.core.userdetails.User.withUsername(
                                    "anonymous#" + session.getOpenid())
                            .authorities("ROLE_GUEST")
                            .password(UUID.randomUUID().toString())
                            .disabled(false)
                            .accountExpired(false)
                            .credentialsExpired(false)
                            .accountLocked(false)
                            .build());
    return userDetails
            .map(
                    userDetail ->
                            new UsernamePasswordAuthenticationToken(
                                    userDetail, userDetail.getPassword(), userDetail.getAuthorities()))
            .flatMap(
                    authentication ->
                            ReactiveSecurityHelper.authenticationTokenMono(exchange, authentication));
  }

  /**
   * 微信注册给定默认用户组
   *
   * @param wxRequest 微信登录用户
   * @param exchange  会话
   * @return 默认登录token实力
   */
  public Mono<AuthenticationToken> login(WxRequest wxRequest, ServerWebExchange exchange) {
    return this.reactiveUserDetailsService.login(wxRequest)
            .flatMap( authentication -> ReactiveSecurityHelper.authenticationTokenMono(exchange, authentication))
            .delayUntil(res -> ReactiveSecurityHelper.removeToken(exchange));
  }
}