package com.platform.system.core.files.minio;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * com.bootiful.system.core.files.minio.MinioPers
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/21
 */
@Data
@ConfigurationProperties("minio")
public class MinioProperties {

  @NotEmpty private String endpoint;

  @NotEmpty private String accessKey;

  @NotEmpty private String secretKey;

  private String bucketName;
}