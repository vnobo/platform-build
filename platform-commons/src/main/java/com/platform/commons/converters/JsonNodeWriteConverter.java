package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.exception.RestJsonProcessingException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * com.mspbots.web.security.converter.UserWriteConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/17
 */
@WritingConverter
public record JsonNodeWriteConverter(ObjectMapper objectMapper) implements Converter<JsonNode, Json> {

    @Override
    public Json convert(@NonNull JsonNode source) {
        try {
            return Json.of(this.objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            throw RestJsonProcessingException.withMsg(e);
        }
    }
}