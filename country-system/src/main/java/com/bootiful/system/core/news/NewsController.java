package com.bootiful.system.core.news;

import com.bootiful.commons.annotation.UserAuditor;
import com.bootiful.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jjh
 * @date 2021/6/15
 */
@Tag(name = "新闻管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/news/manager/v1")
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "新闻分页查询")
    @GetMapping("page")
    public Mono<Page<NewsOnly>> page(NewsSearch newsSearch, @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return this.newsService.page(newsSearch, pageable);
    }

    @Operation(summary = "新闻超级查询")
    @GetMapping("search")
    public Flux<NewsOnly> search(NewsSearch newsSearch, @PageableDefault(size = 999, sort = "id",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return this.newsService.search(newsSearch, pageable);
    }


    @Operation(summary = "添加新闻")
    @PostMapping
    public Mono<News> post(@Valid @RequestBody NewsRequest newsRequest) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(security -> {
            newsRequest.setTenantId(security.getTenantId());
            newsRequest.setTenantCode(security.getTenantCode());
            return this.newsService.operation(newsRequest);
        });
    }

    @Operation(summary = "根据新闻id删除新闻")
    @DeleteMapping
    public Mono<Void> deleteById(@RequestBody NewsRequest newsRequest) {
        return this.newsService.delete(newsRequest);
    }


    @Operation(summary = "根据新闻id删除新闻")
    @DeleteMapping("batch")
    public Mono<Void> deleteBatch(@RequestBody List<NewsRequest> newsRequests) {
        return this.newsService.deleteBatch(newsRequests);
    }

    @Operation(summary = "新闻审核")
    @PutMapping("audit")
    public Mono<News> audit(@RequestBody NewsRequest newsRequest) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(security -> {
            newsRequest.setAuditor(UserAuditor.withUserId(security.getUserId()));
            newsRequest.setAuditTime(LocalDateTime.now());
            return this.newsService.audit(newsRequest);
        });
    }

    @Operation(summary = "新闻审核")
    @PutMapping("audit/batch")
    public Flux<News> reviewerAll(@RequestBody List<NewsRequest> requests) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(security ->
                Flux.fromStream(requests.parallelStream()).flatMap(request -> {
                    request.setAuditor(UserAuditor.withUserId(security.getUserId()));
                    request.setAuditTime(LocalDateTime.now());
                    return this.newsService.audit(request);
                }));
    }

}