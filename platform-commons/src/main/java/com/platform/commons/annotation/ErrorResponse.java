package com.platform.commons.annotation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ErrorResponse
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2020/7/22
 */
@Data
@NoArgsConstructor
public class ErrorResponse implements Serializable {

    private String requestId;

    private LocalDateTime time;

    private String message;

    private Integer code;

    private Object errors;

    @Builder
    public ErrorResponse(String message, Object errors) {
        this.message = message;
        this.errors = errors;
        this.requestId = UUID.randomUUID().toString();
        this.time = LocalDateTime.now();
    }

    public static ErrorResponse withDefault(Object errors) {
        return new ErrorResponse("服务器运行时错误!", errors);
    }

    public static ErrorResponse withErrors(String message, Object errors) {
        ErrorResponse response = withDefault(errors);
        response.setMessage(message);
        return response;
    }

    public ErrorResponse code(int status) {
        this.code = status;
        return this;
    }
}