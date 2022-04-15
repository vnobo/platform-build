package com.platform.commons.utils;

import com.google.common.base.CaseFormat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.StringJoiner;

/**
 * com.bootiful.commons.utils.SqlUtils
 * <p>
 * 拼接SQL 分页 根据 {@link Pageable} 拼接, <code>order by table.id desc,table name asc</code>
 * </p>
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/7/29
 */
public class SqlUtils {

    public static String applyPage(Pageable pageable) {
        StringBuilder sqlBuilder = applySort(pageable.getSort(), new StringBuilder(" "));
        sqlBuilder.append(" offset ").append(pageable.getOffset())
                .append(" limit ").append(pageable.getPageSize());
        return sqlBuilder.toString();
    }

    public static StringBuilder applySort(Sort sort, StringBuilder sql) {
        if (sort == null || sort.isUnsorted()) {
            return sql.append(" ORDER BY id desc ");
        }
        sql.append(" ORDER BY ");
        StringJoiner sortSql = new StringJoiner(" , ");
        sort.iterator().forEachRemaining((o) -> {
            String sortedPropertyName = underLineToCamel(o.getProperty());
            String sortedProperty = o.isIgnoreCase() ? "LOWER(" + sortedPropertyName + ")"
                    : sortedPropertyName;
            sortSql.add(sortedProperty + (o.isAscending() ? " ASC" : " DESC"));
        });
        return sql.append(sortSql);
    }

    private static String underLineToCamel(String source) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, source);
    }
}