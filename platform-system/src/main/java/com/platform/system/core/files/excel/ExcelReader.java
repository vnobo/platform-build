package com.platform.system.core.files.excel;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.platform.commons.annotation.RestServerException;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.commons.utils.ExcelReaderResult;
import com.platform.system.core.files.minio.MinioFilesManager;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * com.bootiful.system.core.files.excel.ExcelReader
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/17
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ExcelReader extends BaseAutoToolsUtil {

  private final MinioFilesManager minioFilesManager;

  public Flux<ExcelReaderResult> reader(ReaderRequest request, FilePart filePart) {
    String suffixName = FileNameUtil.getSuffix(filePart.filename());
    if (!"xls".equals(suffixName)) {
      return Flux.error(RestServerException.withMsg(1500, "文件格式不正确!必须是:[xls]"));
    }
    String fileName =
        "/upload/excel/"
            + request.getPrefix()
            + "/"
            + request.getTenantCode()
            + "/"
            + request.getName();
    if (ObjectUtils.isEmpty(filePart)) {
      throw RestServerException.withMsg(1500, "上传的文件[file]不能为空!");
    }
    return filePart
        .content()
        .doOnNext(
            dataBuffer -> {
              DataBuffer data =
                  DefaultDataBufferFactory.sharedInstance.wrap(dataBuffer.asByteBuffer());
              backMinio(fileName, data.asInputStream(), dataBuffer.readableByteCount());
            })
        .flatMap(
            data -> {
              List<Map<String, Object>> nodeList = new ArrayList<>();
              AtomicReference<List<String>> headerList = new AtomicReference<>();
              ExcelUtil.readBySax(
                  data.asInputStream(),
                  0,
                  (sheetIndex, rowIndex, rowList) -> {
                    log.debug(
                        "读取表格行,当前Sheet序号: {},当前行号: {},行数据: {}", sheetIndex, rowIndex, rowList);
                    if (rowIndex == 0) {
                      headerList.set(
                          rowList.stream().map(String::valueOf).collect(Collectors.toList()));
                      return;
                    }
                    Map<String, Object> rowMap = new HashMap<>(10);
                    for (int x = 0; x < headerList.get().size(); x++) {
                      if (x < rowList.size()) {
                        rowMap.put(String.valueOf(x + 1), rowList.get(x));
                      } else {
                        rowMap.put(String.valueOf(x + 1), "");
                      }
                    }
                    nodeList.add(rowMap);
                  });
              return Mono.just(ExcelReaderResult.withHeader(headerList.get()).data(nodeList));
            });
  }

  private void backMinio(String fileName, InputStream inputStream, long size) {
    try {
      ObjectWriteResponse response =
          minioFilesManager.putObject(fileName, inputStream, size, 1024 * 1024 * 5);
      log.debug(
          "上传文件成功, version: {},etag: {},object: {}",
          response.versionId(),
          response.etag(),
          response.object());
    } catch (RestServerException e) {
      log.warn("文件上传服务错误,请检查Minio是否正常: {}", e.getMsg());
    }
  }
}