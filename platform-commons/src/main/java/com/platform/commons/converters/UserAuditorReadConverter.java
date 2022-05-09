package com.platform.commons.converters;

import com.platform.commons.annotation.UserAuditor;
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
public class UserAuditorReadConverter implements Converter<Long, UserAuditor> {

    @Override
    public UserAuditor convert(@NonNull Long source) {
        return UserAuditor.withUserId(source);
    }
}