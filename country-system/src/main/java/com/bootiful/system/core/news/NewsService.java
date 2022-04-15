package com.bootiful.system.core.news;

import com.bootiful.commons.annotation.RestServerException;
import com.bootiful.commons.annotation.UserAuditor;
import com.bootiful.commons.client.CountryClient;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.bootiful.commons.utils.SystemType;
import com.bootiful.commons.utils.User;
import com.bootiful.system.core.configurations.ConfigurationRequest;
import com.bootiful.system.core.configurations.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2021/6/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService extends BaseAutoToolsUtil {

    private final NewsRepository newsRepository;
    private final ConfigurationService configurationService;
    private final CountryClient countryClient;

    public Mono<Page<NewsOnly>> page(NewsSearch newsSearch, Pageable pageable) {
        return search(newsSearch, pageable)
                .collectList()
                .zipWith(super.entityTemplate.count(Query.query(newsSearch.toCriteria()), News.class))
                .map(entityTuples -> new PageImpl<>(entityTuples.getT1(), pageable, entityTuples.getT2()));
    }

    public Flux<NewsOnly> search(NewsSearch newsSearch, Pageable pageable) {
        return super.entityTemplate.select(Query.query(newsSearch.toCriteria()).with(pageable), News.class)
                .flatMapSequential(this::serializeOnly);
    }

    private Mono<NewsOnly> serializeOnly(News news) {
        return Mono.zipDelayError(this.loadUser(news.getCreator()),
                        this.loadUser(news.getAuditor()), this.loadUser(news.getUpdater()))
                .map(tuple3 -> {
                    news.setCreator(tuple3.getT1());
                    news.setAuditor(tuple3.getT2());
                    news.setUpdater(tuple3.getT3());
                    return NewsOnly.withNews(news);
                });
    }

    public Mono<UserAuditor> loadUser(UserAuditor auditor) {

        if (ObjectUtils.isEmpty(auditor)) {
            return Mono.just(UserAuditor.withUserId(0L));
        }

        Long userId = auditor.getUserId();
        if (ObjectUtils.isEmpty(userId)) {
            return Mono.just(UserAuditor.withUserId(0L));
        }
        User user = User.builder().id(userId).build();
        return this.countryClient.loadUsers(user).last(user).map(UserAuditor::withUser);
    }

    public Mono<News> operation(NewsRequest newsRequest) {
        String auditKey = "audit";
        String newsKey = "news";
        return configurationService.search(ConfigurationRequest.builder().tenantId(newsRequest.getTenantId())
                        .type(newsKey).system(SystemType.country).build())
                .singleOrEmpty().switchIfEmpty(this.configurationService.operation(ConfigurationRequest.builder()
                        .configuration(this.objectMapper.createObjectNode().put(auditKey, false))
                        .name("新闻是否需要审核").type(newsKey).tenantId(newsRequest.getTenantId())
                        .tenantCode(newsRequest.getTenantCode()).system(SystemType.country).build()))
                .flatMap(cg -> {
                    if (!cg.getConfiguration().get(auditKey).asBoolean()) {
                        newsRequest.setStatus("03");
                    }
                    return this.save(newsRequest.toNews());
                });
    }

    public Mono<News> save(News news) {
        if (news.isNew()) {
            return this.newsRepository.save(news);
        } else {
            assert news.getId() != null;
            return this.newsRepository.findById(news.getId())
                    .flatMap(old -> {
                        news.setId(news.getId());
                        news.setCreator(old.getCreator());
                        news.setCreatedTime(old.getCreatedTime());
                        news.setSource(old.getSource());
                        return this.newsRepository.save(news);
                    });
        }
    }

    public Mono<News> audit(NewsRequest newsRequest) {
        Assert.notNull(newsRequest.getId(), "审核的新闻ID不能为空!");
        return this.newsRepository.findById(newsRequest.getId()).flatMap(old -> {
            old.setStatus(newsRequest.getStatus());
            old.setAuditor(newsRequest.getAuditor());
            old.setAuditTime(newsRequest.getAuditTime());
            return this.newsRepository.save(old);
        }).switchIfEmpty(Mono.error(RestServerException.withMsg("没有找到id为" + newsRequest.getId() + "的新闻信息!")));
    }

    public Mono<Void> delete(NewsRequest newsRequest) {
        Assert.notNull(newsRequest.getId(), "删除新闻ID不能为空!");
        Assert.notNull(newsRequest.getSystem(), "删除新闻系统类型不能为空!");
        return this.newsRepository.findById(newsRequest.getId())
                .flatMap(old -> {
                    String[] oldSystem = StringUtils.commaDelimitedListToStringArray(old.getSystem());
                    String[] newSystem = StringUtils.commaDelimitedListToStringArray(newsRequest.getSystem());
                    oldSystem = Arrays.stream(oldSystem)
                            .filter(system -> Arrays.stream(newSystem).noneMatch(d -> d.equals(system)))
                            .toArray(String[]::new);
                    if (ObjectUtils.isEmpty(oldSystem) || oldSystem.length == 0) {
                        return this.newsRepository.delete(old);
                    }
                    old.setSystem(StringUtils.arrayToCommaDelimitedString(oldSystem));
                    return this.newsRepository.save(old);
                }).then();
    }

    public Mono<Void> deleteBatch(List<NewsRequest> requests) {
        return Flux.fromStream(requests.parallelStream())
                .flatMap(this::delete).then();
    }
}