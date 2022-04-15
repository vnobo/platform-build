package com.platform.system.core.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * com.bootiful.system.core.news.NewsOnly
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/28
 */
@Schema(title = "新闻读取")
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsOnly extends News {

    public String getAuditorName() {
        return this.getAuditor().getName();
    }

    public String getCreatorName() {
        return this.getCreator().getName();
    }

    public String getUpdaterName() {
        return this.getUpdater().getName();
    }

    public static NewsOnly withNews(News news) {
        NewsOnly newsOnly = new NewsOnly();
        BeanUtils.copyProperties(news, newsOnly);
        return newsOnly;
    }
}