package com.bootiful.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * com.bootiful.oauth.core.AutoToolsUtil
 * 自动封装工具基础类
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
public abstract class BaseAutoToolsUtil {

    protected ObjectMapper objectMapper;
    protected R2dbcEntityTemplate entityTemplate;
    protected MappingR2dbcConverter mappingR2dbcConverter;
    protected ReactiveRedisTemplate<String, Object> redisTemplate;
    protected ReactiveStringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired(required = false)
    public void setEntityTemplate(R2dbcEntityTemplate entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    @Autowired(required = false)
    public void setMappingR2dbcConverter(MappingR2dbcConverter mappingR2dbcConverter) {
        this.mappingR2dbcConverter = mappingR2dbcConverter;
    }

    @Autowired(required = false)
    public void setRedisTemplate(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired(required = false)
    public void setStringRedisTemplate(ReactiveStringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
}