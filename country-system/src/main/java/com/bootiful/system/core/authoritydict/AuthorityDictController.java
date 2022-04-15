package com.bootiful.system.core.authoritydict;

import com.bootiful.commons.security.ReactiveSecurityDetailsHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Tag(name = "资源管理")
@RestController
@RequestMapping("/authority/dict/v1")
@RequiredArgsConstructor
public class AuthorityDictController {

    private final AuthorityDictService dictService;

    @GetMapping("search")
    public Flux<AuthorityDictOnly> search(DictSearchRequest dictRequest) {
        return this.dictService.search(dictRequest);
    }

    @GetMapping("menu")
    public Flux<AuthorityDictOnly> menu() {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.dictService.loadAuthorityMenu(securityDetails.getAuthorities()));
    }

    /**
     * 批量关联2级菜单
     *
     * @param id           一级菜单ID
     * @param dictRequests 关联的二级菜单
     * @return 成功数
     */
    @Operation(summary = "批量关联2级菜单")
    @PostMapping("batch/{id}")
    public Flux<Integer> batchAssociation(@PathVariable("id") Integer id,
                                          @RequestBody List<AuthorityDictRequest> dictRequests) {
        return this.dictService.batchAssociation(id, dictRequests);
    }

    @PostMapping
    public Mono<AuthorityDictOnly> post(@Valid @RequestBody AuthorityDictRequest dictRequest) {
        return this.dictService.operation(dictRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return this.dictService.delete(id);
    }
}