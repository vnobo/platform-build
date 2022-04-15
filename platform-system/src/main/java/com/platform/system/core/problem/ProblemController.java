package com.platform.system.core.problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2022/3/17
 */
@Tag(name = "常见问题")
@RestController
@RequestMapping("common/problem/v1")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("page")
    @Operation(summary = "分页查询")
    public Mono<Page<Problem>> page(ProblemRequest request, Pageable pageable) {
        return problemService.page(request, pageable);
    }

    @GetMapping("search")
    @Operation(summary = "查询")
    public Flux<Problem> search(ProblemRequest request) {
        return this.problemService.search(request);
    }

    @PostMapping
    @Operation(summary = "添加修改")
    public Mono<Problem> operation(@RequestBody ProblemRequest request) {
        return this.problemService.operation(request);
    }

    @DeleteMapping("batch")
    @Operation(summary = "批量删除")
    public Flux<Void> deleteBatch(@RequestBody List<Problem> problems) {
        return this.problemService.deleteBatch(problems);
    }

}