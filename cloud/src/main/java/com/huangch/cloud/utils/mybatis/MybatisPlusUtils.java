package com.huangch.cloud.utils.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author huangch
 * @since 2026-04-03
 */
public class MybatisPlusUtils {

    /**
     * <p>构建 EXISTS 子查询条件，用于主表与子表的关联查询</p>
     *
     * <p>该方法通过 Lambda 表达式构建跨表 EXISTS 查询，支持自定义子查询条件。
     * 自动处理表名映射、字段名映射以及 SQL 参数替换，生成标准的 EXISTS 子句。</p>
     *
     * @param <T>          主查询实体类型
     * @param <M>          主表实体类型
     * @param <S>          子表实体类型
     * @param wrapper      主查询的 QueryWrapper 对象，用于附加 EXISTS 条件
     * @param subClass     子表实体的 Class 对象
     * @param subField     子表关联字段的 Lambda 引用，用于建立主表与子表的连接关系
     * @param mainClass    主表实体的 Class 对象
     * @param mainField    主表关联字段的 Lambda 引用，用于建立主表与子表的连接关系
     * @param subCondition 子查询条件构造器，通过 Consumer 函数式接口自定义 WHERE 条件
     */
    public static <T, M, S> void exists(
            QueryWrapper<T> wrapper,
            Class<S> subClass,
            SFunction<S, ?> subField,
            Class<M> mainClass,
            SFunction<M, ?> mainField,
            Consumer<LambdaQueryWrapper<S>> subCondition) {
        // ===== 1. 表名 =====
        TableInfo mainTable = TableInfoHelper.getTableInfo(mainClass);
        TableInfo subTable = TableInfoHelper.getTableInfo(subClass);

        String mainTableName = mainTable.getTableName();
        String subTableName = subTable.getTableName();

        // ===== 2. 字段名 =====
        String mainColumn = getColumn(mainClass, mainField);
        String subColumn = getColumn(subClass, subField);

        // ===== 3. 子查询条件 =====
        LambdaQueryWrapper<S> subWrapper = new LambdaQueryWrapper<>();
        subCondition.accept(subWrapper);

        String customSqlSegment = subWrapper.getCustomSqlSegment();
        Map<String, Object> paramMap = subWrapper.getParamNameValuePairs();

        // 把 #{ew.paramNameValuePairs.xxx} → {0} 占位符
        String parsedSql = customSqlSegment;
        System.out.println("---" + parsedSql);
        Object[] params = paramMap.values().toArray();

        int i = 0;
        for (String key : paramMap.keySet()) {
            parsedSql = parsedSql.replace("#{ew.paramNameValuePairs." + key + "}", "{" + i++ + "}");
        }
        if (parsedSql != null && parsedSql.trim().startsWith("WHERE")) {
            parsedSql = parsedSql.replaceFirst("(?i)WHERE", "AND");
        }
        // ===== 4. 拼 EXISTS =====
        String sql = String.format(
                "SELECT 1 FROM %s WHERE %s.%s = %s.%s %s",
                subTableName,
                subTableName,
                subColumn,
                mainTableName,
                mainColumn,
                parsedSql
        );

        wrapper.exists(sql, params);
    }

    /**
     * <p>根据实体类和字段引用获取对应的数据库列名</p>
     *
     * <p>该方法通过解析 Lambda 表达式的字段引用，将 Java 属性名转换为数据库列名。
     * 支持主键字段和普通字段的识别，自动处理 getter/is 方法前缀，并遵循驼峰转下划线的命名规范。</p>
     *
     * @param clazz 实体类的 Class 对象，用于获取表结构信息
     * @param fn    MyBatis-Plus 的 SFunction 字段引用，通常为 Lambda 表达式如 User::getId
     * @return 返回对应的数据库列名，如果未找到匹配则返回转换后的字段名
     */
    private static String getColumn(Class<?> clazz, SFunction<?, ?> fn) {
        String fieldName = LambdaUtils.extract(fn).getImplMethodName();

        // getXxx → xxx
        if (fieldName.startsWith("get")) {
            fieldName = fieldName.substring(3);
        } else if (fieldName.startsWith("is")) {
            fieldName = fieldName.substring(2);
        }

        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (fieldName.equals(tableInfo.getKeyProperty())) {
            return tableInfo.getKeyColumn();
        }

        for (TableFieldInfo field : tableInfo.getFieldList()) {
            if (field.getProperty().equals(fieldName)) {
                return field.getColumn();
            }
        }
        return fieldName;
    }
}
