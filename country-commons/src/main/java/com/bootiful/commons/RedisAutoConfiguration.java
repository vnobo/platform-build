package com.bootiful.commons;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * com.bootiful.commons.AutoR2dbcConfiguration
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ReactiveRedisConnectionFactory.class})
@AutoConfigureAfter(RedisReactiveAutoConfiguration.class)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory,
                                                                       ResourceLoader resourceLoader) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
                resourceLoader.getClassLoader());
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, Object> context = builder.value(jdkSerializer)
                .hashKey(jdkSerializer).hashValue(jdkSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }


}