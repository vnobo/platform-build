package com.bootiful.gateway.weixin;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.bootiful.commons.security.AuthenticationToken;
import com.bootiful.commons.security.SecurityTokenHelper;
import com.bootiful.gateway.security.ReactiveSecurityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@Service
@RequiredArgsConstructor
public class WxMaUserService {

    private final ReactiveSecurityManager reactiveUserDetailsService;

    /**
     * 微信 匿名登录用户
     *
     * @param session  微信 openid
     * @param exchange 会话
     * @return 登录token
     */
    public Mono<AuthenticationToken> anonymousLogin(WxMaJscode2SessionResult session, ServerWebExchange exchange) {

        Mono<UserDetails> userDetails = Mono.just(org.springframework.security.core.userdetails.User
                .withUsername("anonymous#" + session.getOpenid())
                .authorities("ROLE_GUEST")
                .password(UUID.randomUUID().toString())
                .disabled(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .build());
        return userDetails.map(userDetail -> new UsernamePasswordAuthenticationToken(userDetail, userDetail.getPassword()
                        , userDetail.getAuthorities()))
                .flatMap(authentication -> SecurityTokenHelper.authenticationTokenMono(exchange, authentication));
    }

    /**
     * 微信注册给定默认用户组
     *
     * @param wxRequest 微信登录用户
     * @param exchange  会话
     * @return 默认登录token实力
     */
    public Mono<AuthenticationToken> login(WxRequest wxRequest, ServerWebExchange exchange) {
        return this.reactiveUserDetailsService.winXinLogin(wxRequest
                        .system(SecurityTokenHelper.systemForHeader(exchange)))
                .flatMap(authentication -> SecurityTokenHelper.authenticationTokenMono(exchange, authentication))
                .delayUntil(res -> SecurityTokenHelper.removeToken(exchange));
    }

}