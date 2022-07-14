package com.platform.oauth.security.group;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * com.alex.oauth.security.GroupAuthorityRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/20
 */
public interface GroupRepository extends R2dbcRepository<Group, Integer> {

}