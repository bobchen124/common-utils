package com.bob.pn.ds;

import com.bob.pn.ds.tool.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

/**
 * @author chenbo@guworks.cc
 * @title mybatis配置
 * @date 2018年12月04日
 * @since v1.0.0
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({MybatisProperties.class})
@MapperScan(basePackages = {"com.bob.pn.ds.mapper"})
public class MybatisConfig implements ResourceLoaderAware {

    @Autowired
    private MybatisProperties mybatisProperties;

    @Autowired
    private Environment environment;

    ResourceLoader resourceLoader;

    @Bean
    public DynamicDataSource workDynamicDataSource() {
        return DynamicDataSourceBuilder.create().environment(environment).prefix("jdbc.campus").build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        if (StringUtils.hasLength(mybatisProperties.getTypeAliasesPackage())) {
            // 指定基包
            sessionFactoryBean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        }

        sessionFactoryBean.setMapperLocations(mybatisProperties.resolveMapperLocations());
        sessionFactoryBean.setConfigLocation(resourceLoader.getResource("classpath:mybatis.xml"));

        return sessionFactoryBean.getObject();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
