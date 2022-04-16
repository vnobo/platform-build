package com.platform.system.core.files.minio;

import com.platform.commons.annotation.RestServerException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PostPolicy;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.system.core.files.minio.MinioFilesManager sss
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/21
 */
@Log4j2
@Service
public class MinioFilesManager {

  protected final MinioClient minioClient;
  protected final com.platform.system.core.files.minio.MinioProperties minioProperties;

  private String bucketName = "countryside";

  public MinioFilesManager(MinioProperties minioProperties) {
    this.minioProperties = minioProperties;
    this.minioClient =
        MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
    if (!ObjectUtils.isEmpty(this.minioProperties.getBucketName())) {
      this.bucketName = this.minioProperties.getBucketName();
    }
    try {
      // Make 'asiatrip' bucket if not exist.
      boolean found =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        // Make a new bucket called 'asiatrip'.
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      } else {
        log.debug("Bucket {} already exists.", bucketName);
      }
    } catch (ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | IOException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      log.error("Init minio bucket error, 消息: {}", e.getMessage());
    }
  }

  /**
   * 上传文件到 minio
   *
   * @param fileName 文件名称包括路径,/path/filename
   * @param inputStream 文件输入流
   * @param objectSize 文件大小
   * @param partSize 分片上传大小
   * @return 上传 返回结果
   */
  public ObjectWriteResponse putObject(
      String fileName, InputStream inputStream, long objectSize, long partSize) {
    PutObjectArgs putObjectArgs =
        PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                inputStream, objectSize, partSize)
            .build();
    try {
      return this.minioClient.putObject(putObjectArgs);
    } catch (ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | IOException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      log.error("上传文件到minio错误,异常: {}", e.getMessage());
      throw RestServerException.withMsg(1500, e.getMessage());
    }
  }

  /**
   * 获取上传文件凭证
   *
   * @param objectName 上传文件路径和文件名
   * @return 返回上传凭证参数
   */
  public Map<String, String> formDataUrl(String objectName) {
    try {
      // Get presigned URL string to upload 'my-objectname' in 'my-bucketname'
      // with response-content-type as application/json and life time as one day.
      PostPolicy policy = new PostPolicy(bucketName, ZonedDateTime.now().plusHours(1));

      // Add condition that 'key' (object name) equals to 'my-objectname'.
      policy.addEqualsCondition("key", objectName);

      // Add condition that 'Content-Type' starts with 'image/'.
      // policy.addStartsWithCondition("Content-Type", "image/");

      // Add condition that 'content-length-range' is between 64kiB to 10MiB.
      policy.addContentLengthRangeCondition(2 * 1024, 1024 * 1024 * 1024);

      return minioClient.getPresignedPostFormData(policy);
    } catch (ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | IOException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      log.error("上传文件错误,错误信息: {}", e.getMessage());
      throw RestServerException.withMsg(1201, e.getMessage());
    }
  }

  public Mono<Map<String, Object>> getFormDataUrl(UploadUrlRequest uploadUrlRequest) {
    String objectName = uploadUrlRequest.getModule() + "/" + uploadUrlRequest.getFileName();
    Map<String, String> formData = formDataUrl(objectName);
    return Mono.defer(
        () ->
            Mono.just(
                Map.of(
                    "url",
                    minioProperties.getEndpoint() + "/" + bucketName,
                    "fileName",
                    objectName,
                    "formData",
                    formData,
                    "getUrl",
                    minioProperties.getEndpoint() + "/" + bucketName + "/" + objectName)));
  }

  public Flux<DeleteError> removes(List<String> fileNames) {
    return Flux.fromStream(fileNames.parallelStream())
        .map(DeleteObject::new)
        .collectList()
        .map(objects -> RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build())
        .map(minioClient::removeObjects)
        .flatMapMany(Flux::fromIterable)
        .map(
            result -> {
              try {
                return result.get();
              } catch (ErrorResponseException
                  | InsufficientDataException
                  | InternalException
                  | InvalidKeyException
                  | InvalidResponseException
                  | IOException
                  | NoSuchAlgorithmException
                  | ServerException
                  | XmlParserException e) {
                log.error("删除文件失败,失败原因!" + e.getMessage());
                return new DeleteError();
              }
            });
  }
}