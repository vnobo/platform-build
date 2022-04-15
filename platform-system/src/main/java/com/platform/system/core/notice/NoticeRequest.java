package com.platform.system.core.notice;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2021/10/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "通知公告请求")
public class NoticeRequest extends Notice {

    @NotBlank(message = "系统类型[system]不可为空!")
    @Schema(title = "系统类型")
    private String system;

    @NotBlank(message = "标题[title]不可为空!")
    @Schema(title = "标题")
    private String title;

    @NotBlank(message = "内容[content]不可为空!")
    @Schema(title = "内容")
    private String content;

    public Notice toNotice() {
        Notice notice = new Notice();
        BeanUtils.copyProperties(this, notice);
        return notice;
    }

}