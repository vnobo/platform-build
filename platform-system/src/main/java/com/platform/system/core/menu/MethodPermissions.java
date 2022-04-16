package com.platform.system.core.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * com.bootiful.oauth.core.authoritydict.MethodPermissions
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/2
 */
@Schema(title = "资源接口权限")
@Data
public class MethodPermissions implements Serializable {

  @NotNull(message = "接口请求方式[method]不能为空!")
  @Schema(title = "请求方式")
  private HttpMethod method;

  @NotBlank(message = "接口名称[name]不能为空!")
  @Schema(title = "接口名称")
  private String name;

  @Schema(title = "接口路径")
  @NotBlank(message = "接口路径[url]不能为空!")
  private String url;

  @Schema(title = "接口权限")
  @NotBlank(message = "接口权限[permission]不能为空!")
  private String permission;
}