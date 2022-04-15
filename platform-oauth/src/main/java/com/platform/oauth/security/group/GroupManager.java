package com.platform.oauth.security.group;

import com.platform.commons.utils.BaseAutoToolsUtil;
import com.platform.oauth.security.group.authority.AuthorityGroup;
import com.platform.oauth.security.group.authority.AuthorityGroupManger;
import com.platform.oauth.security.group.authority.AuthorityGroupRequest;
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

/**
 * com.bootiful.oauth.security.group.GroupService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/6/3
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class GroupManager extends BaseAutoToolsUtil {

    private final GroupRepository groupRepository;
    private final AuthorityGroupManger authorityGroupManger;

    public Mono<GroupOnly> loadGroupOnly(Integer groupId) {
        Mono<Group> source = this.groupRepository.findById(groupId);
        return source.flatMap(this::integrateAuthorities);
    }

    public Flux<GroupOnly> search(GroupRequest groupRequest, Pageable pageable) {
        log.debug("查询Group request: {}", groupRequest);
        return super.entityTemplate.select(Query.query(groupRequest.toCriteria()).with(pageable), Group.class)
                .flatMapSequential(this::integrateAuthorities);
    }

    public Mono<Page<GroupOnly>> page(GroupRequest groupRequest, Pageable pageable) {
        return this.search(groupRequest, pageable).collectList()
                .zipWith(super.entityTemplate.count(Query.query(groupRequest.toCriteria()), Group.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable,
                        entityTuples.getT2()));
    }

    private Mono<GroupOnly> integrateAuthorities(Group group) {
        return this.loadAuthorities(AuthorityGroupRequest.withGroupId(group.getId()))
                .map(AuthorityGroup::getAuthority).collectList()
                .map(entityTuples -> GroupOnly.withGroup(group).authorities(entityTuples));
    }

    public Mono<Group> add(GroupRequest groupRequest) {
        return this.groupRepository.findByPids(groupRequest.getTenantCode(), groupRequest.getName(),
                        groupRequest.getType(), groupRequest.getSystem().name())
                .switchIfEmpty(this.operation(groupRequest));
    }

    public Mono<Group> operation(GroupRequest groupRequest) {
        return this.groupRepository.findByPids(groupRequest.getTenantCode(), groupRequest.getName(),
                        groupRequest.getType(), groupRequest.getSystem().name())
                .flatMap(old -> this.save(groupRequest.id(old.getId()).toGroup()))
                .switchIfEmpty(this.save(groupRequest.toGroup()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> delete(Integer id) {
        return this.groupRepository.deleteById(id)
                .delayUntil(res -> this.authorityGroupManger.delete(id));
    }

    public Mono<Group> save(Group group) {
        if (group.isNew()) {
            return this.groupRepository.save(group);
        } else {
            assert group.getId() != null;
            return this.groupRepository.findById(group.getId())
                    .flatMap(old -> {
                        group.setCreatedTime(old.getCreatedTime());
                        return this.groupRepository.save(group);
                    });
        }
    }

    public Flux<AuthorityGroup> loadAuthorities(AuthorityGroupRequest request) {
        return this.authorityGroupManger.search(request);
    }

    public Flux<AuthorityGroup> authorizing(Integer groupId, AuthorityGroupRequest authorityList) {
        return this.authorityGroupManger.authorizing(groupId, authorityList)
                .doOnNext(result -> log.debug("角色组:{},授权[{}]成功!", groupId, result.getAuthority()));
    }

}