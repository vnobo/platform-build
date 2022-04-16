package com.platform.system.core.files.minio;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * com.bootiful.system.core.files.minio.UploadFileRequest 获取文件上传路径
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/21
 */
@Data
@Validated
public class UploadUrlRequest {

  @NotNull(message = "文件模块类型[module]不能为空!")
  private String module;

  @NotBlank(message = "文件名[fileName]不能为空!")
  private String fileName;
}