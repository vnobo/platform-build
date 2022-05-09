package com.platform.oauth.security.user;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.user.authority.AuthorityUser;
import com.platform.oauth.security.user.authority.AuthorityUserManger;
import com.platform.oauth.security.user.authority.AuthorityUserRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.bootiful.oauth.security.user.UserController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user/manager/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserManager managerService;
    private final AuthorityUserManger authorityUserManger;

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

    @Hidden
    @GetMapping
    public Flux<User> get(UserRequest request) {
        return this.managerService.loadUsers(request);
    }

    @PostMapping
    public Mono<User> post(@Validated(UserRequest.Register.class) @RequestBody UserRequest request) {
        return this.managerService.register(request);
    }

    @PutMapping("{id}")
    public Mono<User> put(@PathVariable Long id, @Validated(UserRequest.Modify.class) @RequestBody UserRequest request) {
        request.setId(id);
        return this.managerService.modify(request);
    }

    @PutMapping("change/password")
    public Mono<UserOnly> changePassword(@Validated(UserRequest.ChangePassword.class) @RequestBody UserRequest request) {
        return this.managerService.changePassword(request);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return this.managerService.delete(id);
    }

    @PostMapping("authorizing")
    public Flux<AuthorityUser> authorizing(@Valid @RequestBody AuthorityUserRequest request) {
        return this.authorityUserManger.authorizing(request);
    }
}