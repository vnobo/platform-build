package com.platform.commons.converters;

import com.platform.commons.annotation.UserAuditor;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * com.bootiful.commons.converters.CreatorsAuditorWriteConverter
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/24
 */
@WritingConverter
public class UserAuditorWriteConverter implements Converter<UserAuditor, Long> {

    @Override
    public Long convert(@NonNull UserAuditor source) {
        return source.getUserId();
    }
}