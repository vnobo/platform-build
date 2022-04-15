package com.platform.commons.security;

import lombok.Data;

import java.io.Serializable;

/**
 * com.bootiful.commons.security.SimplerSecurityDetails
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/10
 */
@Data
public class LoginSecurityDetails implements Serializable {

    private String username;
    private String password;
    private Boolean enabled;
    private String[] authorities;

    public static LoginSecurityDetails of(String username, String password, Boolean enabled) {

        LoginSecurityDetails securityDetails = new LoginSecurityDetails();
        securityDetails.setUsername(username);
        securityDetails.setPassword(password);
        securityDetails.setEnabled(enabled);
        return securityDetails;

    }

    public LoginSecurityDetails authorities(String[] authorities) {
        this.setAuthorities(authorities);
        return this;
    }


}