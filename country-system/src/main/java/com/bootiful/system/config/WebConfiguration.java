package com.bootiful.system.config;

import com.bootiful.commons.CommonsAutoConfiguration;
import com.bootiful.system.core.files.export.ExportProperties;
import com.bootiful.system.core.files.minio.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({MinioProperties.class, ExportProperties.class})
public class WebConfiguration {
}