package com.platform.commons.security;

import com.platform.commons.client.CountryClient;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.SecurityService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
public class ReactiveSimplerUserDetailsService implements ReactiveUserDetailsService {

    private final CountryClient oauthClient;

    public ReactiveSimplerUserDetailsService(CountryClient oauthClient) {
        this.oauthClient = oauthClient;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return this.oauthClient.login(username)
                .map(ReactiveSecurityHelper::buildUserDetails)
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(new AuthenticationServiceException(
                        throwable.getLocalizedMessage(), throwable))));
    }
}