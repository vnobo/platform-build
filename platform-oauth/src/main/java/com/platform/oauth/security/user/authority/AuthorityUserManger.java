package com.platform.oauth.security.user.authority;

import com.platform.commons.security.SecurityTokenHelper;
import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

  public Flux<AuthorityUser> authorizing(Long userId, AuthorityUserRequest authorizingRequest) {
    return this.authorityUserRepository
        .deleteByUserIdAndSystem(userId, authorizingRequest.getSystem())
        .flatMapMany(
            result ->
                this.authorityUserRepository.saveAll(
                    authorizingRequest.getRules().parallelStream()
                        .map(
                            authority ->
                                AuthorityUser.of(authorizingRequest.getSystem(), userId, authority))
                        .collect(Collectors.toList())));
  }

  public Mono<Integer> deleteByUserId(Long userId) {
    return this.authorityUserRepository.deleteByUserId(userId);
  }

  public Flux<AuthorityUser> getAuthorities(long userId) {
    return Flux.deferContextual(
        contextView ->
            this.search(
                AuthorityUserRequest.withUserId(userId)
                    .system(SecurityTokenHelper.systemForContext(contextView))));
  }
}