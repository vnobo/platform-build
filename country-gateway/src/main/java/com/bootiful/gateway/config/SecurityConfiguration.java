package com.bootiful.gateway.config;

import com.bootiful.commons.annotation.CustomServerAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

/**
 * ccom.bootiful.gateway.config.SecurityConfigurarion
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/3/28
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ServerSecurityContextRepository contextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .authorizeExchange(exchange -> {
                    exchange.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
                    exchange.pathMatchers("/", "/wx/user/*/v1/authorize",
                            "/oauth2/captcha/**", "/oauth2/v1/phone/*").permitAll();
                    exchange.anyExchange().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .httpBasic(httpBasicSpec -> httpBasicSpec
                        .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
                .securityContextRepository(contextRepository)
                .csrf(csrfSpec -> csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }

}