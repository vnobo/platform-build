package com.platform.commons.utils;

import com.platform.commons.annotation.RestServerException;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * com.bootiful.commons.utils.AuthoritySystem
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/9/14
 */
@Schema(title = "系统类型")
public enum SystemType {

    /**
     * 数字乡村,防返贫,积分系统
     */
    country,
    poverty,
    points,
    grid,
    homestead,
    villages,
    toilets;

    private static final SystemType[] VALUES;

    static {
        VALUES = values();
    }

    public static SystemType ofValue(String statusCode) {
        SystemType status = resolve(statusCode);
        if (status == null) {
            throw RestServerException.withMsg(1504, "No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    @Nullable
    public static SystemType resolve(String statusCode) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (SystemType status : VALUES) {
            if (status.name().equals(statusCode)) {
                return status;
            }
        }
        return null;
    }

}