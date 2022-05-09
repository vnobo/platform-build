package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.exception.RestJsonProcessingException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * com.alex.web.converters.SetReadConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/28
 */
@ReadingConverter
public record SetReadConverter(ObjectMapper objectMapper) implements Converter<Json, Set<Object>> {

    @Override
    public Set<Object> convert(@NonNull Json source) {
        if (ObjectUtils.isEmpty(source)) {
            return Set.of();
        }
        try {
            return this.objectMapper.readValue(source.asString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw RestJsonProcessingException.withMsg(e);
        }
    }
}