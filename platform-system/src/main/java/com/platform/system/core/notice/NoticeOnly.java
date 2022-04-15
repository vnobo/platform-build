package com.platform.system.core.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * com.bootiful.system.core.notice.NoticeOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/28
 */
@Schema(title = "通知公告读取")
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeOnly extends Notice {

    public String getCreatorName() {
        return this.getCreator().getName();
    }

    public String getUpdaterName() {
        return this.getUpdater().getName();
    }

    public static NoticeOnly withNotice(Notice news) {
        NoticeOnly newsOnly = new NoticeOnly();
        BeanUtils.copyProperties(news, newsOnly);
        return newsOnly;
    }
}