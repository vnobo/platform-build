package com.platform.commons.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.server.WebSession;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.AuthenticationToken
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/31
 */
@Data
@Builder
public class AuthenticationToken implements Serializable {

    private String token;
    private Long expires;
    private Long lastAccessTime;

    public static AuthenticationToken build(WebSession session) {
        return AuthenticationToken.builder()
                .token(session.getId())
                .expires(session.getMaxIdleTime().getSeconds())
                .lastAccessTime(session.getLastAccessTime().getEpochSecond())
                .build();
    }
}