package com.platform.commons.filter;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.security.ReactiveSecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.commons.security.SecurityTokenFiter 后端服务传递风中初始化 {@link
 * ReactiveSecurityDetailsHolder} 工具类
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/16
 */
@Log4j2
@RequiredArgsConstructor
public class BeforeExchangeContextFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession().flatMap(session -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityDetailsHolder.withSecurityDetails(
                                session.getAttribute(ReactiveSecurityHelper.SECURITY_TOKEN_CONTEXT))))
                .contextWrite(context -> context.put("system", ReactiveSecurityHelper.systemForHeader(exchange)));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}