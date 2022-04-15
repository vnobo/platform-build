package com.platform.commons;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.platform.commons.annotation.GlobalExceptionHandler;
import com.platform.commons.client.CountryClient;
import com.platform.commons.filter.AfterSecurityFilter;
import com.platform.commons.filter.BeforeExchangeContextFilter;
import com.platform.commons.filter.BeforeSecurityFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import reactor.core.publisher.Flux;

import java.time.format.DateTimeFormatter;

/**
 * com.bootiful.commons.CommonsAutoConfiguration
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Flux.class, WebFluxConfigurer.class})
@Import({GlobalExceptionHandler.class, R2dbcAutoConfiguration.class
        , RabbitMqAutoConfiguration.class, CountryClient.class, RedisAutoConfiguration.class,
        SecurityAutoConfiguration.class, SessionAutoConfiguration.class})
public class CommonsAutoConfiguration implements WebFluxConfigurer {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @ConditionalOnMissingBean(CountryClient.class)
    public CountryClient countryClient() {
        return new CountryClient();
    }

    @Bean
    @ConditionalOnBean(SecurityWebFilterChain.class)
    public AfterSecurityFilter authGlobalFilter() {
        return new AfterSecurityFilter();
    }

    @Bean
    @ConditionalOnBean(SecurityWebFilterChain.class)
    public BeforeSecurityFilter beforeSecurityDetailsFilter(CountryClient oauthClient) {
        return new BeforeSecurityFilter(oauthClient);
    }

    @Bean
    @ConditionalOnMissingBean({AfterSecurityFilter.class, SecurityWebFilterChain.class})
    public BeforeExchangeContextFilter afterSecurityDetailsFilter() {
        return new BeforeExchangeContextFilter();
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ReactivePageableHandlerMethodArgumentResolver());
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DATE_TIME_FORMAT);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        };
    }


}