package com.platform.system.core.configurations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * com.bootiful.system.core.configurations.ConfigurationRequest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Schema(title = "租户配置批量修改请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class BatchConfigurationRequest extends ConfigurationRequest implements Serializable {

    @NotNull(message = "是否,包含[isBatch]不能为空!")
    private Boolean isBatch;

    public ConfigurationRequest toRequest() {
        ConfigurationRequest request = new ConfigurationRequest();
        BeanUtils.copyProperties(this, request);
        return request;
    }

}