package com.bootiful.gateway.security;

import com.bootiful.commons.client.CountryClient;
import com.bootiful.commons.security.SecurityTokenHelper;
import com.bootiful.commons.security.SimplerSecurityDetails;
import com.bootiful.gateway.client.AuthClient;
import com.bootiful.gateway.weixin.WxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.SecurityService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ReactiveSecurityManager implements ReactiveUserDetailsPasswordService {

    private final AuthClient authClient;
    private final CountryClient countryClient;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, String newPassword) {
        return this.authClient.changePassword(userDetails.getUsername(), userDetails.getPassword())
                .map(SecurityTokenHelper::buildUserDetails)
                .map(userDetails1 -> withNewPassword(userDetails1, newPassword));
    }

    private UserDetails withNewPassword(UserDetails userDetails, String newPassword) {
        // @formatter:off
        return org.springframework.security.core.userdetails.User.withUserDetails(userDetails)
                .password(newPassword)
                .build();
        // @formatter:on
    }

    public Mono<Authentication> winXinLogin(WxRequest wxRequest) {
        return this.countryClient.login(wxRequest.getPhone())
                .switchIfEmpty(authClient.userRegister(wxRequest.toRegister()))
                .map(SecurityTokenHelper::buildUserDetails)
                .map(userDetail -> new UsernamePasswordAuthenticationToken(userDetail,
                        userDetail.getPassword(), userDetail.getAuthorities()));
    }

    public Mono<Authentication> appLogin(LoginRequest loginRequest) {
        return this.countryClient.login(loginRequest.getPhone())
                .switchIfEmpty(authClient.userRegister(loginRequest.toRegister()))
                .map(SecurityTokenHelper::buildUserDetails)
                .map(userDetail -> new UsernamePasswordAuthenticationToken(userDetail,
                        userDetail.getPassword(), userDetail.getAuthorities()));
    }


    public Mono<SimplerSecurityDetails> tenantCut(TenantCutRequest cutRequest) {
        return this.authClient.tenantCut(cutRequest);
    }

}