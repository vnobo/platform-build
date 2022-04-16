package com.platform.commons.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * com.bootiful.commons.utils.ExcelReaderResult
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/17
 */
@Data
public class ExcelReaderResult implements Serializable {
  private List<String> header;
  private List<Map<String, Object>> data;

  public static ExcelReaderResult withHeader(List<String> header) {
    ExcelReaderResult readerResult = new ExcelReaderResult();
    readerResult.setHeader(header);
    return readerResult;
  }

  public ExcelReaderResult data(List<Map<String, Object>> data) {
    this.setData(data);
    return this;
  }
}