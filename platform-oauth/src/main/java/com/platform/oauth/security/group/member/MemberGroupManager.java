package com.platform.oauth.security.group.member;

import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.commons.utils.SqlUtils;
import com.platform.oauth.security.group.GroupManager;
import com.platform.oauth.security.user.UserOnly;
import com.platform.oauth.security.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.group.MemberGroupManagerService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MemberGroupManager extends BaseAutoToolsUtil {

  private final MemberGroupRepository memberRepository;
  private final UserRepository userRepository;
  private final GroupManager groupManager;

  public Mono<Page<MemberGroupOnly>> members(MemberGroupRequest groupRequest, Pageable pageable) {

    String querySql =
        "select se_group_members.id,se_group_members.group_id,se_group_members.user_id,"
            + "se_group_members.created_time,se_group_members.updated_time,"
            + "se_users.name as uname,se_users.id_card as id_card,se_groups.name as group_name,se_users.enabled,"
            + "se_users.email,se_users.phone,se_users.username from se_group_members"
            + " inner join  se_users on se_group_members.user_id = se_users.id"
            + " inner join  se_groups on se_group_members.group_id = se_groups.id "
            + groupRequest.toMemberWhere();

    String countSql =
        "select count(*) from se_group_members"
            + " inner join se_users on se_group_members.user_id = se_users.id"
            + " inner join se_groups on se_group_members.group_id = se_groups.id "
            + groupRequest.toMemberWhere();

    Flux<MemberGroupOnly> queryFlux =
        super.entityTemplate
            .getDatabaseClient()
            .sql(querySql + SqlUtils.applyPage(pageable))
            .map(
                (row, rowMetadata) ->
                    super.mappingR2dbcConverter.read(MemberGroupOnly.class, row, rowMetadata))
            .all();
    Mono<Long> countMono =
        super.entityTemplate
            .getDatabaseClient()
            .sql(countSql)
            .map(
                (row, rowMetadata) ->
                    super.mappingR2dbcConverter.read(Long.class, row, rowMetadata))
            .first();

    return queryFlux
        .collectList()
        .zipWith(countMono)
        .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
  }

  /**
   * 角色增加用户
   *
   * @param groupId 要增肌的组ID
   * @param userList 用户列表
   * @return 授权信息
   */
  public Flux<MemberGroupOnly> addMembers(Integer groupId, Set<Long> userList) {
    return Flux.fromStream(userList.parallelStream())
        .map(userId -> MemberGroupRequest.of(groupId, userId))
        .flatMap(this::save)
        .flatMapSequential(this::integrateOnly);
  }

  /**
   * 角色删除用户
   *
   * @param groupId 要增肌的组ID
   * @param userList 用户列表
   * @return 授权信息
   */
  public Flux<Void> deleteMembers(Integer groupId, Set<Long> userList) {
    return Flux.fromStream(userList.parallelStream())
        .flatMap(userId -> this.memberRepository.findByGroupIdAndUserId(groupId, userId))
        .flatMap(this.memberRepository::delete);
  }

  public Mono<Integer> deleteByUserId(Long userId) {
    return this.memberRepository.deleteByUserId(userId);
  }

  public Mono<Integer> deleteByGroupId(Integer groupId) {
    return this.memberRepository.deleteByGroupId(groupId);
  }

  public Mono<MemberGroup> save(MemberGroup group) {
    return this.memberRepository
        .findByGroupIdAndUserId(group.getGroupId(), group.getUserId())
        .switchIfEmpty(this.memberRepository.save(group));
  }
  /**
   * 从 MemberGroup 转换为 only, 1,增加角色信息 2,增加用户信息
   *
   * @param memberGroup 要转换的角色用户关系
   * @return 转换后整合的角色用户关系
   */
  private Mono<MemberGroupOnly> integrateOnly(MemberGroup memberGroup) {
    return this.userRepository
        .findById(memberGroup.getUserId())
        .zipWith(this.groupManager.loadGroupOnly(memberGroup.getGroupId()))
        .map(
            entityTuples ->
                MemberGroupOnly.withMemberGroup(memberGroup)
                    .userOnly(UserOnly.withUser(entityTuples.getT1()))
                    .groupOnly(entityTuples.getT2()));
  }
}