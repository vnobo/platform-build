package com.platform.system.core.feedback;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
public interface FeedbackRepository extends R2dbcRepository<Feedback, Long> {
}