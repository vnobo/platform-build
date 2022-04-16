package com.platform.gateway.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.commons.client.AbstractClient;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.gateway.client.SystemClient
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/4/8
 */
@Service
public class SystemClient extends AbstractClient {

  private static final String HTTP_SYSTEM_SERVICE_BASE_URL = "http://bootiful-system";

  /**
   * 使用父类 {@link AbstractClient#webClient} 派生继承,继承全局客户端配置 必须实现 {@link
   * InitializingBean#afterPropertiesSet()} 初始化BaseUrl
   */
  @Override
  public void afterPropertiesSet() {
    this.initializeBaseUrl(HTTP_SYSTEM_SERVICE_BASE_URL);
  }

  /**
   * 短信发送接口
   *
   * @param phone 要发送的手机号码
   * @return 发送返回信息
   */
  public Mono<JsonNode> smsSend(String phone) {
    return super.get("/sms/manager/v1/send", Map.of("phone", phone), JsonNode.class, new Object[1])
        .singleOrEmpty();
  }

  /**
   * 短信认证接口
   *
   * @param body 认证数据信息
   * @return 是否验证成功
   */
  public Mono<Boolean> smsCheck(Object body) {
    return super.post("/sms/manager/v1/send", body, Boolean.class, new Object[1]);
  }
}