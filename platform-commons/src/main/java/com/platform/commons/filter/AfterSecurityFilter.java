package com.platform.commons.filter;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.commons.security.SecurityDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.filter.AuthGlobalFilter 认证后封装订阅CSRF订阅启动保护 订阅登录用户SECURITY 信息封装
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/28
 * @see ReactiveSecurityDetailsHolder
 */
@RequiredArgsConstructor
public class AfterSecurityFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());

        Mono<SecurityDetails> securityDetailsMono = exchange.getAttribute(SecurityDetails.class.getName());

        if (csrfTokenMono != null) {
            if (securityDetailsMono != null) {
                return exchange.getSession().delayUntil(session -> securityDetailsMono
                                .mapNotNull(securityDetails -> session.getAttributes()
                                        .put(ReactiveSecurityHelper.SECURITY_TOKEN_CONTEXT, securityDetails)))
                        .delayUntil(WebSession::save)
                        .flatMap(session -> chain.filter(exchange));
            }
            return csrfTokenMono.flatMap(csrfToken -> chain.filter(exchange));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}