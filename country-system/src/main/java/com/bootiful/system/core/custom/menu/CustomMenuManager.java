package com.bootiful.system.core.custom.menu;

import com.bootiful.commons.utils.BaseAutoToolsUtil;
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

import java.util.List;

/**
 * com.bootiful.system.core.menu.CustomMenuManager
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/5
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomMenuManager extends BaseAutoToolsUtil {

    private final CustomMenuRepository customMenuRepository;

    public Flux<CustomMenu> search(CustomMenuRequest tenantRequest, Pageable pageable) {
        return super.entityTemplate.select(CustomMenu.class)
                .matching(Query.query(tenantRequest.toCriteria()).with(pageable))
                .all();
    }

    public Mono<Page<CustomMenu>> page(CustomMenuRequest tenantRequest, Pageable pageable) {
        return super.entityTemplate.select(CustomMenu.class)
                .matching(Query.query(tenantRequest.toCriteria()).with(pageable))
                .all().collectList()
                .zipWith(super.entityTemplate.count(Query.query(tenantRequest.toCriteria()), CustomMenu.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<CustomMenu> batch(List<CustomMenuRequest> requests) {
        return Flux.fromStream(requests.parallelStream())
                .flatMap(request -> this.customMenuRepository.findByMenuIdAndUserId(request.getMenuId(), request.getUserId())
                        .map(customMenu -> request.id(customMenu.getId()))
                        .defaultIfEmpty(request))
                .flatMap(request -> this.save(request.toCustomMenu()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<CustomMenu> operation(CustomMenuRequest request) {
        return this.save(request.toCustomMenu());
    }

    public Mono<CustomMenu> save(CustomMenu customMenu) {
        if (customMenu.isNew()) {
            return this.customMenuRepository.save(customMenu);
        } else {
            assert customMenu.getId() != null;
            return this.customMenuRepository.findById(customMenu.getId())
                    .flatMap(old -> {
                        customMenu.setCreatedTime(old.getCreatedTime());
                        return this.customMenuRepository.save(customMenu);
                    });
        }
    }

    public Mono<Void> delete(Long id) {
        return this.customMenuRepository.deleteById(id);
    }

}