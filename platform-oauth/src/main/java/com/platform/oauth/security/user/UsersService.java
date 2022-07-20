package com.platform.oauth.security.user;

import cn.hutool.core.util.IdUtil;
import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.oauth.security.group.member.MemberGroupRepository;
import com.platform.oauth.security.tenant.member.MemberTenantRepository;
import com.platform.oauth.security.user.authority.AuthorityUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private final UserRepository userRepository;
    private final AuthorityUserRepository authorityUserRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberTenantRepository memberTenantRepository;

    public Flux<UserOnly> search(UserRequest userRequest, Pageable pageable) {
        return super.entityTemplate.select(Query.query(userRequest.toCriteria()).with(pageable),
                        User.class)
                .flatMapSequential(this::integrateOnly);
    }

    public Mono<Page<UserOnly>> page(UserRequest userRequest, Pageable pageable) {
        return search(userRequest, pageable)
                .collectList()
                .zipWith(super.entityTemplate.count(Query.query(userRequest.toCriteria()), User.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    private Mono<UserOnly> integrateOnly(User user) {
        return Mono.just(UserOnly.withUser(user));
    }

    public Mono<User> loadByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public Mono<User> operate(UserRequest userRequest) {
        return this.userRepository.findByUsernameIgnoreCase(userRequest.getUsername())
                .flatMap(old -> this.save(userRequest.id(old.getId()).toUser()))
                .switchIfEmpty(this.save(userRequest.toUser()));
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
            user.setCode(IdUtil.fastSimpleUUID());
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