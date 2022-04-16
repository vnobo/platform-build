package com.platform.gateway.config;

import com.platform.commons.CommonsAutoConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * coupons in com.alex.web.config
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/14
 */
@Configuration(proxyBeanMethods = false)
@Import({CommonsAutoConfiguration.class})
@RequiredArgsConstructor
public class WebConfiguration {}