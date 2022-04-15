package com.platform.system.core.menu;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Tag(name = "资源管理")
@RestController
@RequestMapping("/menu/dict/v1")
@RequiredArgsConstructor
public class MenuDictController {

    private final MenuDictService dictService;

    @GetMapping("search")
    public Flux<MenuDictOnly> search(MenuDictRequest dictRequest) {
        return this.dictService.search(dictRequest);
    }

    @GetMapping("me")
    public Flux<MenuDictOnly> me() {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.dictService.loadAuthorityMenu(securityDetails.getAuthorities()));
    }

    @PostMapping
    public Mono<MenuDictOnly> post(@Valid @RequestBody MenuDictRequest dictRequest) {
        return this.dictService.operation(dictRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return this.dictService.delete(id);
    }
}