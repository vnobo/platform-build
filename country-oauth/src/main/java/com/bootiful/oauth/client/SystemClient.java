package com.bootiful.oauth.client;

import com.bootiful.commons.client.AbstractClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * com.bootiful.gateway.client.SystemClient
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/4/8
 */
@Service
public class SystemClient extends AbstractClient {

    private final static String HTTP_SYSTEM_SERVICE_BASE_URL = "http://bootiful-system";

    /**
     * 使用父类 {@link AbstractClient#webClient}
     * 派生继承,继承全局客户端配置
     * 必须实现 {@link InitializingBean#afterPropertiesSet()} 初始化BaseUrl
     */
    @Override
    public void afterPropertiesSet() {
        this.initializeBaseUrl(HTTP_SYSTEM_SERVICE_BASE_URL);
    }

    public Flux<JsonNode> authorities(String system) {
        return super.get("/authority/dict/v1/search", Map.of("system", system), JsonNode.class);
    }


    /**
     * 获取村庄信息
     *
     * @param streetCode 镇租户编码
     * @return 返回村庄信息
     */
    public Flux<JsonNode> villages(String streetCode) {
        return super.get("/villages/v1/", Map.of("streetCode", streetCode), JsonNode.class);
    }
}