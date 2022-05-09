package com.platform.commons.filter;

import com.platform.commons.client.CountryClient;
import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.commons.security.SecurityDetails;
import org.springframework.core.Ordered;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * com.bootiful.gateway.filter.SecurityDetailsWebFilter 加载用户安全险信息{@link SecurityDetails} 封装到会话中
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/22
 */
public record BeforeSecurityFilter(CountryClient countryClient) implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            Mono<SecurityDetails> securityDetailsToken = securityDetailsToken(exchange);
            exchange.getAttributes().put(SecurityDetails.class.getName(), securityDetailsToken);
            return chain.filter(exchange).contextWrite(ReactiveSecurityDetailsHolder
                    .withSecurityDetailsContext(securityDetailsToken));
        });
    }

    private Mono<SecurityDetails> securityDetailsToken(ServerWebExchange exchange) {
        return this.loadToken(exchange).switchIfEmpty(generateToken(exchange));
    }

    private Mono<SecurityDetails> loadToken(ServerWebExchange exchange) {
        return exchange.getSession().filter((session) -> session.getAttributes()
                        .containsKey(ReactiveSecurityHelper.SECURITY_TOKEN_CONTEXT))
                .mapNotNull((session) -> session.getAttribute(ReactiveSecurityHelper.SECURITY_TOKEN_CONTEXT));
    }

    private Mono<SecurityDetails> generateToken(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> countryClient.loadSecurity(securityContext.getAuthentication().getName())
                        .map(securityDetails -> securityDetails.authorities(securityContext
                                .getAuthentication().getAuthorities().stream().distinct()
                                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))))
                .cast(SecurityDetails.class)
                .delayUntil((securityDetails) -> ReactiveSecurityHelper.saveToken(exchange, securityDetails));
    }

    @Override
    public int getOrder() {
        return SecurityWebFiltersOrder.AUTHORIZATION.getOrder() + 100;
    }
}