package com.platform.oauth.security.group.authority;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * com.bootiful.oauth.security.group.authority.AuthorityManger
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Service
@RequiredArgsConstructor
public class AuthorityGroupManger extends BaseAutoToolsUtil {

  private final AuthorityGroupRepository authorityGroupRepository;

  public Flux<AuthorityGroup> search(AuthorityGroupRequest request) {
    String querySql = "select * from se_group_authorities " + request.toWhereSql();
    return entityTemplate
        .getDatabaseClient()
        .sql(querySql)
        .map(row -> mappingR2dbcConverter.read(AuthorityGroup.class, row))
        .all();
  }

  public Flux<AuthorityGroup> authorizing(Integer groupId, AuthorityGroupRequest authorityList) {
    return this.authorityGroupRepository
        .findByGroupId(groupId)
        .collectList()
        .flatMapMany(
            oldList -> {
              List<AuthorityGroup> addList =
                  authorityList.getRules().parallelStream()
                      .filter(
                          a ->
                              oldList.size() == 0
                                  || oldList.parallelStream()
                                      .noneMatch(o -> o.getAuthority().equals(a)))
                      .map(a -> AuthorityGroupRequest.of(groupId, a))
                      .collect(Collectors.toList());
              List<AuthorityGroup> deleteList =
                  oldList.parallelStream()
                      .filter(a -> !authorityList.getRules().contains(a.getAuthority()))
                      .collect(Collectors.toList());
              return this.authorityGroupRepository
                  .saveAll(addList)
                  .publishOn(Schedulers.boundedElastic())
                  .doOnComplete(
                      () -> this.authorityGroupRepository.deleteAll(deleteList).subscribe());
            });
  }

  public Mono<Integer> delete(Integer groupId) {
    return this.authorityGroupRepository.deleteByGroupId(groupId);
  }
}