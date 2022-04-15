package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.RestServerException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * com.alex.web.converters.SetReadConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/28
 */
@Log4j2
@ReadingConverter
@RequiredArgsConstructor
public class JsonNodeReadConverter implements Converter<Json, JsonNode> {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode convert(@NonNull Json source) {
        try {
            return this.objectMapper.readValue(source.asString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("读取 Json 为 Set 转换错误,信息: {}", e.getMessage());
            throw RestServerException
                    .withMsg(1101, "序列化数据Json为Set类型错误,信息: " + e.getMessage());
        }
    }
}