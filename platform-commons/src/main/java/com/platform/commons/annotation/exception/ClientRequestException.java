package com.platform.commons.annotation.exception;

import com.platform.commons.annotation.RestServerException;

/**
 * com.bootiful.commons.annotation.exception.ClientRequestException
 *
 * <p>内部Client统一错误出里结果
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/4/7
 */
public class ClientRequestException extends RestServerException {

    private String serviceId;

    public ClientRequestException(int code, Object msg) {
        super(code, msg);
    }

    public static ClientRequestException withMsg(int code, Object msg) {
        return new ClientRequestException(code, msg);
    }

    public static ClientRequestException withMsg(Object msg) {
        return withMsg(1503, msg);
    }

    public ClientRequestException serviceId(String serviceId) {
        this.setServiceId(serviceId);
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}