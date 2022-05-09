package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.RestServerException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * com.alex.web.converters.SetReadConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/28
 */
@ReadingConverter
public record JsonNodeReadConverter(ObjectMapper objectMapper) implements Converter<Json, JsonNode> {

    @Override
    public JsonNode convert(@NonNull Json source) {
        try {
            return this.objectMapper.readValue(source.asString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw RestServerException.withMsg(e);
        }
    }
}