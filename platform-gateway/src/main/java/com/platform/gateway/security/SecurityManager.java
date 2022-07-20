package com.platform.gateway.security;

import com.platform.commons.client.Oauth2Client;
import com.platform.commons.security.LoginSecurityDetails;
import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.commons.security.SimplerSecurityDetails;
import com.platform.gateway.client.AuthClient;
import com.platform.gateway.weixin.WxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.SecurityService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/4/25
 */
@Service
@RequiredArgsConstructor
public class SecurityManager implements ReactiveUserDetailsPasswordService {
    private final AuthClient authClient;
    private final Oauth2Client oauth2Client;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, String newPassword) {
        return this.authClient.changePassword(userDetails.getUsername(), userDetails.getPassword())
                .map(ReactiveSecurityHelper::buildUserDetails)
                .map(userDetails1 -> withNewPassword(userDetails1, newPassword));
    }

    private UserDetails withNewPassword(UserDetails userDetails, String newPassword) {
        // @formatter:off
        return org.springframework.security.core.userdetails.User.withUserDetails(userDetails)
                .password(newPassword)
                .build();
        // @formatter:on
    }


    public Mono<Authentication> login(LoginRequest loginRequest) {
        return this.oauth2Client.login(loginRequest.getUsername())
                .switchIfEmpty(register(loginRequest.toRegister()))
                .map(ReactiveSecurityHelper::buildUserDetails)
                .map(userDetail -> new UsernamePasswordAuthenticationToken(
                        userDetail, userDetail.getPassword(), userDetail.getAuthorities()));
    }

    public Mono<LoginSecurityDetails> register(RegisterRequest request) {
        request.setPassword(passwordEncoder.encode("A123456a"));
        return this.authClient.operate(request);
    }

    public Mono<SimplerSecurityDetails> tenantCut(TenantCutRequest cutRequest) {
        return this.authClient.tenantCut(cutRequest);
    }
}