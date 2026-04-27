package com.huangch.cloud.utils.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.SneakyThrows;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author huangch
 * @since 2026-02-04
 */
@SuppressWarnings("UnusedReturnValue")
public class ServiceExtImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public static List<String> ignoreUpdateNames = new ArrayList<>();

    @Transactional(rollbackFor = Exception.class)
    public boolean fullSaveOrUpdateBatch(Collection<T> entityList) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
            sqlSession.getConnection();
            fullUpdateById(sqlSession, entity);
        });
    }

    @SneakyThrows
    private void fullUpdateById(SqlSession sqlSession, Object entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        String tableName = tableInfo.getTableName();
        String keyColumn = tableInfo.getKeyColumn();
        Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        List<Object> params = new ArrayList<>();

        for (TableFieldInfo fieldInfo : fieldList) {
            // 跳过不更新字段
            FieldFill fieldFill = fieldInfo.getFieldFill();
            if (FieldFill.INSERT.equals(fieldFill) || ignoreUpdateNames.contains(fieldInfo.getField().getName())) {
                continue;
            }
            Field field = fieldInfo.getField();
            field.setAccessible(true);
            Object value = field.get(entity);
            sql.append(fieldInfo.getColumn()).append(" = ?,");
            params.add(value);
        }

        // 去掉最后一个逗号
        sql.deleteCharAt(sql.length() - 1);

        sql.append(" WHERE ").append(keyColumn).append(" = ?");
        params.add(idVal);

        Connection conn = sqlSession.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.executeUpdate();
        }
    }
}
