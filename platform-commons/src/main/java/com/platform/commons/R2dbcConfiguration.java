package com.platform.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.ReactiveUserAuditorAware;
import com.platform.commons.annotation.UserAuditor;
import com.platform.commons.converters.*;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * com.bootiful.commons.AutoR2dbcConfiguration
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/2
 */
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableR2dbcAuditing
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

  protected ObjectMapper objectMapper;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveAuditorAware<UserAuditor> creatorsAuditorProvider() {
    return new ReactiveUserAuditorAware();
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return ConnectionFactories.get("r2dbc:..");
  }

  @Override
  public List<Object> getCustomConverters() {
    List<Object> converters = new ArrayList<>();
    converters.add(new SetReadConverter(this.objectMapper));
    converters.add(new SetWriteConverter(this.objectMapper));
    converters.add(new JsonNodeReadConverter(this.objectMapper));
    converters.add(new JsonNodeWriteConverter(this.objectMapper));
    converters.add(new UserAuditorReadConverter());
    converters.add(new UserAuditorWriteConverter());
    converters.add(new SystemReadConverter());
    converters.add(new SystemWriteConverter());
    return converters;
  }
}