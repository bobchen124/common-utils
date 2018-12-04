package com.bob.pn.ds;

import com.bob.pn.ds.tool.DynamicDataSource;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenbo@guworks.cc
 * @title 动态数据源创建
 * @date 2018年12月03日
 * @since v1.0.0
 */
public class DynamicDataSourceBuilder {

    /**
     * 默认数据源
     */
    private static final Object DEFAULT_DS_TYPE = "com.alibaba.druid.pool.DruidDataSource";

    /**
     * 默认配置前缀
     */
    private static final String DEFAULT_PREFIX = "jdbc.ds";

    private ConversionService conversionService = new DefaultConversionService();

    /**
     * 数据源属性配置
     */
    private PropertyValues dataSourcePropertyValues;

    private String defaultDataSourceName;
    private DataSource defaultDataSource;
    private Map<String, DataSource> customDataSources = new HashMap<>();

    private Environment environment;

    /**
     * 数据源前缀
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * 主数据源
     */
    private String master = "master";

    /**
     * 多从数据源
     */
    private String[] slaves = {"slave", "slave2"};

    private DynamicDataSourceBuilder() {

    }

    public static DynamicDataSourceBuilder create() {
        return new DynamicDataSourceBuilder();
    }

    /**
     * 设置环境参数
     * @param environment 环境参数
     * @return builder
     */
    public DynamicDataSourceBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * 设置key前缀
     * @param prefix 前缀
     * @return builder
     */
    public DynamicDataSourceBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * 置主库标志，默认是：master
     * @param master 主库标志
     * @return builder
     */
    public DynamicDataSourceBuilder master(String master) {
        this.master = master;
        return this;
    }

    /**
     * 设置从库标识，默认是：slave
     * @param slaves 从库标识
     * @return builder
     */
    public DynamicDataSourceBuilder slaves(String... slaves) {
        this.slaves = slaves;
        return this;
    }

    /**
     * 创建动态数据源
     * @return 动态数据源
     */
    public DynamicDataSource build() {
        this.initDataSourceName(environment);
        this.initDefaultDataSource(environment);
        this.initCustomDataSources(environment);

        //注册DynamicDataSource
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(this.defaultDataSourceName, this.defaultDataSource);
        targetDataSources.putAll(this.customDataSources);

        // 实例化DynamicDataSource
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(this.defaultDataSource);

        return dynamicDataSource;
    }

    /**
     * 初始化数据源名称
     * @param env 环境参数
     */
    private void initDataSourceName(Environment env) {
        //配置文件 以prefix开头的配置文件
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, prefix + ".");

        String namesProperty = propertyResolver.getProperty("names", "");
        if ("".equals(namesProperty)) {
            return;
        }

        int index = 0;
        String[] dsPrefixs = namesProperty.split(",");
        slaves = new String[dsPrefixs.length - 1];

        for (String dsPrefix : dsPrefixs) {
            if ( 0 == index) {
                master = dsPrefix;
            } else {
                slaves[index - 1] = dsPrefix;
            }

            index++;
        }
    }

    /**
     * 初始化主数据源
     * @param env
     */
    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        String masterPrefix = prefix + "." + master;
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, masterPrefix + ".");

        Map<String, Object> dsMap = new HashMap<>();
        dsMap.put("type", propertyResolver.getProperty("type"));
        dsMap.put("driver-class-name", propertyResolver.getProperty("driver-class-name"));
        dsMap.put("url", propertyResolver.getProperty("url"));
        dsMap.put("username", propertyResolver.getProperty("username"));
        dsMap.put("password", propertyResolver.getProperty("password"));

        this.defaultDataSource = buildDataSource(dsMap);
        this.defaultDataSourceName = propertyResolver.getProperty("name");
        if (null == defaultDataSourceName || defaultDataSourceName.length() == 0) {
            throw new RuntimeException(masterPrefix + ".name must not be empty");
        }

        dataBinder(defaultDataSource, env, masterPrefix);
    }

    /**
     * 初始化从数据源
     * @param env
     */
    private void initCustomDataSources(Environment env) {
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = null;

        for (String slave : slaves) {
            // 多个数据源 前缀
            String slavePrefix = prefix + "." + slave;
            propertyResolver = new RelaxedPropertyResolver(env, slavePrefix + ".");

            Map<String, Object> dsMap = new HashedMap();
            dsMap.put("type", propertyResolver.getProperty("type"));
            dsMap.put("driver-class-name", propertyResolver.getProperty("driver-class-name"));
            dsMap.put("url", propertyResolver.getProperty("url"));
            dsMap.put("username", propertyResolver.getProperty("username"));
            dsMap.put("password", propertyResolver.getProperty("password"));

            DataSource ds = buildDataSource(dsMap);
            String name = propertyResolver.getProperty("name");
            if(null == name || name.length() == 0) {
                throw new RuntimeException(slavePrefix + ".name must not be empty");
            }

            this.customDataSources.put(name, ds);
            dataBinder(ds, env, slavePrefix);
        }
    }

    /**
     * 为DataSource绑定更多数据配置
     * @param dataSource 数据源
     * @param env        环境变量
     * @param prefix     数据源前缀，prefix + master 或 prefix + slave
     */
    private void dataBinder(DataSource dataSource, Environment env, String prefix) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);

        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);

        Map<String, Object> rpr = new RelaxedPropertyResolver(env, prefix).getSubProperties(".");
        Map<String, Object> values = new HashMap<>(rpr);

        // 排除已经设置的属性
        values.remove("type");
        values.remove("driver-class-name");
        values.remove("url");
        values.remove("username");
        values.remove("password");

        this.dataSourcePropertyValues = new MutablePropertyValues(values);
        dataBinder.bind(this.dataSourcePropertyValues);
    }

    /**
     * 构建数据源
     * @param dsMap 数据源基本属相字典
     * @return 数据源
     */
    private DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            Object type = dsMap.get("type");
            if (null == type) {
                // 默认DataSource
                type = DEFAULT_DS_TYPE;
            }

            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>)Class.forName((String)type);

            String driverClassName = dsMap.get("driver-class-name").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();

            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName)
                    .url(url).username(username).password(password).type(dataSourceType);

            return factory.build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
