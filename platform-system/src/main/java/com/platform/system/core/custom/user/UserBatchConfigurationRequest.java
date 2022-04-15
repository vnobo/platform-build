package com.platform.system.core.custom.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.system.core.configurations.ConfigurationRequest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Schema(title = "用户自定义配置批量请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBatchConfigurationRequest extends UserConfigurationRequest implements Serializable {

    @NotNull(message = "是否,包含[isBatch]不能为空!")
    private Boolean isBatch;

}