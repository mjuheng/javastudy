package com.huangch.dynamicDatasource.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * @author huangch
 * @date 2023-08-04
 */
@Configuration
public class MybatisPlusConfig {


    @Bean("sqlSessionFactory")
    @Primary
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DynamicDataSource dynamicDataSource) throws IOException {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dynamicDataSource);
        sessionFactoryBean.setTransactionFactory(new MultiDataSourceTransactionFactory());
        //设置我们的xml文件路径
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
                "classpath*:mapper/*.xml"));
        return sessionFactoryBean;
    }
}
