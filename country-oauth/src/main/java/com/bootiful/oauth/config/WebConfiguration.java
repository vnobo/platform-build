package com.bootiful.oauth.config;

import com.bootiful.commons.CommonsAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * coupons in com.alex.web.config
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2019/7/14
 */
@Configuration(proxyBeanMethods = false)
@Import({CommonsAutoConfiguration.class})
public class WebConfiguration {

}