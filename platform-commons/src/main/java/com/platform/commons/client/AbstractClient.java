package com.platform.commons.client;

import com.platform.commons.annotation.ErrorResponse;
import com.platform.commons.annotation.exception.ClientRequestException;
import com.platform.commons.security.CustomServerAuthenticationEntryPoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * com.bootiful.commons.client.AbstractClient 访问服务基础工具类,使用 webclient 取派生. 使用此基础类统一系统化错误处理 统一调度优化查询
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
public abstract class AbstractClient implements InitializingBean {

    protected WebClient webClient;

    @Autowired
    public final void initWebClient(WebClient.Builder builder, ReactorLoadBalancerExchangeFilterFunction loadBalancer) {
        this.webClient = builder.filter(new ExceptionFilterFunction())
                .filter(new SecuritySessionHandlerFilterFunction())
                .filter(loadBalancer)
                .build();
    }

    /**
     * 配置 {@link AbstractClient#webClient} 访问base Host
     *
     * @param url 需要访问域名
     */
    public void initializeBaseUrl(String url) {
        this.webClient = this.webClient.mutate().baseUrl(url).build();
    }

    /**
     * 获取装备后的 client
     *
     * @return 装备后的client
     */
    public WebClient client() {
        return this.webClient;
    }

    /**
     * 工具类get请求
     *
     * @param path         请求路径
     * @param queryParams  查询参数
     * @param responseType 需要序列返回类型
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Flux<T> get(String path, Map<String, String> queryParams, Class<T> responseType) {
        return this.get(path, queryParams, responseType, new Object[1]);
    }

    /**
     * 工具类get请求
     *
     * @param path         请求路径
     * @param queryParams  查询参数
     * @param responseType 需要序列返回类型
     * @param uriVariables 路径里参数序列化
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Flux<T> get(String path, Map<String, String> queryParams, Class<T> responseType, Object... uriVariables) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        queryParams.forEach(multiValueMap::add);
        return this.webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(multiValueMap).build(uriVariables))
                .retrieve()
                .bodyToFlux(responseType);
    }

    /**
     * 工具类post请求
     *
     * @param path         请求路径
     * @param body         请求体数据
     * @param responseType 需要序列返回类型
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> post(String path, Object body, Class<T> responseType) {
        return this.post(path, body, responseType, new Object[1]);
    }

    /**
     * 工具类post请求
     *
     * @param path         请求路径
     * @param body         请求体数据
     * @param responseType 需要序列返回类型
     * @param uriVariables 路径里参数序列化
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> post(String path, Object body, Class<T> responseType, Object... uriVariables) {
        return this.webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(path).build(uriVariables))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType);
    }

    /**
     * 工具类PUT请求
     *
     * @param path         请求路径
     * @param body         请求参数体
     * @param responseType 需要序列返回类型
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> put(String path, Object body, Class<T> responseType) {
        return this.put(path, body, responseType, new Object[1]);
    }

    /**
     * 工具类PUT请求
     *
     * @param path         请求路径
     * @param body         请求参数体
     * @param responseType 需要序列返回类型
     * @param uriVariables 路径里参数序列化
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> put(String path, Object body, Class<T> responseType, Object... uriVariables) {
        return this.webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(path).build(uriVariables))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType);
    }

    /**
     * 工具类DELETE请求
     *
     * @param path         请求路径
     * @param queryParams  查询参数
     * @param responseType 需要序列返回类型
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> delete(String path, Map<String, String> queryParams, Class<T> responseType) {
        return this.delete(path, queryParams, responseType, new Object[1]);
    }

    /**
     * 工具类DELETE请求
     *
     * @param path         请求路径
     * @param queryParams  查询参数
     * @param responseType 需要序列返回类型
     * @param uriVariables 路径里参数序列化
     * @param <T>          数据类型定义
     * @return 结果返回
     */
    public <T> Mono<T> delete(String path, Map<String, String> queryParams, Class<T> responseType, Object... uriVariables) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        queryParams.forEach(multiValueMap::add);
        return this.webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(multiValueMap).build(uriVariables))
                .retrieve()
                .bodyToMono(responseType);
    }

    /**
     * 内部Client异常错误过滤器 {@link ClientRequestException} 自定义请求错误信息
     */
    @Log4j2
    static class ExceptionFilterFunction implements ExchangeFilterFunction {

        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
            URI originalUrl = request.url();
            String serviceId = originalUrl.getHost();
            return next.exchange(request).flatMap(response -> {
                if (response.statusCode().isError()) {
                    if (response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        return response.bodyToMono(ErrorResponse.class).flatMap(
                                ex -> Mono.defer(() -> Mono.error(ClientRequestException
                                        .withMsg(ex.getMessage()).serviceId(serviceId))));
                    }
                    return response.createException().flatMap(
                            ex -> Mono.defer(() -> Mono.error(ClientRequestException
                                    .withMsg(ex.getMessage()).serviceId(serviceId))));
                } else {
                    return Mono.defer(() -> Mono.just(response));
                }
            });
        }
    }

    /**
     * 内部client安全认证设置 {@link CustomServerAuthenticationEntryPoint} 设置会话seesion 向后传播
     */
    @Log4j2
    static class SecuritySessionHandlerFilterFunction implements ExchangeFilterFunction {

        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
            return Mono.deferContextual(contextView -> {
                Optional<ServerWebExchange> exchangeOpt = contextView.getOrEmpty(ServerWebExchange.class);
                if (exchangeOpt.isPresent()) {
                    ServerWebExchange exchange = exchangeOpt.get();
                    HttpHeaders oldHttpHeaders = exchange.getRequest().getHeaders();
                    ClientRequest clientRequest = ClientRequest.from(request)
                            .headers(httpHeaders -> {
                                httpHeaders.add("X-Requested-With", "XMLHttpRequest");
                                if (oldHttpHeaders.containsKey("X-Auth-Token")) {
                                    httpHeaders.add("X-Auth-Token",
                                            Objects.requireNonNull(oldHttpHeaders.get("X-Auth-Token"))
                                                    .get(0));
                                }
                                if (oldHttpHeaders.containsKey("login-system")) {
                                    httpHeaders.add("login-system",
                                            Objects.requireNonNull(oldHttpHeaders.get("login-system"))
                                                    .get(0));
                                }
                            })
                            .attributes(stringObjectMap -> stringObjectMap.putAll(exchange.getAttributes()))
                            .build();
                    return next.exchange(clientRequest);
                }
                return next.exchange(request);
            });
        }
    }
}