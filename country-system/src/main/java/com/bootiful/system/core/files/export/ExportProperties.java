package com.bootiful.system.core.files.export;

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
@ConfigurationProperties("export")
public class ExportProperties {

    @NotEmpty(message = "导出模板路径不能为空!")
    private String templatePath = "https://static.jinqiruanjian.com/countryside/export-template/";


}