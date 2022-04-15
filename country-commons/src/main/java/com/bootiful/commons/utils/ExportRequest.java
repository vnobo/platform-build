package com.bootiful.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * com.jinrun.storage.export.ExportWordParams
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/3/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequest implements Serializable {

    @NotBlank(message = "模板名字[name]不能为空!")
    private String name;

    @NotNull(message = "租户[tenantId]不能为空!")
    private Integer tenantId;

    @NotBlank(message = "导出文档标题[title]不能为空!")
    private String title;

    @NotNull(message = "模板数据不能为空!")
    private Map<String, Object> params;

    public ExportRequest params(Map<String, Object> params) {
        this.setParams(params);
        return this;
    }
}