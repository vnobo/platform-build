package com.platform.gateway.client;

import com.platform.commons.client.AbstractClient;
import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.gateway.security.RegisterRequest;
import com.platform.gateway.security.TenantCutRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * com.bootiful.gateway.client.OauthClient
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/22
 */
@Service
public class AuthClient extends AbstractClient {

  private static final String OAUTH_SERVICE_BASE_URL = "http://bootiful-oauth";

  /**
   * 使用父类 {@link AbstractClient#webClient} 派生继承,继承全局客户端配置 必须实现 {@link
   * InitializingBean#afterPropertiesSet()} 初始化BaseUrl
   */
  @Override
  public void afterPropertiesSet() {
    this.initializeBaseUrl(OAUTH_SERVICE_BASE_URL);
  }

  /**
   * 注册用户
   *
   * @param registerRequest 注册信息
   * @return 安全用户信息
   */
  public Mono<LoginSecurityDetails> userRegister(RegisterRequest registerRequest) {
    return super.post("/oauth2/v1/register", registerRequest, LoginSecurityDetails.class, new Object[1]);
  }

  /**
   * 修改用户密码
   *
   * @param username 需要修改的用户
   * @param password 新的密码
   * @return 安全用户信息
   */
  public Mono<LoginSecurityDetails> changePassword(String username, String password) {
    return super.put(
        "/user/manager/v1/change/password",
        Map.of("username", username, "password", password, "newPassword", password),
        LoginSecurityDetails.class,
        new Object[1]);
  }

  /**
   * 用户租户切换
   *
   * @param cutRequest 切换信息
   * @return 用户安全认证信息
   */
  public Mono<SimplerSecurityDetails> tenantCut(TenantCutRequest cutRequest) {
    return super.post(
        "/oauth2/v1/tenant/cut", cutRequest, SimplerSecurityDetails.class, new Object[1]);
  }
}