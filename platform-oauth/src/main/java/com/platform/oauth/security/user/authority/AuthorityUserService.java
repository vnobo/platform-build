package com.platform.oauth.security.user.authority;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
public class AuthorityUserService extends BaseAutoToolsUtil {

    private final AuthorityUserRepository authorityUserRepository;

    public Flux<AuthorityUser> search(AuthorityUserRequest authorizingRequest) {
        return super.entityTemplate.select(Query.query(authorizingRequest.toCriteria()), AuthorityUser.class);
    }

    public Flux<AuthorityUser> authorizing(AuthorityUserRequest authorizingRequest) {
        return this.authorityUserRepository.findByUserCode(authorizingRequest.getUserCode()).collectList()
                .flatMapMany(oldList -> {
                    List<AuthorityUser> addList = authorizingRequest.getAuthorities().parallelStream()
                            .filter(a -> oldList.size() == 0 || oldList.parallelStream()
                                    .noneMatch(o -> o.getAuthority().equals(a)))
                            .map(a -> AuthorityUserRequest.of(authorizingRequest.getUserCode(), a))
                            .collect(Collectors.toList());
                    List<AuthorityUser> deleteList = oldList.parallelStream()
                            .filter(a -> !authorizingRequest.getAuthorities().contains(a.getAuthority()))
                            .collect(Collectors.toList());
                    return this.authorityUserRepository.saveAll(addList).defaultIfEmpty(authorizingRequest)
                            .delayUntil(res -> this.authorityUserRepository.deleteAll(deleteList));
                });
    }

}