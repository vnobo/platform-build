package com.platform.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

/**
 * com.bootiful.oauth.OauthApplicationTest
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/30
 */
class OauthApplicationTest {


    @Test
    public void exampleTest() {
        PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();
        System.out.println(passwordEncoder.encode("q1w2e3.."));
    }
}