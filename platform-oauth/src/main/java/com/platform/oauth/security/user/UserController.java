package com.platform.oauth.security.user;

import cn.hutool.core.util.IdcardUtil;
import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.user.authority.AuthorityUser;
import com.platform.oauth.security.user.authority.AuthorityUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
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
@Tag(name = "账户管理")
@RestController
@RequestMapping("/user/manager/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserManager managerService;

    @GetMapping("search")
    public Flux<UserOnly> search(UserRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails -> this.managerService
                .search(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @GetMapping("page")
    public Mono<Page<UserOnly>> page(UserRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails -> this.managerService
                .page(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @PostMapping("/authorizing/{id}")
    public Flux<AuthorityUser> authorizing(@PathVariable Long id, @Valid @RequestBody AuthorityUserRequest request) {
        return this.managerService.authorizing(id, request);
    }

    @GetMapping
    public Flux<User> get(UserRequest request) {
        return this.managerService.loadUsers(request);
    }

    @PostMapping
    public Mono<User> post(@Valid @RequestBody UserRequest request) {
        Assert.hasText(request.getPassword(), "登录用户密码[password]不能为空!");
        Assert.isTrue(IdcardUtil.isValidCard(request.affirmIdCard()), "身份证[idCard]不合法!");
        return this.managerService.register(request);
    }

    @PutMapping("{id}")
    public Mono<User> put(@PathVariable Long id, @Valid @RequestBody ModifyUserRequest request) {
        request.setId(id);
        return this.managerService.modify(request);
    }

    @PutMapping("change/password")
    public Mono<UserOnly> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Assert.isTrue(request.getPassword().equals(request.getNewPassword()), "新密码和确认密码不相等!");
        return this.managerService.changePassword(request);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return this.managerService.delete(id);
    }

}