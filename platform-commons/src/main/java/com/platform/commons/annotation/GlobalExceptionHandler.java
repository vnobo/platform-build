package com.platform.commons.annotation;

import com.platform.commons.annotation.exception.ClientRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler 全局错误信息拦截器,根据不同错误自定义错误返回定制信息
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2020/7/22
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleBindException(ServerWebInputException ex) {
        log.error("请求参数验证失败! 信息:{}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        if (ex instanceof WebExchangeBindException) {
            errors = ((WebExchangeBindException) ex).getBindingResult().getAllErrors().parallelStream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.withErrors("请求参数验证错误!", errors).code(1400));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleLockingFailureException(DataAccessException ex) {
        log.error("数据库操作失败! 信息:{}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        if (ex instanceof OptimisticLockingFailureException) {
            errors = List.of("修改数据不能重复操作! 错误: " + ex.getMessage());
        }

        if (ex instanceof DataIntegrityViolationException) {
            errors = List.of("数据已存在请检查后,重试! 错误: " + ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.withErrors("数据库操作失败!", errors).code(1600));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("数据空指针错误! 信息:{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.withErrors("数据空指针错误!", ex.getMessage()).code(1404));
    }

    @ExceptionHandler(ClientRequestException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientRequestException ex) {
        log.error("内部服务访问错误! 信息: {}", ex.getMsg());
        ErrorResponse response = ErrorResponse.withErrors("内部服务[" + ex.getServiceId() + "]访问异常!",
                ex.getMsg()).code(ex.getCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler(RestServerException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RestServerException ex) {
        log.error("服务器自定义错误. 信息: {}", ex.getMsg());
        ErrorResponse response = ErrorResponse.withErrors("自定义服务错误!", ex.getMsg()).code(ex.getCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}