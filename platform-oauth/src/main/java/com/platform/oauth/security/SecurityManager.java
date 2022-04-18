package com.platform.oauth.security;

import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.SecurityDetailsTenant;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.commons.utils.SystemType;
import com.platform.oauth.security.group.GroupManager;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
import com.platform.oauth.security.tenant.member.MemberTenantManager;
import com.platform.oauth.security.tenant.member.MemberTenantOnly;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import com.platform.oauth.security.user.User;
import com.platform.oauth.security.user.UserBinding;
import com.platform.oauth.security.user.UserManager;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.SecurityService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class SecurityManager extends BaseAutoToolsUtil {
  private final UserManager userManager;
  private final GroupManager groupManager;
  private final MemberTenantManager memberTenantManager;

  public Mono<LoginSecurityDetails> register(RegisterRequest request) {

    Assert.notNull(request.getUsername(), "登录用户名[username]不能为空!");
    // 用户密码为空,默认用户密码
    if (!StringUtils.hasLength(request.getPassword())) {
      request.setPassword("A123456a");
    }

    if (StringUtils.hasLength(request.getAppId()) && StringUtils.hasLength(request.getOpenid())) {
      log.debug("微信注册用户信息!");
      request.setBinding(UserBinding.withWeiXin(request.getAppId(), request.getOpenid()));
    }

    // 角色组未设置,默认给用户角色
    if (ObjectUtils.isEmpty(request.getGroupId())) {
      request.setGroupId(2);
    }

    // 租户是否设置如果未设置,默认给0
    if (ObjectUtils.isEmpty(request.getTenantId())) {
      request.setTenantId(0);
    }

    Function<User, Mono<String[]>> authoritiesFunction =
        user -> this.authorities(Optional.ofNullable(user.getId()).orElse(-1L));

    return this.userManager
        .register(request.toUserRequest())
        .flatMap(
            user ->
                authoritiesFunction
                    .apply(user)
                    .map(
                        authorities ->
                            LoginSecurityDetails.of(
                                    user.getUsername(), user.getPassword(), user.getEnabled())
                                .authorities(authorities)))
        .contextWrite(context -> context.put("system", request.getSystem()));
  }

  public Mono<LoginSecurityDetails> login(String username) {

    Mono<User> userMono = this.userManager.loadByUsername(username);

    Function<User, Mono<String[]>> authoritiesFunction =
        user -> this.authorities(Optional.ofNullable(user.getId()).orElse(-1L));

    return userMono.flatMap(
        user ->
            authoritiesFunction
                .apply(user)
                .map(
                    authorities ->
                        LoginSecurityDetails.of(
                                user.getUsername(), user.getPassword(), user.getEnabled())
                            .authorities(authorities)));
  }

  public Mono<SimplerSecurityDetails> loadSecurity(String username) {

    Mono<SimplerSecurityDetails> securityDetailsMono =
        this.userManager
            .loadByUsername(username)
            .map(user -> SimplerSecurityDetails.of(user.getId(), user.getUsername()));

    Function<SimplerSecurityDetails, Flux<SecurityDetailsTenant>> userTenantFunction =
        securityDetails ->
            this.memberTenantManager
                .search(MemberTenantRequest.withUserId(securityDetails.getUserId()))
                .map(
                    tenantOnlyName ->
                        SecurityDetailsTenant.of(
                            tenantOnlyName.getTenantId(),
                            tenantOnlyName.getTenantCode(),
                            tenantOnlyName.getTenantName(),
                            tenantOnlyName.getIsDefault(),
                            tenantOnlyName.getUserId(),
                            tenantOnlyName.getUserName(),
                            tenantOnlyName.getTenantExtend()));

    return securityDetailsMono.flatMap(
        securityDetails ->
            userTenantFunction
                .apply(securityDetails)
                .distinct()
                .collectList()
                .map(
                    tenants ->
                        securityDetails.tenants(
                            tenants.parallelStream().collect(Collectors.toSet()))));
  }

  public Mono<Void> loginSuccess(String username) {
    return entityTemplate
        .update(User.class)
        .matching(Query.query(Criteria.where("username").like(username).ignoreCase(true)))
        .apply(Update.update("lastLoginTime", LocalDateTime.now()))
        .then();
  }

  private Mono<String[]> authorities(long userId) {
    return this.getAuthorities(userId)
        .concatWith(this.getGroupAuthorities(userId))
        .distinct()
        .collectList()
        .map(
            listAuth ->
                listAuth.parallelStream()
                    .map(SimpleAuthority::getAuthority)
                    .distinct()
                    .toList()
                    .toArray(new String[0]));
  }

  /**
   * 获取用户权限
   *
   * @param userId 用户ID
   * @return 返回用户单独授权的权限
   */
  private Flux<SimpleAuthority> getAuthorities(long userId) {
    return this.userManager.loadAuthorities(userId).cast(SimpleAuthority.class);
  }

  /**
   * 根据用户ID 获取用户组权限 如果流存在SYSTEM 根据系统类型过滤
   *
   * @param userId 用户ID
   * @return 用户所有组权限
   */
  private Flux<SimpleAuthority> getGroupAuthorities(long userId) {

    AuthorityGroupRequest groupRequest = AuthorityGroupRequest.withUserId(userId);

    return Flux.deferContextual(
            contextView -> {
              Optional<SystemType> optSystemStr = contextView.getOrEmpty("system");
              optSystemStr.ifPresent(groupRequest::setSystem);
              return this.groupManager.loadAuthorities(groupRequest);
            })
        .cast(SimpleAuthority.class);
  }

  /**
   * 切换用户租户信息
   *
   * @param memberTenantRequest 当前租户用户参数
   * @return 切换后用户信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Mono<SimplerSecurityDetails> tenantCut(MemberTenantRequest memberTenantRequest) {
    return this.memberTenantManager
        .operation(memberTenantRequest)
        .delayUntil(this::updateUserTenant)
        .map(memberTenant -> SimplerSecurityDetails.of(memberTenant.getUserId(), null));
  }

  /**
   * 更新用户租户信息
   *
   * @param memberTenant 租户用户关系
   * @return 更新数据记录
   */
  private Mono<Integer> updateUserTenant(MemberTenantOnly memberTenant) {
    return entityTemplate
        .update(User.class)
        .matching(Query.query(Criteria.where("userId").is(memberTenant.getUserId())))
        .apply(
            Update.update("tenantId", memberTenant.getTenantId())
                .set("tenantCode", memberTenant.getTenantCode()));
  }
}