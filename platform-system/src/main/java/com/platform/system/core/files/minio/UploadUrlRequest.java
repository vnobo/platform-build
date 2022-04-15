package com.platform.system.core.files.minio;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * com.bootiful.system.core.files.minio.UploadFileRequest
 * 获取文件上传路径
 *
 * @author Alex bob(https://github.com/vnobo)
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