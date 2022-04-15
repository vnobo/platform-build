package com.platform.system.core.feedback;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "意见反馈请求")
public class FeedbackRequest extends Feedback {

    public Feedback toFeedback() {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(this, feedback);
        return feedback;
    }

    public FeedbackRequest whitId(Long id) {
        this.setId(id);
        return this;
    }
}