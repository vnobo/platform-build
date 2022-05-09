package com.platform.oauth.security.user.authority;

import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * com.bootiful.oauth.security.user.authority.AuthorityManger
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Service
@RequiredArgsConstructor
public class AuthorityUserManger extends BaseAutoToolsUtil {

    private final AuthorityUserRepository authorityUserRepository;

    public Flux<AuthorityUser> search(AuthorityUserRequest authorizingRequest) {
        return super.entityTemplate.select(
                Query.query(authorizingRequest.toCriteria()), AuthorityUser.class);
    }

    public Flux<AuthorityUser> authorizing(AuthorityUserRequest authorizingRequest) {
        return this.authorityUserRepository.findByUserIdAndSystem(authorizingRequest.getUserId(),
                        authorizingRequest.getSystem()).collectList()
                .flatMapMany(oldList -> {
                    List<AuthorityUser> addList = authorizingRequest.getRules().parallelStream()
                            .filter(a -> oldList.size() == 0 || oldList.parallelStream()
                                    .noneMatch(o -> o.getAuthority().equals(a)))
                            .map(a -> AuthorityUserRequest.of(authorizingRequest.getSystem(), authorizingRequest.getUserId(), a))
                            .collect(Collectors.toList());
                    List<AuthorityUser> deleteList = oldList.parallelStream()
                            .filter(a -> !authorizingRequest.getRules().contains(a.getAuthority()))
                            .collect(Collectors.toList());
                    return this.authorityUserRepository.saveAll(addList).defaultIfEmpty(authorizingRequest)
                            .delayUntil(res -> this.authorityUserRepository.deleteAll(deleteList));
                });
    }

    public Mono<Integer> deleteByUserId(Long userId) {
        return this.authorityUserRepository.deleteByUserId(userId);
    }

    public Flux<AuthorityUser> getAuthorities(long userId) {
        return Flux.deferContextual(contextView -> this.search(AuthorityUserRequest.withUserId(userId)
                .system(ReactiveSecurityHelper.systemForContext(contextView))));
    }
}