package com.platform.commons.utils;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * com.bootiful.commons.utils.CriteriaUtils
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 */
public class CriteriaUtils {

    /**
     * 序列化对象条件
     *
     * @param object 需要序列化得实体
     * @return 拼接后得Criteria
     */
    public static Criteria build(Object object) {
        return build(object, List.of());
    }

    /**
     * 序列化对象条件
     *
     * @param object  需要序列化得实体
     * @param skipKes 需要跳过得序列化字段
     * @return 拼接后得Criteria
     */
    public static Criteria build(Object object, List<String> skipKes) {
        Map<String, Object> objectMap = BeanUtil.beanToMap(object, false, true);
        skipKes.forEach(objectMap::remove);
        return build(objectMap);
    }

    /**
     * 序列化对象条件
     * <p>增加String类型,数字类型number</p>
     *
     * @param objectMap 需要序列化得Map对象,key为字段,value为值
     * @return 拼接后得Criteria
     */
    public static Criteria build(Map<String, Object> objectMap) {
        if (ObjectUtils.isEmpty(objectMap)) {
            return Criteria.empty();
        }
        List<Criteria> criteriaList = new ArrayList<>();
        objectMap.forEach((key, value) -> {
            if (value instanceof String) {
                criteriaList.add(Criteria.where(key).like(value + "%").ignoreCase(true));
            } else if (value instanceof Collection) {
                criteriaList.add(Criteria.where(key).in(value));
            } else {
                criteriaList.add(Criteria.where(key).is(value));
            }
        });
        return Criteria.from(criteriaList);
    }
}