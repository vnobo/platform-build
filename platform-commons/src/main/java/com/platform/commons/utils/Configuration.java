package com.platform.commons.utils;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * com.bootiful.commons.utils.Configuration
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/7
 */
@Schema(title = "租户配置类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration implements Serializable {

  private Long id;

  private Integer tenantId;

  private String tenantCode;

  private String name;

  private String type;

  private SystemType system;

  private JsonNode configuration;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  public static Configuration of(
      Integer tenantId, String tenantCode, String type, SystemType system) {
    return Configuration.builder()
        .tenantId(tenantId)
        .tenantCode(tenantCode)
        .type(type)
        .system(system)
        .build();
  }

  public MultiValueMap<String, String> toQueryParams() {
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(10);
    Map<String, Object> objectMap = BeanUtil.beanToMap(this, false, true);
    objectMap.forEach((k, v) -> multiValueMap.set(k, String.valueOf(v)));
    return multiValueMap;
  }
}