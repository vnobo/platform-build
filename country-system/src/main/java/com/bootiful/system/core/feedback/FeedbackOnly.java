package com.bootiful.system.core.feedback;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
@Data
@Schema(title = "意见反馈读取")
@EqualsAndHashCode(callSuper = true)
public class FeedbackOnly extends Feedback {

    @Schema(title = "用户姓名")
    private final String name;

    @Schema(title = "答复姓名")
    private final String replyName;


}