package com.platform.commons;

import com.platform.commons.annotation.CustomServerAuthenticationEntryPoint;
import com.platform.commons.client.CountryClient;
import com.platform.commons.security.ReactiveUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
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
  ReactiveUserDetailsService.class,
  ServerSecurityContextRepository.class
})
@AutoConfigureAfter(
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
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
  @ConditionalOnBean(CountryClient.class)
  public ReactiveUserDetailsService userDetailsService(CountryClient countryClient) {
    return new ReactiveUserDetailsServiceImpl(countryClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, ServerSecurityContextRepository contextRepository) {

    http.authorizeExchange(exchange -> exchange.anyExchange().authenticated())
        .securityContextRepository(contextRepository)
        .httpBasic(
            httpBasicSpec ->
                httpBasicSpec.authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
        .csrf(
            csrfSpec ->
                csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
    return http.build();
  }
}