package com.bootiful.system.core.authoritydict;

import com.bootiful.commons.security.SecurityTokenHelper;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.bootiful.commons.security.SecurityTokenHelper.ADMINISTRATORS_GROUP_ROLE_NAME;


/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AuthorityDictService extends BaseAutoToolsUtil {

    private final AuthorityDictRepository authorityDictRepository;

    public Flux<AuthorityDictOnly> search(DictSearchRequest dictRequest) {
        return entityTemplate.select(Query.query(dictRequest.toCriteria())
                        .sort(Sort.by("sort")), AuthorityDict.class)
                .map(AuthorityDictOnly::withAuthorityDict);
    }

    public Flux<AuthorityDictOnly> loadAuthorityMenu(String[] authorities) {

        if (Arrays.asList(authorities).contains(ADMINISTRATORS_GROUP_ROLE_NAME)) {
            return Flux.deferContextual(contextView -> this.authorityDictRepository
                            .findBySystemOrderBySort(SecurityTokenHelper.systemForContext(contextView)))
                    .map(AuthorityDictOnly::withAuthorityDict);
        }
        return Flux.deferContextual(contextView -> entityTemplate.select(Query.query(Criteria.where("system")
                                .is(SecurityTokenHelper.systemForContext(contextView))
                                .and("authority").in(authorities))
                        .sort(Sort.by("sort")), AuthorityDict.class))
                .map(AuthorityDictOnly::withAuthorityDict);
    }

    /**
     * 删除菜单 权限,注意会删除所有权限组下面的菜单
     * 包括所有租户
     *
     * @param id 菜单权限组ID
     * @return 无返回 错误异常处理
     */
    public Mono<Void> delete(Integer id) {
        return this.authorityDictRepository.deleteById(id).then();
    }

    public Flux<Integer> batchAssociation(Integer parentId, List<AuthorityDictRequest> dictRequests) {
        return Flux.fromStream(dictRequests.parallelStream()).flatMap(authorityDictRequest -> {
            assert authorityDictRequest.getId() != null;
            return entityTemplate.update(AuthorityDict.class)
                    .matching(Query.query(Criteria.where("id").is(authorityDictRequest.getId())))
                    .apply(Update.update("pid", parentId));
        });
    }

    public Mono<AuthorityDictOnly> operation(AuthorityDictRequest dictRequest) {
        return this.save(dictRequest.toDict())
                .map(AuthorityDictOnly::withAuthorityDict);
    }

    public Mono<AuthorityDict> save(AuthorityDict authorityDict) {
        if (authorityDict.isNew()) {
            return this.authorityDictRepository.save(authorityDict);
        } else {
            assert authorityDict.getId() != null;
            return this.authorityDictRepository.findById(authorityDict.getId())
                    .flatMap(old -> {
                        authorityDict.setCreatedTime(old.getCreatedTime());
                        return this.authorityDictRepository.save(authorityDict);
                    });
        }
    }

}