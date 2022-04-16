package com.platform.commons.annotation.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.platform.commons.annotation.RestServerException;

/**
 * com.bootiful.commons.annotation.exception.RestJsonProcessingException
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/23
 */
public class RestJsonProcessingException extends RestServerException {

  public RestJsonProcessingException(JsonProcessingException jsonProcessingException) {
    this(1101, jsonProcessingException);
  }

  public RestJsonProcessingException(int status, Object msg) {
    super(status, msg);
  }

  public static RestJsonProcessingException withError(
      JsonProcessingException jsonProcessingException) {
    return new RestJsonProcessingException(jsonProcessingException);
  }
}