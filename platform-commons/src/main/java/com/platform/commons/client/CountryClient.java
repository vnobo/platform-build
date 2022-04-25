package com.platform.commons.client;

import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.commons.utils.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * com.bootiful.gateway.client.OauthClient 平台访问工具类 {@link CountryClient#systemClient} 访问平台工具服务
 * {@link CountryClient#oauthClient} 访问平台认证服务
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/22
 */
public class CountryClient extends AbstractClient {

    private static final String OAUTH_SERVICE_BASE_URL = "http://bootiful-oauth";
    private static final String SYSTEM_SERVICE_BASE_URL = "http://bootiful-system";

    private WebClient oauthClient;
    private WebClient systemClient;

    /**
     * 使用父类 {@link AbstractClient#webClient} 派生继承,继承全局客户端配置 必须实现 {@link
     * InitializingBean#afterPropertiesSet()} 初始化BaseUrl
     */
    @Override
    public void afterPropertiesSet() {
        this.oauthClient = super.webClient.mutate().baseUrl(OAUTH_SERVICE_BASE_URL).build();
        this.systemClient = super.webClient.mutate().baseUrl(SYSTEM_SERVICE_BASE_URL).build();
    }

    /**
     * 用户登录接口
     *
     * @param username 登录用户名
     * @return 登录用户信息
     */
    public Mono<LoginSecurityDetails> login(String username) {

        if (!StringUtils.hasLength(username)) {
            return Mono.empty();
        }

        return this.oauthClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/oauth2/v1/login/{username}").build(username))
                .retrieve()
                .bodyToMono(LoginSecurityDetails.class);
    }

    /**
     * 获取登录用户安全认证信息
     *
     * @param username 登录用户名
     * @return 登录用户信息
     */
    public Mono<SimplerSecurityDetails> loadSecurity(String username) {
        return this.oauthClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/oauth2/v1/load/security/{username}").build(username))
                .retrieve()
                .bodyToMono(SimplerSecurityDetails.class);
    }

    /**
     * 根据用户ID获取单个用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public Mono<User> loadUser(Long userId) {

        if (ObjectUtils.isEmpty(userId) || userId == 0) {
            return Mono.empty();
        }
        return this.loadUsers(User.builder().id(userId).build()).singleOrEmpty();
    }

    /**
     * 查询用户列表信息
     *
     * @param user 用户查询条件
     * @return 列表用户信息
     */
    public Flux<User> loadUsers(User user) {

        MultiValueMap<String, String> params = user.toQueryParams();
        Assert.notEmpty(user.toQueryParams(), "获取用户查询参数不能为空!");
        return this.oauthClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/user/manager/v1").queryParams(params).build())
                .retrieve()
                .bodyToFlux(User.class);
    }

    /**
     * 查询租户列表信息
     *
     * @param tenant 租户查询条件
     * @return 列表租户信息
     */
    public Flux<Tenant> loadTenant(Tenant tenant) {

        MultiValueMap<String, String> params = tenant.toQueryParams();
        Assert.notEmpty(params, "获取租户查询参数不能为空!");
        return this.oauthClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/tenant/manager/v1").queryParams(params).build())
                .retrieve()
                .bodyToFlux(Tenant.class);
    }

    /**
     * 查询租户配置信息
     *
     * @param configuration 租户配置查询条件
     * @return 列表租户配置
     */
    public Flux<Configuration> loadConfiguration(Configuration configuration) {

        MultiValueMap<String, String> params = configuration.toQueryParams();
        Assert.notEmpty(params, "获取配置查询参数不能为空!");
        return this.systemClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder.path("tenant/configuration/v1/search").queryParams(params).build())
                .retrieve()
                .bodyToFlux(Configuration.class);
    }

    /**
     * 读取Excel内容
     *
     * @param builder file body
     * @return 读取内容
     */
    public Flux<ExcelReaderResult> excelReader(MultipartBodyBuilder builder) {
        return this.systemClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/files/excel/reader/v1/sax").build())
                .bodyValue(builder.build())
                .retrieve()
                .bodyToFlux(ExcelReaderResult.class);
    }

    /**
     * 数据导出Excel文件公共方法
     *
     * @param request 导出查询参数
     * @return 文件
     */
    public Mono<ResponseEntity<Resource>> excelExport(ExportRequest request) {
        return this.systemClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/files/export/v1/excel").build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Resource.class)
                .map(resource -> ResponseEntity.ok().headers(httpHeaders ->
                                httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                + URLEncoder.encode(request.getTitle(), StandardCharsets.UTF_8) + "\"")
                        .body(resource));
    }

    /**
     * 数据导出Word文件公共方法
     *
     * @param request 导出参数
     * @return 导出文件
     */
    public Mono<ResponseEntity<Resource>> wordExport(ExportRequest request) {
        return this.systemClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/files/export/v1/word").build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Resource.class)
                .map(resource -> ResponseEntity.ok().headers(httpHeaders ->
                                httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                + URLEncoder.encode(request.getTitle(), StandardCharsets.UTF_8) + "\"")
                        .body(resource));
    }
}