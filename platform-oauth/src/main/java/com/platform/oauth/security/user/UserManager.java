package com.platform.oauth.security.user;

import com.platform.commons.annotation.RestServerException;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.oauth.security.group.member.MemberGroupManager;
import com.platform.oauth.security.tenant.TenantManager;
import com.platform.oauth.security.tenant.TenantRequest;
import com.platform.oauth.security.tenant.member.MemberTenantManager;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import com.platform.oauth.security.user.authority.AuthorityUserManger;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

/**
 * com.bootiful.oauth.security.user.UsersService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * <p>Created by 2021/5/31
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserManager extends BaseAutoToolsUtil {

    private final PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();
    private final UserRepository userRepository;
    private final AuthorityUserManger authorityUserManger;
    private final MemberGroupManager memberGroupManager;
    private final TenantManager tenantManager;
    private final MemberTenantManager memberTenantManager;

    public Flux<UserOnly> search(UserRequest userRequest, Pageable pageable) {
        return super.entityTemplate.select(Query.query(userRequest.toCriteria()).with(pageable), User.class)
                .flatMapSequential(this::integrateOnly);
    }

    public Mono<Page<UserOnly>> page(UserRequest userRequest, Pageable pageable) {
        return search(userRequest, pageable)
                .collectList()
                .zipWith(super.entityTemplate.count(Query.query(userRequest.toCriteria()), User.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<User> loadUsers(UserRequest userRequest) {
        return super.entityTemplate.select(Query.query(userRequest.toCriteria()), User.class);
    }

    public Mono<User> loadByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private Mono<UserOnly> integrateOnly(User user) {
        return Mono.just(UserOnly.withUser(user));
    }

    public Mono<User> register(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Mono<User> nextStep = this.tenantManager.loadById(userRequest.getTenantId())
                .defaultIfEmpty(TenantRequest.withId(0))
                .map(tenant -> userRequest.tenantCode(tenant.getCode())).flatMap(this::operation);
        return this.userRepository.findByUsername(userRequest.getUsername()).switchIfEmpty(nextStep);
    }

    public Mono<User> modify(ModifyUserRequest modifyUserRequest) {
        return this.userRepository.findById(modifyUserRequest.getId())
                .filterWhen(old -> this.userRepository.exists(Example.of(
                                UserRequest.withUsername(modifyUserRequest.getUsername()).toUser(),
                                ExampleMatcher.matching().withIgnoreCase("username")))
                        .map(result -> !result || Objects.equals(old.getUsername(), modifyUserRequest.getUsername())))
                .switchIfEmpty(Mono.error(RestServerException.withMsg("登录用户名["
                        + modifyUserRequest.getUsername() + "]已存在!")))
                .flatMap(oldUser -> {
                    UserRequest userRequest = new UserRequest();
                    BeanUtils.copyProperties(modifyUserRequest, userRequest);
                    userRequest.setPassword(oldUser.getPassword());
                    return this.operation(userRequest);
                });
    }

    public Mono<User> operation(UserRequest userRequest) {
        return this.save(userRequest.toUser()).publishOn(Schedulers.boundedElastic())
                .doOnNext(userOnly -> this.memberTenantManager.operation(MemberTenantRequest
                                .of(userOnly.getTenantId(), userOnly.getId()).isDefault(true))
                        .subscribe(result -> log.debug("关联租户信息成功! {}", result)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> delete(Long id) {
        return Flux.concat(
                        this.memberGroupManager.deleteByUserId(id),
                        this.memberTenantManager.deleteByUserId(id),
                        this.authorityUserManger.deleteByUserId(id))
                .delayUntil(res -> userRepository.deleteById(id))
                .then();
    }

    public Mono<User> save(User user) {
        if (user.isNew()) {
            return this.userRepository.save(user);
        } else {
            assert user.getId() != null;
            return this.userRepository.findById(user.getId())
                    .flatMap(old -> {
                        user.setCreatedTime(old.getCreatedTime());
                        user.setPassword(old.getPassword());
                        return this.userRepository.save(user);
                    });
        }
    }

    public Mono<UserOnly> changePassword(ChangePasswordRequest request) {
        return this.userRepository.findByUsername(request.getUsername())
                .flatMap(old -> {
                    old.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    return this.userRepository.save(old);
                })
                .flatMap(this::integrateOnly)
                .switchIfEmpty(Mono.error(RestServerException.withMsg("你要修改用户不存在!")));
    }
}