package com.platform.oauth.security.user;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * coupons in com.alex.web.model
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2019/7/14
 */
public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * get by username
     *
     * @param username user id
     * @return user model
     */
    @Query("SELECT * FROM se_users WHERE username ILIKE :username ")
    Mono<User> findByUsername(String username);

}