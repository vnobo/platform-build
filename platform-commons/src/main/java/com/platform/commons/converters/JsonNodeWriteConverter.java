package com.platform.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.commons.annotation.exception.RestJsonProcessingException;
import io.r2dbc.postgresql.codec.Json;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * com.mspbots.web.security.converter.UserWriteConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/12/17
 */
@WritingConverter
@Log4j2
@RequiredArgsConstructor
public class JsonNodeWriteConverter implements Converter<JsonNode, Json> {

  private final ObjectMapper objectMapper;

  @Override
  public Json convert(@NonNull JsonNode source) {
    try {
      return Json.of(this.objectMapper.writeValueAsString(source));
    } catch (JsonProcessingException e) {
      log.error("写入 Set 为 Json 转换错误,信息: {}", e.getMessage());
      throw RestJsonProcessingException.withMsg("序列化数据Set为Json类型错误,信息: " + e.getMessage());
    }
  }
}