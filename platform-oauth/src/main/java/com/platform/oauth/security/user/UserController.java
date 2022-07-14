package com.platform.oauth.security.user;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.user.authority.AuthorityUser;
import com.platform.oauth.security.user.authority.AuthorityUserRequest;
import com.platform.oauth.security.user.authority.AuthorityUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.user.UserController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/users/v1")
@RequiredArgsConstructor
public class UserController {
    private final UsersService managerService;
    private final AuthorityUserService authorityUserService;

    @GetMapping("search")
    public Flux<UserOnly> search(UserRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.managerService.search(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @GetMapping("page")
    public Mono<Page<UserOnly>> page(UserRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails ->
                this.managerService.page(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @PostMapping
    public Mono<User> post(@Validated(UserRequest.Register.class) @RequestBody UserRequest request) {
        return this.managerService.operate(request);
    }

    @DeleteMapping
    public Mono<Void> delete(Long id) {
        return this.managerService.delete(id);
    }

    @GetMapping("authorities")
    public Flux<AuthorityUser> authorities(AuthorityUserRequest request) {
        return this.authorityUserService.search(request);
    }

    @PostMapping("authorizing")
    public Flux<AuthorityUser> authorizing(@Validated(AuthorityUserRequest.Authorities.class)
                                           @RequestBody AuthorityUserRequest request) {
        return this.authorityUserService.authorizing(request);
    }
}