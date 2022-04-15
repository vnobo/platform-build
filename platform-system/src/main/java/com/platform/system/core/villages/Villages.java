package com.platform.system.core.villages;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * com.bootiful.system.core.villages.Villages
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/12
 */
@Data
@Table("sys_villages")
public class Villages implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private Long code;
    private String name;
    private Long provinceCode;
    private Long cityCode;
    private Long areaCode;
    private Long streetCode;

    @Override
    public boolean isNew() {
        return ObjectUtils.isEmpty(this.id);
    }

    public String getCode() {
        return String.valueOf(code);
    }
}