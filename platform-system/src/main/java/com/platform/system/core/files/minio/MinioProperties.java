package com.platform.system.core.files.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

/**
 * com.bootiful.system.core.files.minio.MinioPers
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/6/21
 */
@Data
@ConfigurationProperties("minio")
public class MinioProperties {

    @NotEmpty
    private String endpoint;

    @NotEmpty
    private String accessKey;

    @NotEmpty
    private String secretKey;

    private String bucketName;

}