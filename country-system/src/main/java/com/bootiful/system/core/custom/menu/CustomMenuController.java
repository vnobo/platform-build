package com.bootiful.system.core.custom.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * com.bootiful.system.core.menu.CustomMenuController
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/5
 */
@Tag(name = "用户自定义菜单")
@RestController
@RequestMapping("custom/menu/v1")
@RequiredArgsConstructor
public class CustomMenuController {

    private final CustomMenuManager managerService;

    @GetMapping("search")
    public Flux<CustomMenu> search(CustomMenuRequest request,
                                   @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                           Pageable pageable) {
        Assert.notNull(request.getUserId(), () -> "获取自定义常用菜单[userId]不能为空!");
        return this.managerService.search(request, pageable);
    }

    @GetMapping("page")
    public Mono<Page<CustomMenu>> page(CustomMenuRequest request,
                                       @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                               Pageable pageable) {
        return this.managerService.page(request, pageable);
    }

    @PostMapping("batch")
    public Flux<CustomMenu> batch(@Valid @RequestBody List<CustomMenuRequest> request) {
        return this.managerService.batch(request);
    }

    @PostMapping
    public Mono<CustomMenu> post(@Valid @RequestBody CustomMenuRequest request) {
        return this.managerService.operation(request);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return this.managerService.delete(id);
    }

}