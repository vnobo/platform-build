package com.bootiful.system.core.problem;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2022/3/17
 */
public interface ProblemRepository extends R2dbcRepository<Problem, Long> {
}
