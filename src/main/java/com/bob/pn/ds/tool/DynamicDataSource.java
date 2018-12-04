package com.bob.pn.ds.tool;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author chenbo@guworks.cc
 * @title 动态数据源
 * @date 2018年12月03日
 * @since v1.0.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 重写数据源，利用ThreadLocal，每个线程单独搞一份
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSource();
    }

}
