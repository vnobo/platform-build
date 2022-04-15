package com.bootiful.system.core.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author jjh
 * @date 2021/9/13 create
 */
@Schema(title = "新闻请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsRequest extends News implements Serializable {

    @NotBlank(message = "系统类型[system]不能为空!")
    private String system;

    @NotBlank(message = "新闻标题[title]不能为空!")
    @Schema(title = "新闻标题")
    private String title;

    @NotBlank(message = "新闻类型[type]不能为空!")
    @Schema(title = "新闻类型")
    private String type;

    @NotBlank(message = "文章状态[status]不能为空!01新提交需要审核 02新提交不审核 03已审核 04审核不通过")
    @Schema(title = "文章状态-审核使用,01新提交需要审核 02新提交不审核 03已审核 04审核不通过")
    private String status;

    @NotBlank(message = "新闻内容[content]不能为空!")
    @Schema(title = "新闻内容")
    private String content;

    public News toNews() {
        News news = new News();
        BeanUtils.copyProperties(this, news);
        return news;
    }


}