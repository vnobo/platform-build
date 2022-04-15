package com.platform.commons.security;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.function.Function;

/**
 * com.bootiful.commons.security.ReactiveSecurityDetailsHolder
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/10
 */
public final class ReactiveSecurityDetailsHolder {

    private static final Class<?> SECURITY_DETAILS_CONTEXT_KEY = SecurityDetails.class;

    private ReactiveSecurityDetailsHolder() {
    }

    public static Mono<SecurityDetails> getContext() {
        // @formatter:off
        return Mono.deferContextual(Mono::just)
                .filter(ReactiveSecurityDetailsHolder::hasSecurityContext)
                .flatMap(ReactiveSecurityDetailsHolder::getSecurityContext);
        // @formatter:on
    }

    private static boolean hasSecurityContext(ContextView context) {
        return context.hasKey(SECURITY_DETAILS_CONTEXT_KEY);
    }

    private static Mono<SecurityDetails> getSecurityContext(ContextView context) {
        return context.<Mono<SecurityDetails>>get(SECURITY_DETAILS_CONTEXT_KEY);
    }

    public static Function<Context, Context> clearContext() {
        return (context) -> context.delete(SECURITY_DETAILS_CONTEXT_KEY);
    }

    public static ContextView withSecurityDetailsContext(Mono<SecurityDetails> securityDetails) {
        return Context.of(SECURITY_DETAILS_CONTEXT_KEY, securityDetails);
    }

    public static ContextView withSecurityDetails(SecurityDetails securityUserDetails) {
        return withSecurityDetailsContext(Mono.just(securityUserDetails));
    }

}