package com.bootiful.system.core.feedback;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author jjh
 * @date 2021/9/30 create
 */
@Tag(name = "意见反馈")
@RequiredArgsConstructor
@RestController
@RequestMapping("/feedback/manager/v1")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @GetMapping("page")
    public Mono<Page<FeedbackOnly>> page(FeedbackSearch search, Pageable pageable) {
        return this.feedbackService.page(search, pageable);
    }

    @PostMapping
    public Mono<Feedback> operation(@RequestBody FeedbackRequest request) {
        return this.feedbackService.operation(request);
    }

    @PutMapping("examine/{id}")
    public Mono<Feedback> examine(@PathVariable Long id, @RequestBody FeedbackRequest request) {
        return this.feedbackService.examine(request.whitId(id));
    }


}