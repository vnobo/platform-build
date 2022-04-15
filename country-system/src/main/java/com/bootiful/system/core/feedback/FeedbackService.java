package com.bootiful.system.core.feedback;

import com.bootiful.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
@Service
@RequiredArgsConstructor
public class FeedbackService extends BaseAutoToolsUtil {

    private final FeedbackRepository feedbackRepository;

    public Mono<Page<FeedbackOnly>> page(FeedbackSearch search, Pageable pageable) {
        return super.entityTemplate.getDatabaseClient().sql(search.whereSql(pageable))
                .map((row, metadata) -> super.mappingR2dbcConverter.read(FeedbackOnly.class, row, metadata))
                .all().collectList().zipWith(entityTemplate.getDatabaseClient()
                        .sql(search.countSql().toString()).map((row, metadata) ->
                                super.mappingR2dbcConverter.read(Integer.class, row, metadata)).one())
                .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Feedback> operation(FeedbackRequest request) {
        return this.save(request.toFeedback());
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Feedback> examine(FeedbackRequest request) {
        assert request.getId() != null;
        return this.feedbackRepository.findById(request.getId())
                .flatMap(data -> {
                    data.setReplyId(request.getReplyId());
                    data.setReplyContent(request.getReplyContent());
                    data.setReplyTime(LocalDateTime.now());
                    return this.save(data);
                });
    }

    /**
     * 意见反馈保存修改
     *
     * @param feedback 接参
     * @return 返回意见反馈保存修改
     */
    private Mono<Feedback> save(Feedback feedback) {
        if (feedback.isNew()) {
            return this.feedbackRepository.save(feedback);
        } else {
            assert feedback.getId() != null;
            return this.feedbackRepository.findById(feedback.getId())
                    .flatMap(old -> {
                        feedback.setCreatedTime(old.getCreatedTime());
                        return this.feedbackRepository.save(feedback);
                    });
        }
    }
}