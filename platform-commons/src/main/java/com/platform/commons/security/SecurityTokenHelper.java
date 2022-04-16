package com.platform.commons.security;

import com.platform.commons.utils.SystemType;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * com.bootiful.commons.security.SecurityDetailsDesc 安全认证token工具类
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/15
 */
@Log4j2
public class SecurityTokenHelper {

  public static final String SECURITY_TOKEN_CONTEXT = "COUNTRY_SECURITY_DETAILS_CONTEXT";
  public static final String ADMINISTRATORS_GROUP_ROLE_NAME = "ROLE_GROUP_ADMINISTRATORS";

  /**
   * 认证登录,设置登录后的session
   *
   * @param exchange 请求session
   * @return 登录后的 token
   */
  public static Mono<AuthenticationToken> authenticationTokenMono(
      ServerWebExchange exchange, Authentication authentication) {

    return exchange
        .getSession()
        .doOnNext(
            session ->
                session
                    .getAttributes()
                    .put(
                        WebSessionServerSecurityContextRepository
                            .DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                        new SecurityContextImpl(authentication)))
        .delayUntil(WebSession::changeSessionId)
        .map(AuthenticationToken::build)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
  }

  public static UserDetails buildUserDetails(LoginSecurityDetails securityDetails) {
    return org.springframework.security.core.userdetails.User.withUsername(
            securityDetails.getUsername())
        .authorities(securityDetails.getAuthorities())
        .password(securityDetails.getPassword())
        .disabled(!securityDetails.getEnabled())
        .accountExpired(!securityDetails.getEnabled())
        .credentialsExpired(!securityDetails.getEnabled())
        .accountLocked(!securityDetails.getEnabled())
        .build();
  }

  /**
   * 从会话中获取系统类型
   *
   * @param exchange 会话
   * @return 系统类型
   */
  public static SystemType systemForHeader(ServerWebExchange exchange) {
    HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
    List<String> stringList = httpHeaders.getOrDefault("login-system", new ArrayList<>());
    String system = SystemType.poverty.name();
    if (stringList.size() > 0) {
      String valueStr = stringList.get(0);
      if (StringUtils.hasLength(valueStr)) {
        system = valueStr;
      }
    } else {
      log.warn("获取系统类型为空,系统默认系统类型: {}", system);
    }
    return SystemType.ofValue(system);
  }

  /**
   * 从上下文中获取系统类型
   *
   * @param context 运行上下文
   * @return 系统类型
   */
  public static SystemType systemForContext(ContextView context) {
    Optional<SystemType> exchangeOpt = context.getOrEmpty("system");
    SystemType systemType = SystemType.poverty;
    if (exchangeOpt.isPresent()) {
      systemType = exchangeOpt.get();
    } else {
      log.warn("获取系统类型为空,系统默认系统类型: {}", systemType);
    }
    return systemType;
  }

  /**
   * 将Token保存在Session
   *
   * @param exchange 绘画请求
   * @param securityDetails 用户认证信息
   * @return 无返回
   */
  public static Mono<Void> saveToken(ServerWebExchange exchange, SecurityDetails securityDetails) {
    return exchange
        .getSession()
        .doOnNext((session) -> putToken(session.getAttributes(), securityDetails))
        .flatMap(WebSession::save);
  }

  /**
   * 将token 保存session map中
   *
   * @param attributes session map {@link WebSession#getAttributes()}
   * @param securityDetails 用户认证信息
   */
  private static void putToken(Map<String, Object> attributes, SecurityDetails securityDetails) {
    if (securityDetails.getUserId() == -1) {
      attributes.remove(SECURITY_TOKEN_CONTEXT);
    } else {
      attributes.put(SECURITY_TOKEN_CONTEXT, securityDetails);
    }
  }

  /**
   * 清除会话上下文的用户认证信息
   *
   * @param exchange 会话上下文
   * @return 无返回
   */
  public static Mono<Void> removeToken(ServerWebExchange exchange) {
    return exchange
        .getSession()
        .doOnNext(session -> session.getAttributes().remove(SECURITY_TOKEN_CONTEXT))
        .flatMap(WebSession::save)
        .contextWrite(context -> ReactiveSecurityDetailsHolder.clearContext().apply(context));
  }
}