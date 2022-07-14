package com.platform.oauth.security.group;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.group.authority.AuthorityGroup;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
import com.platform.oauth.security.group.authority.AuthorityGroupService;
import com.platform.oauth.security.group.member.MemberGroup;
import com.platform.oauth.security.group.member.MemberGroupRequest;
import com.platform.oauth.security.group.member.MemberGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * com.bootiful.oauth.security.group.GroupController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Tag(name = "角色组管理")
@RestController
@RequestMapping("/groups/v1")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService managerService;
    private final MemberGroupService memberGroupService;
    private final AuthorityGroupService authorityGroupService;

    @Operation(summary = "获取列表")
    @GetMapping("search")
    public Flux<Group> search(GroupRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.managerService.search(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Operation(summary = "获取分页")
    @GetMapping("page")
    public Mono<Page<Group>> page(GroupRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails ->
                this.managerService.page(request.securityTenantCode(securityDetails.getTenantCode()), pageable));
    }

    @Operation(summary = "创建/修改")
    @PostMapping
    public Mono<Group> post(@Valid @RequestBody GroupRequest request) {
        return this.managerService.operation(request);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping
    public Mono<Void> delete(Integer id) {
        return this.managerService.delete(id);
    }

    @Operation(summary = "获取角色权限")
    @GetMapping("authorities")
    public Flux<AuthorityGroup> authorities(AuthorityGroupRequest request) {
        Assert.isTrue(request.validAllEmpty(), "查询条件不能都为空!");
        return this.authorityGroupService.search(request);
    }

    @Operation(summary = "分配角色权限")
    @PostMapping("authorizing")
    public Flux<AuthorityGroup> authorizing(@Validated(AuthorityGroupRequest.Authority.class)
                                            @RequestBody AuthorityGroupRequest request) {
        return this.authorityGroupService.authorizing(request);
    }

    @Operation(summary = "获取角色用户")
    @GetMapping("member")
    public Mono<Page<MemberGroup>> memberPage(MemberGroupRequest groupRequest, Pageable pageable) {
        return this.memberGroupService.page(groupRequest, pageable);
    }

    @Operation(summary = "分配角色用户")
    @PostMapping("member")
    public Flux<MemberGroup> addMembers(@Validated(MemberGroupRequest.Users.class)
                                        @RequestBody MemberGroupRequest request) {
        return this.memberGroupService.operate(request);
    }

    @Operation(summary = "删除角色用户")
    @DeleteMapping("member")
    public Mono<Void> deleteMembers(Long id) {
        return this.memberGroupService.delete(id);
    }


}