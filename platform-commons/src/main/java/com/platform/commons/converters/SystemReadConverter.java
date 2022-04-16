package com.platform.commons.converters;

import com.platform.commons.utils.SystemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

/**
 * com.bootiful.commons.converters.AuthoritySystemConverter
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/20
 */
@ReadingConverter
public class SystemReadConverter implements Converter<String, SystemType> {
  @Override
  public SystemType convert(@NonNull String source) {
    return SystemType.ofValue(source);
  }
}