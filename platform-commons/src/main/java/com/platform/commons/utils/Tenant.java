package com.platform.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * com.bootiful.commons.utils.Tenant
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant implements Serializable {

  private Integer id;
  private String code;
  private String name;
  private String address;
  private String description;
  private Integer pid;
  private JsonNode extend;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;

  private TenantLevel level;

  public TenantLevel getLevel() {
    return StringUtils.hasLength(code) ? TenantLevel.valueOf(code.length()) : null;
  }

  public Integer getTier() {
    return ObjectUtils.isEmpty(this.extend) ? 0 : this.extend.withArray("addressCode").size();
  }

  public MultiValueMap<String, String> toQueryParams() {

    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(10);

    if (!ObjectUtils.isEmpty(id)) {
      multiValueMap.set("id", String.valueOf(id));
    }

    if (!ObjectUtils.isEmpty(pid)) {
      multiValueMap.set("id", String.valueOf(pid));
    }

    if (!ObjectUtils.isEmpty(level)) {
      multiValueMap.set("level", level.name());
    }

    if (StringUtils.hasLength(name)) {
      multiValueMap.set("name", name);
    }
    if (StringUtils.hasLength(code)) {
      multiValueMap.set("code", code);
    }

    if (StringUtils.hasLength(address)) {
      multiValueMap.set("code", address);
    }

    return multiValueMap;
  }
}