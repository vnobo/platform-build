package com.platform.oauth.security.group;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.oauth.security.group.authority.AuthorityGroup;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
import com.platform.oauth.security.group.member.MemberGroupManager;
import com.platform.oauth.security.group.member.MemberGroupOnly;
import com.platform.oauth.security.group.member.MemberGroupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

  @GetMapping("search")
  public Flux<GroupOnly> search(GroupRequest request, Pageable pageable) {
    return ReactiveSecurityDetailsHolder.getContext()
        .flatMapMany(
            securityDetails ->
                this.managerService.search(
                    request.securityTenantCode(securityDetails.getTenantCode()), pageable));
  }

  @GetMapping("page")
  public Mono<Page<GroupOnly>> page(GroupRequest request, Pageable pageable) {
    return ReactiveSecurityDetailsHolder.getContext()
        .flatMap(
            securityDetails ->
                this.managerService.page(
                    request.securityTenantCode(securityDetails.getTenantCode()), pageable));
  }

  @GetMapping
  public Flux<GroupOnly> get(GroupRequest request) {
    return this.managerService.search(request, Pageable.ofSize(100));
  }

  @PostMapping
  public Mono<Group> post(@Valid @RequestBody GroupRequest request) {
    return this.managerService.add(request);
  }

  @DeleteMapping("{id}")
  public Mono<Void> delete(@PathVariable Integer id) {
    return this.managerService
        .delete(id)
        .delayUntil(res -> this.memberGroupManager.deleteByGroupId(id));
  }

  @GetMapping("authority/search")
  public Flux<AuthorityGroup> authoritiesSearch(AuthorityGroupRequest request) {
    Assert.isTrue(request.validAllEmpty(), "查询条件不能都为空!");
    return this.managerService.loadAuthorities(request);
  }

  @PostMapping("authorizing/{id}")
  public Flux<AuthorityGroup> authorizing(
      @PathVariable Integer id, @RequestBody AuthorityGroupRequest request) {
    return this.managerService.authorizing(id, request);
  }

  @PostMapping("/member/{id}")
  public Flux<MemberGroupOnly> addMembers(
      @Parameter(name = "角色组ID") @PathVariable Integer id,
      @Parameter(name = "增加用户ID集合") @Valid @RequestBody Set<Long> request) {
    return this.memberGroupManager.addMembers(id, request);
  }

  @DeleteMapping("/member/{id}")
  public Flux<Void> deleteMembers(
      @Parameter(name = "角色组ID") @PathVariable Integer id,
      @Parameter(name = "删除用户ID集合") @Valid @RequestBody Set<Long> request) {
    return this.memberGroupManager.deleteMembers(id, request);
  }

  @GetMapping("/member/{id}")
  public Mono<Page<MemberGroupOnly>> members(
      @Parameter(name = "角色组ID") @PathVariable Integer id,
      @Parameter(name = "获取角色查询条件") MemberGroupRequest groupRequest,
      @PageableDefault(sort = "se_group_members.user_id", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return this.memberGroupManager.members(groupRequest.id(id), pageable);
  }

  @Operation(summary = "获取角色组的用户")
  @GetMapping("members")
  public Mono<Page<MemberGroupOnly>> members(
      MemberGroupRequest groupRequest,
      @PageableDefault(sort = "se_group_members.user_id", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return this.memberGroupManager.members(groupRequest, pageable);
  }
}