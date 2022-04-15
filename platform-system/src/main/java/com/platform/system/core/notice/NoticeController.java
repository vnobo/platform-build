package com.platform.system.core.notice;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author jjh
 * @date 2021/6/7
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "通知管理")
@RequestMapping("/notifications/manager/v1")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("page")
    public Mono<Page<NoticeOnly>> page(NoticeSearch noticeSearch,
                                       @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return this.noticeService.page(noticeSearch, pageable);
    }

    @GetMapping("search")
    public Flux<NoticeOnly> search(NoticeSearch noticeSearch,
                                   @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return this.noticeService.search(noticeSearch, pageable);
    }

    @PostMapping
    @Operation(summary = "添加数据")
    public Mono<Notice> post(@RequestBody NoticeRequest request) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(security -> {
            request.setTenantId(security.getTenantId());
            request.setTenantCode(security.getTenantCode());
            return this.noticeService.operation(request);
        });
    }

    @DeleteMapping
    @Operation(summary = "根据id删除数据")
    public Mono<Void> deleteById(@RequestBody NoticeRequest request) {
        return this.noticeService.delete(request);
    }


    @DeleteMapping("batch")
    public Mono<Void> batch(@RequestBody List<NoticeRequest> ids) {
        return this.noticeService.deleteBatch(ids);
    }
}