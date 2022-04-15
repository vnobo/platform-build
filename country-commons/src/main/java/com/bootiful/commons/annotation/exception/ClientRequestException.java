package com.bootiful.commons.annotation.exception;

import com.bootiful.commons.annotation.RestServerException;

/**
 * com.bootiful.commons.annotation.exception.ClientRequestException
 * <p>
 * 内部Client统一错误出里结果
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/4/7
 */
public class ClientRequestException extends RestServerException {

    public ClientRequestException(int code, Object msg) {
        super(code, msg);
    }

    public static ClientRequestException withMsg(int code, Object msg) {
        return new ClientRequestException(code, msg);
    }

    public static ClientRequestException withMsg(Object msg) {
        return withMsg(1503, msg);
    }

}