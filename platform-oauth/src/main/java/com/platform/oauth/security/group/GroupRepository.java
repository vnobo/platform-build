package com.platform.oauth.security.group;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * com.alex.oauth.security.GroupAuthorityRepository
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/20
 */
public interface GroupRepository extends R2dbcRepository<Group, Integer> {

  /**
   * 根据类型查找
   *
   * @param tenantCode 类型
   * @param name 类型
   * @param type 类型
   * @param system 类型
   * @return 角色组
   */
  @Query(
      "select * from se_groups where tenant_code=:tenantCode and name ilike :name "
          + "and type=:type and system=:system ")
  Mono<Group> findByPids(String tenantCode, String name, Integer type, String system);
}