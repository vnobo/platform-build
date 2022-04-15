package com.bootiful.gateway.weixin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * com.bootiful.oauth.core.weixin.WxMaProperties
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/28
 */
@Data
@ConfigurationProperties(prefix = "wx.mini-app")
public class WxMaProperties {

    private String httpProxyHost;
    private Integer httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;

    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appid;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;

    }

}