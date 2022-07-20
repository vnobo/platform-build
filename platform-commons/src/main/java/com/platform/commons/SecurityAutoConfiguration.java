package com.platform.commons;

import com.platform.commons.client.Oauth2Client;
import com.platform.commons.security.CustomServerAuthenticationEntryPoint;
import com.platform.commons.security.ReactiveSecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

/**
 * coupons in com.alex.web.config
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/14
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({
        EnableWebFluxSecurity.class,
        org.springframework.security.core.userdetails.ReactiveUserDetailsService.class,
        ServerSecurityContextRepository.class
})
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@RequiredArgsConstructor
public class SecurityAutoConfiguration {

    @Bean
    public ServerSecurityContextRepository contextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Oauth2Client.class)
    public org.springframework.security.core.userdetails.ReactiveUserDetailsService userDetailsService(Oauth2Client oauth2Client) {
        return new ReactiveSecurityUserService(oauth2Client);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http, ServerSecurityContextRepository contextRepository) {
        http.authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .securityContextRepository(contextRepository)
                .httpBasic(httpBasicSpec ->
                        httpBasicSpec.authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
                .csrf(csrfSpec ->
                        csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }
}