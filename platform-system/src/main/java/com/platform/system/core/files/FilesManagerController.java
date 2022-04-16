package com.platform.system.core.files;

import com.platform.system.core.files.minio.MinioFilesManager;
import com.platform.system.core.files.minio.UploadUrlRequest;
import io.minio.messages.DeleteError;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.system.core.files.FilesManagerController
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/22
 */
@Tag(name = "上传minio处理")
@RestController
@RequestMapping("files/manager/v1")
@RequiredArgsConstructor
public class FilesManagerController {

  private final MinioFilesManager minioFilesManager;

  @GetMapping(value = "presigned/post")
  public Mono<Map<String, Object>> presignedPost(@Valid UploadUrlRequest uploadUrlRequest) {
    return this.minioFilesManager.getFormDataUrl(uploadUrlRequest);
  }

  @PostMapping(value = "removes")
  public Flux<DeleteError> removes(@RequestBody List<String> fileNames) {
    return this.minioFilesManager.removes(fileNames);
  }
}