package com.platform.commons.annotation;

import java.nio.charset.Charset;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.commons.annotation.CustomServerAuthenticationEntryPoint
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/3/28
 */
public class CustomServerAuthenticationEntryPoint extends HttpBasicServerAuthenticationEntryPoint {

  /**
   * 自定义认证错误信息
   *
   * @param exchange 请求会话
   * @param e 认证错误信息
   * @return 完成返回订阅, 无返回
   */
  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {

    String xRequestedWith = "X-Requested-With";
    String xmlHttpRequest = "XMLHttpRequest";
    List<String> requestedWith = exchange.getRequest().getHeaders().get(xRequestedWith);
    if (requestedWith != null && requestedWith.contains(xmlHttpRequest)) {
      return Mono.defer(() -> Mono.just(exchange.getResponse()))
          .flatMap(
              (response) -> {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String body =
                    "{\"code\":401,\"msg\":\"认证失败,检查你的用户名,密码是否正确或安全密钥是否过期!\",\"errors\":\""
                        + e.getMessage()
                        + "\"}";
                DataBufferFactory dataBufferFactory = response.bufferFactory();
                DataBuffer buffer = dataBufferFactory.wrap(body.getBytes(Charset.defaultCharset()));
                return response
                    .writeWith(Mono.just(buffer))
                    .doOnError((error) -> DataBufferUtils.release(buffer));
              });
    }
    return super.commence(exchange, e);
  }
}