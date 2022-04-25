package com.platform.commons.annotation;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.commons.security.CreatorsSecurityAuditorAware
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/6/24
 */
public class ReactiveUserAuditorAware implements ReactiveAuditorAware<UserAuditor> {

  @Override
  public Mono<UserAuditor> getCurrentAuditor() {
    return ReactiveSecurityDetailsHolder.getContext().map(UserAuditor::withSecurityDetails);
  }

}