package com.platform.oauth.security.group.member;

import com.platform.commons.utils.BaseAutoToolsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.oauth.security.group.MemberGroupManagerService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/4
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MemberGroupService extends BaseAutoToolsUtil {

    private final MemberGroupRepository memberRepository;

    public Mono<Page<MemberGroup>> page(MemberGroupRequest groupRequest, Pageable pageable) {
        return entityTemplate.select(Query.query(groupRequest.toCriteria()).with(pageable), MemberGroup.class)
                .collectList().zipWith(entityTemplate.count(Query.query(groupRequest.toCriteria()), MemberGroup.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<MemberGroup> operate(MemberGroupRequest request) {
        return Flux.fromStream(request.getUsers().parallelStream())
                .map(user -> MemberGroupRequest.of(request.getGroupCode(), user))
                .flatMap(this.memberRepository::save);
    }

    public Mono<Void> delete(Long id) {
        return this.memberRepository.deleteById(id);
    }


}