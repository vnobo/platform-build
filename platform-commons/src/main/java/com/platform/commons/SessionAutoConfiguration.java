package com.platform.commons;

import io.micrometer.core.lang.NonNull;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.Session;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.config.SessionConfiguration
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Session.class, WebSessionManager.class, Mono.class})
@AutoConfigureBefore({
  org.springframework.boot.autoconfigure.session.SessionAutoConfiguration.class,
  WebSessionIdResolverAutoConfiguration.class
})
public class SessionAutoConfiguration {

  @Bean
  public WebSessionIdResolver webSessionIdResolver() {
    HeaderWebSessionIdResolver resolver = new CustomHeaderWebSessionIdResolver();
    resolver.setHeaderName("X-Auth-Token");
    return resolver;
  }

  static class CustomHeaderWebSessionIdResolver extends HeaderWebSessionIdResolver {
    private final String xRequestedWith = "X-Requested-With";
    private final String xmlHttpRequest = "XMLHttpRequest";
    private final CookieWebSessionIdResolver cookieWebSessionIdResolver =
        new CookieWebSessionIdResolver();

    @Override
    public void setSessionId(ServerWebExchange exchange, @NonNull String id) {
      List<String> requestedWith =
          exchange.getRequest().getHeaders().getOrDefault(xRequestedWith, Collections.emptyList());
      if (requestedWith.contains(xmlHttpRequest)) {
        super.setSessionId(exchange, id);
      } else {
        cookieWebSessionIdResolver.setSessionId(exchange, id);
      }
    }

    @Override
    public List<String> resolveSessionIds(@NonNull ServerWebExchange exchange) {
      List<String> requestedWith =
          exchange.getRequest().getHeaders().getOrDefault(xRequestedWith, Collections.emptyList());
      if (requestedWith.contains(xmlHttpRequest)) {
        return super.resolveSessionIds(exchange);
      }
      return cookieWebSessionIdResolver.resolveSessionIds(exchange);
    }

    @Override
    public void expireSession(ServerWebExchange exchange) {
      List<String> requestedWith =
          exchange.getRequest().getHeaders().getOrDefault(xRequestedWith, Collections.emptyList());
      if (requestedWith.contains(xmlHttpRequest)) {
        super.expireSession(exchange);
      } else {
        cookieWebSessionIdResolver.expireSession(exchange);
      }
    }
  }
}