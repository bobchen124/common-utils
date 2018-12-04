package com.bob.pn.ds.tool;

/**
 * @author chenbo@guworks.cc
 * @title 线程切换然后注入当前使用的数据源
 * @date 2018年12月03日
 * @since v1.0.0
 */
public class DynamicDataSourceHolder {

    /**
     * 开启本地线程，防止线程不安全， 本来spring的线程就不安全
     */
    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public DynamicDataSourceHolder() {
    }

    /**
     * 设置数据源数据源
     * @param name 数据源名称
     */
    public static void setDataSource(String name) {
        holder.set(name);
    }

    /**
     * 默认方法，只重新插入，不重新拿取
     * @return 获取数据源
     */
    public static String getDataSource() {
        return holder.get();
    }

    /**
     * 删除数据源
     */
    public static void clearDataSource() {
        holder.remove();
    }

}
