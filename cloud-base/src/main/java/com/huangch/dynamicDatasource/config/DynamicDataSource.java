package com.huangch.dynamicDatasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author huangch
 * @date 2023-08-01
 */
@SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
public class DynamicDataSource extends AbstractRoutingDataSource {

    public static final ThreadLocal<String> name = new ThreadLocal<>();

    private final DataSource defaultDataSource;
    private final Map<Object, Object> targetDataSources;

    public DynamicDataSource(DataSource defaultDataSource, Map<Object, Object> targetDataSources) {
        this.defaultDataSource = defaultDataSource;
        this.targetDataSources = targetDataSources;
        super.setLenientFallback(false);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return name.get();
    }

    @Override
    public void afterPropertiesSet() {
        // 设置目标数据源
        super.setTargetDataSources(targetDataSources);
        // 设置默认数据源
        super.setDefaultTargetDataSource(defaultDataSource);
        super.afterPropertiesSet();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
    }
}
