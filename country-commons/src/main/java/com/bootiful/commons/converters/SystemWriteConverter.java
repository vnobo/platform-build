package com.bootiful.commons.converters;

import com.bootiful.commons.utils.SystemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * com.bootiful.commons.converters.AuthoritySystemConverter
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/20
 */
@WritingConverter
public class SystemWriteConverter implements Converter<SystemType, String> {

    @Override
    public String convert(SystemType source) {
        return source.name();
    }
}