package com.bootiful.oauth.utils;

import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.bootiful.commons.utils.SystemType;
import com.bootiful.oauth.client.SystemClient;
import com.bootiful.oauth.security.group.Group;
import com.bootiful.oauth.security.group.GroupManager;
import com.bootiful.oauth.security.group.GroupRequest;
import com.bootiful.oauth.security.group.authority.AuthorityGroupRequest;
import com.bootiful.oauth.security.tenant.Tenant;
import com.bootiful.oauth.security.tenant.TenantManager;
import com.bootiful.oauth.security.tenant.TenantRequest;
import com.bootiful.oauth.security.user.User;
import com.bootiful.oauth.security.user.UserManager;
import com.bootiful.oauth.security.user.UserRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.ContextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * com.bootiful.oauth.utils.InitializingTenantUtils
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/28
 */
@Log4j2
@Service
public class InitializingTenantUtils extends BaseAutoToolsUtil {

    private final WebClient webClient;
    private final SystemClient systemClient;
    private final TenantManager tenantManager;
    private final GroupManager groupManager;
    private final UserManager userManager;
    Sinks.Many<VillagesData> hotSource = Sinks.unsafe().many().multicast().directBestEffort();
    Flux<VillagesData> hotFlux = hotSource.asFlux();

    public InitializingTenantUtils(WebClient.Builder builder, SystemClient systemClient, TenantManager tenantManager,
                                   GroupManager groupManager, UserManager userManager) {
        webClient = builder.codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                .maxInMemorySize(1024 * 1024 * 1024)).build();
        this.systemClient = systemClient;
        this.tenantManager = tenantManager;
        this.groupManager = groupManager;
        this.userManager = userManager;
    }

    /**
     * 初始化租户
     *
     * @param code 初始化租户省份code
     * @return 省份初始化返回
     */
    public Flux<Tenant> initializing(String code) {
        return this.getResource().map(bytes -> {
                    try {
                        return objectMapper.readValue(bytes, JsonNode.class);
                    } catch (IOException e) {
                        return objectMapper.createObjectNode();
                    }
                })
                .flatMapMany(jsonNode -> Flux.fromStream(StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(jsonNode.elements(), Spliterator.ORDERED),
                        true)))
                .filter(item -> item.get("code").asText().equals(code)).flatMap(item -> {
                    List<String> codes = new ArrayList<>();
                    codes.add(item.get("code").asText());
                    List<String> addresses = new ArrayList<>();
                    addresses.add(item.get("name").asText());
                    return this.initializingTenant(VillagesData.builder().codes(codes).addresses(addresses)
                                    .pTenant(TenantRequest.withId(0)).item(item).build())
                            .publishOn(Schedulers.boundedElastic())
                            .transformDeferredContextual((tenantMono, contextView) -> tenantMono
                                    .doOnNext(tenant -> this.recursiveInitialization(tenant,
                                            item.withArray("children"), contextView)));
                });
    }

    /**
     * 递归初始化租户信息
     *
     * @param pTenant     初始化父级组化
     * @param childNode   父级包含的子租户
     * @param contextView 会话上下文
     */
    private void recursiveInitialization(Tenant pTenant, ArrayNode childNode, ContextView contextView) {
        hotFlux.flatMap(this::initializingTenant)
                .subscribe(result -> log.info("村村村村村村村村村: id:{}, name: {}, code: {}",
                        result.getId(), result.getName(), result.getCode()));
        this.recursiveExpand(pTenant, childNode).delayUntil(this::recursiveVillages).contextWrite(contextView)
                .subscribe(result -> log.info("市,区,镇 id:{}, name: {}, code: {}",
                        result.getId(), result.getName(), result.getCode()));
    }

    /**
     * 递归循环查询创建租户
     *
     * @param pTenant   父级租户
     * @param childNode 子集租户
     * @return 创建后的租户
     */
    private Flux<Tenant> recursiveExpand(Tenant pTenant, ArrayNode childNode) {
        return Flux.fromStream(StreamSupport.stream(Spliterators.spliteratorUnknownSize(childNode.iterator(),
                Spliterator.ORDERED), true)).flatMap(item -> {
            List<String> codes = objectMapper.convertValue(pTenant.getExtend().get("addressCode"),
                    new TypeReference<>() {
                    });
            codes.add(item.get("code").asText());
            List<String> addresses = objectMapper.convertValue(pTenant.getExtend().get("addressText"),
                    new TypeReference<>() {
                    });
            addresses.add(item.get("name").asText());
            return this.initializingTenant(VillagesData.builder().codes(codes).addresses(addresses)
                            .pTenant(pTenant).item(item).build())
                    .flatMapMany(tenant -> this.recursiveExpand(tenant, item.withArray("children")));
        });
    }

    /**
     * 初始化村庄租户
     *
     * @param pTenant 父级租户
     * @return 自动初始化 无返回
     */
    private Mono<Void> recursiveVillages(Tenant pTenant) {
        return this.systemClient.villages(pTenant.getCode()).doOnNext(item -> {
            List<String> codes = objectMapper.convertValue(pTenant.getExtend().get("addressCode"),
                    new TypeReference<>() {
                    });
            codes.add(item.get("code").asText());
            List<String> addresses = objectMapper.convertValue(pTenant.getExtend().get("addressText"),
                    new TypeReference<>() {
                    });
            addresses.add(item.get("name").asText());
            this.hotSource
                    .tryEmitNext(VillagesData.builder().codes(codes)
                            .addresses(addresses).pTenant(pTenant).item(item).build());
        }).then();
    }

    /**
     * 创建租户,存在修改
     * <p>
     * addressCode 地址编码
     * address     租户地址
     * pid         租户父级ID
     * item        租户信息
     *
     * @param data 序列化数据
     * @return 创建后的租户
     */
    private Mono<Tenant> initializingTenant(VillagesData data) {
        TenantRequest tenantRequest = new TenantRequest();
        tenantRequest.setCode(data.getItem().get("code").asText());
        tenantRequest.setName(data.getItem().get("name").asText());
        tenantRequest.setDescription("系统默认初始化租户");
        tenantRequest.setPid(data.pTenant.getId());
        tenantRequest.setAddress(StringUtils.collectionToCommaDelimitedString(data.getAddresses()));
        ObjectNode objectNode = this.objectMapper.createObjectNode();
        objectNode.putPOJO("addressCode", data.getCodes());
        objectNode.putPOJO("addressText", data.getAddresses());
        tenantRequest.setExtend(objectNode);
        return this.tenantManager.add(tenantRequest);
    }

    /**
     * 创建租户默认的管理员角色组
     *
     * @param tenant 租户信息
     * @return 创建管理员角色组
     */
    public Mono<Group> initializingGroup(Tenant tenant) {
        return this.groupManager.add(GroupRequest.of(tenant.getId(), tenant.getCode(),
                tenant.getName() + "系统管理组", "初始化租户的时候,系统自动创建的默认管理组.",
                1, SystemType.country)).transformDeferredContextual((groupMono, contextView) -> {
            SystemType systemType = contextView.get("system");
            return groupMono.publishOn(Schedulers.boundedElastic()).delayUntil(group ->
                    this.systemClient.authorities(systemType.name()).flatMap(jsonNode -> {
                        List<String> authorities = new ArrayList<>(List.of(jsonNode.get("authority").asText()));
                        jsonNode.withArray("permissions").forEach(node -> authorities.add(node.get("permission").asText()));
                        return this.groupManager.authorizing(group.getId(), AuthorityGroupRequest
                                .withAuthorities(authorities.parallelStream().distinct().collect(Collectors.toList())));
                    }));
        }).delayUntil(this::initializingUser);
    }

    /**
     * 创建默认的管理员,默认账号: 租户code,默认密码: A123456a
     *
     * @param group 默认管理员角色组
     * @return 创建的后的管理员
     */
    public Mono<User> initializingUser(Group group) {
        return this.userManager.register(UserRequest.builder()
                .username(org.apache.commons.lang3.StringUtils.rightPad(group.getTenantCode(), 12, "0"))
                .password("A123456a").enabled(true).tenantCode(group.getTenantCode())
                .tenantId(group.getTenantId()).groupId(group.getId()).build());
    }


    /**
     * 获取租户JSON国标地区定义
     *
     * @return 返回文件内容
     */
    private Mono<byte[]> getResource() {
        return this.webClient.get().uri(uriBuilder -> uriBuilder.scheme("https").host("static.jinqiruanjian.com")
                        .path("/countryside/config/pcas-code.json").build())
                .retrieve().bodyToMono(byte[].class);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class VillagesData {
        private List<String> codes;
        private List<String> addresses;
        private Tenant pTenant;
        private JsonNode item;
    }
}