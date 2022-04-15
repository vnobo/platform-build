package com.platform.system.core.problem;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2022/3/17
 */
@Service
@RequiredArgsConstructor
public class ProblemService extends BaseAutoToolsUtil {
    private final ProblemRepository problemRepository;

    public Mono<Page<Problem>> page(ProblemRequest request, Pageable pageable) {
        return super.entityTemplate.select(Query.query(request.toCriteria()).with(pageable), Problem.class)
                .collectList()
                .zipWith(super.entityTemplate.count(Query.query(request.toCriteria()), Problem.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<Problem> search(ProblemRequest request) {
        return super.entityTemplate.select(Query.query(request.toCriteria()), Problem.class);
    }

    public Mono<Problem> operation(ProblemRequest request) {
        return this.save(request.toProblem());
    }

    public Flux<Void> deleteBatch(List<Problem> problems) {
        return Flux.fromStream(problems.parallelStream()).flatMap(this.problemRepository::delete);
    }

    private Mono<Problem> save(Problem problem) {
        if (problem.isNew()) {
            return this.problemRepository.save(problem);
        } else {
            assert !ObjectUtils.isEmpty(problem.getId());
            return this.problemRepository.findById(problem.getId()).flatMap(old -> {
                problem.setCreatedTime(old.getCreatedTime());
                return this.problemRepository.save(problem);
            });
        }
    }
}