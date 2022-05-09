package com.platform.oauth.security.group;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.group.authority.AuthorityGroup;
import com.platform.oauth.security.group.authority.AuthorityGroupManger;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
import com.platform.oauth.security.group.member.MemberGroupManager;
import com.platform.oauth.security.group.member.MemberGroupOnly;
import com.platform.oauth.security.group.member.MemberGroupRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Set;

/**
 * com.bootiful.oauth.security.group.GroupController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Tag(name = "角色组管理")
@RestController
@RequestMapping("/group/manager/v1")
@RequiredArgsConstructor
public class GroupController {

    private final GroupManager managerService;
    private final MemberGroupManager memberGroupManager;
    private final AuthorityGroupManger authorityGroupManger;

    @Operation(summary = "获取角色列表")
    @GetMapping("search")
    public Flux<GroupOnly> search(GroupRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMapMany(securityDetails ->
                this.managerService.search(request, pageable));
    }

    @Operation(summary = "获取角色分页")
    @GetMapping("page")
    public Mono<Page<GroupOnly>> page(GroupRequest request, Pageable pageable) {
        return ReactiveSecurityDetailsHolder.getContext().flatMap(securityDetails ->
                this.managerService.page(request, pageable));
    }

    @Hidden
    @GetMapping
    public Flux<GroupOnly> get(GroupRequest request) {
        return this.managerService.search(request, Pageable.ofSize(100));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Mono<Group> post(@Valid @RequestBody GroupRequest request) {
        return this.managerService.add(request);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return this.managerService.delete(id);
    }

    @Operation(summary = "获取角色权限")
    @GetMapping("authorities")
    public Flux<AuthorityGroup> authorities(AuthorityGroupRequest request) {
        Assert.isTrue(request.validAllEmpty(), "查询条件不能都为空!");
        return this.authorityGroupManger.search(request);
    }

    @Operation(summary = "角色分配权限")
    @PostMapping("authorizing")
    public Flux<AuthorityGroup> authorizing(@RequestBody AuthorityGroupRequest request) {
        return this.authorityGroupManger.authorizing(request);
    }

    @Operation(summary = "分配角色用户")
    @PostMapping("/member/{id}")
    public Flux<MemberGroupOnly> addMembers(@Parameter(name = "角色组ID") @PathVariable Integer id,
                                            @Parameter(name = "增加用户ID集合") @Valid @RequestBody Set<Long> request) {
        return this.memberGroupManager.addMembers(id, request);
    }

    @Operation(summary = "删除角色用户")
    @DeleteMapping("/member/{id}")
    public Flux<Void> deleteMembers(@Parameter(name = "角色组ID") @PathVariable Integer id,
                                    @Parameter(name = "删除用户ID集合") @Valid @RequestBody Set<Long> request) {
        return this.memberGroupManager.deleteMembers(id, request);
    }

    @Operation(summary = "获取角色用户")
    @GetMapping("members")
    public Mono<Page<MemberGroupOnly>> members(MemberGroupRequest groupRequest, Pageable pageable) {
        return this.memberGroupManager.members(groupRequest, pageable);
    }
}