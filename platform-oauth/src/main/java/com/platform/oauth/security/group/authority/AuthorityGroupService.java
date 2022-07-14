package com.platform.oauth.security.group.authority;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
public class AuthorityGroupService extends BaseAutoToolsUtil {

    private final AuthorityGroupRepository authorityGroupRepository;

    public Flux<AuthorityGroup> search(AuthorityGroupRequest request) {
        return entityTemplate.select(Query.query(request.toCriteria()), AuthorityGroup.class);
    }

    public Flux<AuthorityGroup> authorizing(AuthorityGroupRequest authorityGroupRequest) {
        return this.authorityGroupRepository.findByGroupCode(authorityGroupRequest.getGroupCode())
                .collectList().flatMapMany(oldList -> {
                    List<AuthorityGroup> addList = authorityGroupRequest.getAuthorities().parallelStream()
                            .filter(a -> oldList.size() == 0 || oldList.parallelStream()
                                    .noneMatch(o -> o.getAuthority().equals(a)))
                            .map(a -> AuthorityGroupRequest.of(authorityGroupRequest.getGroupCode(), a))
                            .collect(Collectors.toList());
                    List<AuthorityGroup> deleteList = oldList.parallelStream()
                            .filter(a -> !authorityGroupRequest.getAuthorities().contains(a.getAuthority()))
                            .collect(Collectors.toList());
                    return this.authorityGroupRepository.saveAll(addList).defaultIfEmpty(authorityGroupRequest)
                            .delayUntil(res -> this.authorityGroupRepository.deleteAll(deleteList));
                });
    }

}