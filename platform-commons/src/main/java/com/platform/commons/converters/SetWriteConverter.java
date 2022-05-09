package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.exception.RestJsonProcessingException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * com.mspbots.web.security.converter.UserWriteConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/17
 */
@WritingConverter
public record SetWriteConverter(ObjectMapper objectMapper) implements Converter<Set<Object>, Json> {

    @Override
    public Json convert(@NonNull Set<Object> source) {
        if (ObjectUtils.isEmpty(source)) {
            return Json.of("[]");
        }
        try {
            return Json.of(this.objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            throw RestJsonProcessingException.withError(e);
        }
    }
}