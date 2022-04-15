package com.platform.system.core.custom.column;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * @author jjh
 * @date 2021/8/2
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomColumnService extends BaseAutoToolsUtil {

    private final CustomColumnRepository customColumnRepository;


    public Flux<CustomColumn> search(CustomColumnRequest request, Pageable pageable) {
        return entityTemplate.select(Query.query(request.toCriteria()).with(pageable), CustomColumn.class);
    }

    public Mono<Page<CustomColumn>> page(CustomColumnRequest request, Pageable pageable) {
        return this.search(request, pageable)
                .collectList().zipWith(entityTemplate.count(Query.query(request.toCriteria()), CustomColumn.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<CustomColumn> userSearch(CustomColumnRequest request) {
        Pageable pageable = PageRequest.of(0, 10000, Sort.by("sortNo"));
        return search(request, pageable).concatWith(search(request.userId(0L).tenantId(0), pageable))
                .distinct(CustomColumn::getPropName);
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<CustomColumn> operation(CustomColumnRequest customColumn) {
        return this.save(customColumn.toCustomColumn()).publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> this.updateAssociatedUser(result)
                        .subscribe(res -> log.debug("更新默认菜单和名称,刷新所有用户自定义菜单成功! {}", res)));
    }

    /**
     * "更新默认菜单和名称,刷新所有用户自定义菜单成功!"
     * 修改默认模板 更新用户自定义的关联数据
     *
     * @param customColumnRequest 自定义农办
     * @return 无返回
     */
    @Transactional(rollbackFor = Exception.class)
    public Flux<CustomColumn> updateAssociatedUser(CustomColumn customColumnRequest) {

        if (customColumnRequest.isNew() && customColumnRequest.getUserId() > 0
                && customColumnRequest.getTenantId() > 0) {
            return Flux.empty();
        }
        CustomColumnRequest searchRequest = new CustomColumnRequest();
        searchRequest.setMenuId(customColumnRequest.getMenuId());
        searchRequest.setSystem(customColumnRequest.getSystem());
        searchRequest.setCategory(customColumnRequest.getId());
        searchRequest.setIsDefault(false);
        return this.search(searchRequest, Pageable.ofSize(Integer.MAX_VALUE))
                .flatMap(old -> {
                    Long idOld = old.getId();
                    Integer tenantId = old.getTenantId();
                    Long userId = old.getUserId();
                    Boolean isShow = old.getIsShow();
                    String width = old.getWidth();
                    Boolean isFixed = old.getIsFixed();
                    Boolean isHide = old.getIsHide();
                    BeanUtils.copyProperties(customColumnRequest, old);
                    old.setId(idOld);
                    old.setIsShow(isShow);
                    old.setWidth(width);
                    old.setIsFixed(isFixed);
                    old.setIsHide(isHide);
                    old.setTenantId(tenantId);
                    old.setUserId(userId);
                    old.setCategory(customColumnRequest.getId());
                    return this.save(old);
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public Flux<CustomColumn> userOperation(List<CustomColumnRequest> requests) {
        return Flux.fromStream(requests.parallelStream())
                .flatMap(req -> {
                    CustomColumnRequest searchRequest = new CustomColumnRequest();
                    searchRequest.setMenuId(req.getMenuId());
                    searchRequest.setSystem(req.getSystem());
                    searchRequest.setCategory(req.getCategory());
                    searchRequest.setUserId(req.getUserId());
                    searchRequest.setTenantId(req.getTenantId());
                    searchRequest.setPropName(req.getPropName());
                    // 每次设置提交为新的,把模板列的id放入Category
                    req.setCategory(req.getId());
                    req.setId(null);
                    return this.search(searchRequest, Pageable.ofSize(1))
                            .last(req)
                            .flatMap(customColumn -> this.save(req.id(customColumn.getId())
                                    .category(customColumn.getCategory()).toCustomColumn()));
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> delete(Long id) {
        return this.customColumnRepository.deleteById(id)
                .zipWith(this.customColumnRepository.deleteByCategory(id))
                .then();
    }

    public Mono<CustomColumn> save(CustomColumn customColumn) {
        if (customColumn.isNew()) {
            return this.customColumnRepository.save(customColumn);
        } else {
            assert customColumn.getId() != null;
            return this.customColumnRepository.findById(customColumn.getId())
                    .flatMap(old -> {
                        customColumn.setCreatedTime(old.getCreatedTime());
                        return this.customColumnRepository.save(customColumn);
                    });
        }
    }
}