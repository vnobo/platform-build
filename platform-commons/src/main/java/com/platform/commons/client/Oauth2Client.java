package com.platform.commons.client;

import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.gateway.client.OauthClient 平台访问工具类 {@link Oauth2Client#systemClient} 访问平台工具服务
 * {@link Oauth2Client#oauthClient} 访问平台认证服务
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/22
 */
public class Oauth2Client extends AbstractClient {
    private static final String OAUTH_SERVICE_BASE_URL = "http://platform-oauth";

    private WebClient oauthClient;

    /**
     * 使用父类 {@link AbstractClient#webClient} 派生继承,继承全局客户端配置 必须实现 {@link
     * InitializingBean#afterPropertiesSet()} 初始化BaseUrl
     */
    @Override
    public void afterPropertiesSet() {
        this.oauthClient = super.webClient.mutate().baseUrl(OAUTH_SERVICE_BASE_URL).build();
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
        return this.oauthClient.get().uri(uriBuilder -> uriBuilder.path("/security/v1/login/{username}")
                        .build(username))
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
        return this.oauthClient.get()
                .uri(uriBuilder -> uriBuilder.path("/security/v1/load/security/{username}")
                        .build(username))
                .retrieve()
                .bodyToMono(SimplerSecurityDetails.class);
    }

}