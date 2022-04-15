package com.bootiful.system.core.notice;

import com.bootiful.commons.annotation.UserAuditor;
import com.bootiful.commons.client.CountryClient;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.bootiful.commons.utils.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author jjh
 * @date 2021/6/7
 */
@Service
@RequiredArgsConstructor
public class NoticeService extends BaseAutoToolsUtil {

    private final NoticeRepository noticeRepository;
    private final CountryClient countryClient;

    public Mono<Page<NoticeOnly>> page(NoticeSearch noticeSearch, Pageable pageable) {
        return search(noticeSearch, pageable).collectList()
                .zipWith(super.entityTemplate.count(Query.query(noticeSearch.toCriteria()), Notice.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<NoticeOnly> search(NoticeSearch noticeSearch, Pageable pageable) {
        return super.entityTemplate.select(Query.query(noticeSearch.toCriteria()).with(pageable), Notice.class)
                .flatMap(this::serializeOnly);
    }

    private Mono<NoticeOnly> serializeOnly(Notice news) {
        return Mono.zipDelayError(this.countryClient.loadUser(getUserIdNoNull(news.getCreator()))
                                .defaultIfEmpty(User.withId(getUserIdNoNull(news.getCreator()))),
                        this.countryClient.loadUser(getUserIdNoNull(news.getUpdater()))
                                .defaultIfEmpty(User.withId(getUserIdNoNull(news.getUpdater()))))
                .map(tuple3 -> {
                    news.setCreator(UserAuditor.withUser(tuple3.getT1()));
                    news.setUpdater(UserAuditor.withUser(tuple3.getT2()));
                    return NoticeOnly.withNotice(news);
                });
    }

    private Long getUserIdNoNull(UserAuditor auditor) {
        if (ObjectUtils.isEmpty(auditor)) {
            return null;
        }
        return auditor.getUserId();
    }

    public Mono<Notice> operation(NoticeRequest request) {
        return this.save(request.toNotice());
    }


    private Mono<Notice> save(Notice notice) {
        if (notice.isNew()) {
            return this.noticeRepository.save(notice);
        } else {
            assert notice.getId() != null;
            return this.noticeRepository.findById(notice.getId()).flatMap(old -> {
                notice.setCreatedTime(old.getCreatedTime());
                return this.noticeRepository.save(notice);
            });
        }
    }

    public Mono<Void> delete(NoticeRequest newsRequest) {
        Assert.notNull(newsRequest.getId(), "删除通告[id]不能为空!");
        Assert.notNull(newsRequest.getSystem(), "删除通告[system]不能为空!");
        return this.noticeRepository.findById(newsRequest.getId())
                .flatMap(old -> {
                    String[] oldSystem = StringUtils.commaDelimitedListToStringArray(old.getSystem());
                    String[] newSystem = StringUtils.commaDelimitedListToStringArray(newsRequest.getSystem());
                    oldSystem = Arrays.stream(oldSystem).filter(system -> Arrays.stream(newSystem).noneMatch(d -> d.equals(system)))
                            .toArray(String[]::new);
                    if (ObjectUtils.isEmpty(oldSystem) || oldSystem.length == 0) {
                        return this.noticeRepository.delete(old);
                    }
                    old.setSystem(StringUtils.arrayToCommaDelimitedString(oldSystem));
                    return this.noticeRepository.save(old);
                }).then();
    }

    public Mono<Void> deleteBatch(List<NoticeRequest> requests) {
        return Flux.fromStream(requests.parallelStream())
                .flatMap(this::delete).then();
    }
}