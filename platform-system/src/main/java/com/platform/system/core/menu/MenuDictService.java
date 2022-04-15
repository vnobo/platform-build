package com.platform.system.core.menu;

import com.platform.commons.security.SecurityTokenHelper;
import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.platform.commons.security.SecurityTokenHelper.ADMINISTRATORS_GROUP_ROLE_NAME;


/**
 * com.bootiful.oauth.core.authoritydict.AuthorityDictService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/1
 */
@Service
@RequiredArgsConstructor
public class MenuDictService extends BaseAutoToolsUtil {

    private final MenuDictRepository menuDictRepository;

    public Flux<MenuDictOnly> search(DictSearchRequest dictRequest) {
        return entityTemplate.select(Query.query(dictRequest.toCriteria())
                        .sort(Sort.by("sort")), MenuDict.class)
                .map(MenuDictOnly::withAuthorityDict);
    }

    public Flux<MenuDictOnly> loadAuthorityMenu(String[] authorities) {

        if (Arrays.asList(authorities).contains(ADMINISTRATORS_GROUP_ROLE_NAME)) {
            return Flux.deferContextual(contextView -> this.menuDictRepository
                            .findBySystemOrderBySort(SecurityTokenHelper.systemForContext(contextView)))
                    .map(MenuDictOnly::withAuthorityDict);
        }
        return Flux.deferContextual(contextView -> entityTemplate.select(Query.query(Criteria.where("system")
                                .is(SecurityTokenHelper.systemForContext(contextView))
                                .and("authority").in(authorities))
                        .sort(Sort.by("sort")), MenuDict.class))
                .map(MenuDictOnly::withAuthorityDict);
    }

    /**
     * 删除菜单 权限,注意会删除所有权限组下面的菜单
     * 包括所有租户
     *
     * @param id 菜单权限组ID
     * @return 无返回 错误异常处理
     */
    public Mono<Void> delete(Integer id) {
        return this.menuDictRepository.deleteById(id).then();
    }

    public Flux<Integer> batchAssociation(Integer parentId, List<MenuDictRequest> dictRequests) {
        return Flux.fromStream(dictRequests.parallelStream()).flatMap(authorityDictRequest -> {
            assert authorityDictRequest.getId() != null;
            return entityTemplate.update(MenuDict.class)
                    .matching(Query.query(Criteria.where("id").is(authorityDictRequest.getId())))
                    .apply(Update.update("pid", parentId));
        });
    }

    public Mono<MenuDictOnly> operation(MenuDictRequest dictRequest) {
        return this.save(dictRequest.toDict())
                .map(MenuDictOnly::withAuthorityDict);
    }

    public Mono<MenuDict> save(MenuDict menuDict) {
        if (menuDict.isNew()) {
            return this.menuDictRepository.save(menuDict);
        } else {
            assert menuDict.getId() != null;
            return this.menuDictRepository.findById(menuDict.getId())
                    .flatMap(old -> {
                        menuDict.setCreatedTime(old.getCreatedTime());
                        return this.menuDictRepository.save(menuDict);
                    });
        }
    }

}