package com.platform.oauth.security;

import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.commons.utils.SystemType;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
import com.platform.oauth.security.group.authority.AuthorityGroupService;
import com.platform.oauth.security.tenant.member.MemberTenantOnly;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import com.platform.oauth.security.tenant.member.MemberTenantService;
import com.platform.oauth.security.user.User;
import com.platform.oauth.security.user.UserBinding;
import com.platform.oauth.security.user.UsersService;
import com.platform.oauth.security.user.authority.AuthorityUserService;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final UsersService usersService;
    private final AuthorityUserService authorityUserService;
    private final AuthorityGroupService authorityGroupService;
    private final MemberTenantService memberTenantService;

    public Mono<LoginSecurityDetails> register(RegisterRequest request) {

        Assert.notNull(request.getUsername(), "???????????????[username]????????????!");
        // ??????????????????,??????????????????
        if (!StringUtils.hasLength(request.getPassword())) {
            request.setPassword("A123456a");
        }

        if (StringUtils.hasLength(request.getAppId()) && StringUtils.hasLength(request.getOpenid())) {
            log.debug("????????????????????????!");
            request.setBinding(UserBinding.withWeiXin(request.getAppId(), request.getOpenid()));
        }

        // ?????????????????????????????????,?????????0
        if (ObjectUtils.isEmpty(request.getTenantId())) {
            request.setTenantId(0);
        }

        Function<User, Mono<String[]>> authoritiesFunction = user ->
                this.authorities(Optional.ofNullable(user.getId()).orElse(-1L));

        return this.usersService.register(request.toUserRequest())
                .flatMap(user -> authoritiesFunction.apply(user).map(authorities -> LoginSecurityDetails
                        .of(user.getUsername(), user.getPassword(), user.getEnabled())
                        .authorities(authorities)));
    }

    public Mono<LoginSecurityDetails> login(String username) {

        Mono<User> userMono = this.usersService.loadByUsername(username);

        Function<User, Mono<String[]>> authoritiesFunction = user ->
                this.authorities(Optional.ofNullable(user.getId()).orElse(-1L));

        return userMono.flatMap(user -> authoritiesFunction.apply(user).map(authorities ->
                LoginSecurityDetails.of(user.getUsername(), user.getPassword(), user.getEnabled())
                        .authorities(authorities)));
    }

    public Mono<SimplerSecurityDetails> loadSecurity(String username) {

        Mono<SimplerSecurityDetails> securityDetailsMono = this.usersService.loadByUsername(username)
                .map(user -> SimplerSecurityDetails.of(user.getId(), user.getUsername()));

        Function<SimplerSecurityDetails, Flux<MemberTenantOnly>> userTenantFunction = securityDetails ->
                this.memberTenantService.search(MemberTenantRequest.withUserCode(securityDetails.getUserId()));

        return securityDetailsMono.flatMap(securityDetails -> userTenantFunction.apply(securityDetails)
                .distinct().collectList().map(tenants -> securityDetails.tenants(
                        tenants.parallelStream().collect(Collectors.toSet()))));
    }

    public Mono<Void> loginSuccess(String username) {
        return entityTemplate.update(User.class)
                .matching(Query.query(Criteria.where("username").like(username).ignoreCase(true)))
                .apply(Update.update("lastLoginTime", LocalDateTime.now()))
                .then();
    }

    private Mono<String[]> authorities(long userId) {
        return this.getAuthorities(userId).concatWith(this.getGroupAuthorities(userId))
                .distinct().collectList().map(listAuth -> listAuth.parallelStream()
                        .map(SimpleAuthority::getAuthority).distinct()
                        .toList().toArray(new String[0]));
    }

    /**
     * ??????????????????
     *
     * @param userId ??????ID
     * @return ?????????????????????????????????
     */
    private Flux<SimpleAuthority> getAuthorities(long userId) {
        return this.authorityUserService.getAuthorities(userId).cast(SimpleAuthority.class);
    }

    /**
     * ????????????ID ????????????????????? ???????????????SYSTEM ????????????????????????
     *
     * @param userId ??????ID
     * @return ?????????????????????
     */
    private Flux<SimpleAuthority> getGroupAuthorities(long userId) {
        return Flux.deferContextual(contextView -> {
            AuthorityGroupRequest groupRequest = AuthorityGroupRequest.withUserId(userId);
            Optional<SystemType> optSystemStr = contextView.getOrEmpty("system");
            optSystemStr.ifPresent(groupRequest::setSystem);
            return this.authorityGroupService.search(groupRequest);
        }).cast(SimpleAuthority.class);
    }

    /**
     * ????????????????????????
     *
     * @param memberTenantRequest ????????????????????????
     * @return ?????????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    public Mono<SimplerSecurityDetails> tenantCut(MemberTenantRequest memberTenantRequest) {
        return this.memberTenantService.operation(memberTenantRequest)
                .delayUntil(this::updateUserTenant)
                .map(memberTenant -> SimplerSecurityDetails.of(memberTenant.getUserId(), null));
    }

    /**
     * ????????????????????????
     *
     * @param memberTenant ??????????????????
     * @return ??????????????????
     */
    private Mono<Integer> updateUserTenant(MemberTenantOnly memberTenant) {
        return entityTemplate.update(User.class)
                .matching(Query.query(Criteria.where("userId").is(memberTenant.getUserId())))
                .apply(Update.update("tenantId", memberTenant.getTenantId())
                        .set("tenantCode", memberTenant.getTenantCode()));
    }
}