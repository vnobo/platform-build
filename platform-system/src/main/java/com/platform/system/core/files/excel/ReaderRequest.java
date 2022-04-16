package com.platform.system.core.files.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * com.bootiful.system.core.files.excel.ReaderRequest
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/17
 */
@Data
@Schema(title = "导入Excel请求")
public class ReaderRequest implements Serializable {

  @Schema(title = "导入租户")
  @NotNull(message = "导入租户Code[tenantCode]不能为空!")
  private String tenantCode;

  @Schema(title = "导入文件保存路径前缀")
  @NotBlank(message = "导入路径前缀[prefix]不能为空!")
  private String prefix;

  @Schema(title = "导入文件名称")
  @NotBlank(message = "导入文件名称[name]不能为空!")
  private String name;
}