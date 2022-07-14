package com.platform.oauth.security.user;

import com.platform.commons.annotation.RestServerException;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.oauth.security.group.member.MemberGroupRepository;
import com.platform.oauth.security.tenant.member.MemberTenantRepository;
import com.platform.oauth.security.tenant.member.MemberTenantRequest;
import com.platform.oauth.security.user.authority.AuthorityUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
public class UsersService extends BaseAutoToolsUtil {

    private final PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();
    private final UserRepository userRepository;
    private final AuthorityUserRepository authorityUserRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberTenantRepository memberTenantRepository;

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

    public Mono<User> operate(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return this.userRepository.findByUsername(userRequest.getUsername())
                .switchIfEmpty(this.operation(userRequest));
    }

    public Mono<User> modify(UserRequest userRequest) {
        assert userRequest.getId() != null;
        return this.userRepository.findById(userRequest.getId())
                .filterWhen(old -> this.userRepository.exists(Example.of(
                                UserRequest.withUsername(userRequest.getUsername()).toUser(),
                                ExampleMatcher.matching().withIgnoreCase("username")))
                        .map(result -> !result || Objects.equals(old.getUsername(), userRequest.getUsername())))
                .switchIfEmpty(Mono.error(RestServerException.withMsg("登录用户名["
                        + userRequest.getUsername() + "]已存在!")))
                .flatMap(oldUser -> {
                    userRequest.setPassword(oldUser.getPassword());
                    return this.operation(userRequest);
                });
    }

    public Mono<User> operation(UserRequest userRequest) {
        return this.save(userRequest.toUser()).publishOn(Schedulers.boundedElastic())
                .doOnNext(userOnly -> this.memberTenantService.operation(MemberTenantRequest
                                .of(userOnly.getTenantId(), userOnly.getId()).isDefault(true))
                        .subscribe(result -> log.debug("关联租户信息成功! {}", result)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> delete(Long id) {
        return userRepository.findById(id)
                .delayUntil(res -> this.memberGroupRepository.deleteByUserCode(res.getCode()))
                .delayUntil(res -> this.memberTenantRepository.deleteByUserCode(res.getCode()))
                .delayUntil(res -> this.authorityUserRepository.deleteByUserCode(res.getCode()))
                .flatMap(userRepository::delete);
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

}