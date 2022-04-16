package com.platform.system.core.files.minio;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;

/**
 * com.bootiful.system.core.files.minio.UploadFileRequest
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/21
 */
@Data
@Validated
public class UploadFileRequest {

  @NotBlank(message = "文件目录[module]不能为空!")
  private String module;

  @NotBlank(message = "文件名[name]不能为空!")
  private String name;

  @NotNull(message = "文件[fileData]不能为空!")
  private FilePart fileData;
}