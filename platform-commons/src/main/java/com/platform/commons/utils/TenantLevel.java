package com.platform.commons.utils;

import com.platform.commons.annotation.RestServerException;
import org.springframework.lang.Nullable;

/**
 * com.homestead.statistics.utils.TenantLevel 租户等级,省,市,县
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/25
 */
public enum TenantLevel {
  /** 省级 */
  PROVINCE(2),
  /** 市级 */
  CITY(4),
  /** 县级 */
  AREA(6),

  /** 镇级 */
  STREET(9),

  /** 村级级 */
  VILLAGE(12);

  private static final TenantLevel[] VALUES;

  static {
    VALUES = values();
  }

  private final int value;

  TenantLevel(int value) {
    this.value = value;
  }

  public static TenantLevel valueOf(int statusCode) {
    TenantLevel status = resolve(statusCode);
    if (status == null) {
      throw RestServerException.withMsg("没有找到你要统计的租户级别 [" + statusCode + "]");
    }
    return status;
  }

  @Nullable
  public static TenantLevel resolve(int statusCode) {
    for (TenantLevel status : VALUES) {
      if (status.value == statusCode) {
        return status;
      }
    }
    return null;
  }

  public int value() {
    return this.value;
  }
}