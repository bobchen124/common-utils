package com.bob.pn.ds.tool;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenbo@guworks.cc
 * @title 数据源切面
 * @date 2018年12月04日
 * @since v1.0.0
 */
@Aspect
@Order(2)
@Component
public class DynamicDataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 该环境会同时被多个线程访问，所有使用线程安全对象
     */
    private ConcurrentHashMap<String, String> pointMap = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.bob.pn.ds.tool.DataSource)")
    public void dataSourceMethod() {
    }

    @Around("dataSourceMethod()")
    public Object doRound(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行到本方法之前方法的数据源，可能为空
        String preDataSource = DynamicDataSourceHolder.getDataSource();

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Class<?> clazz = signature.getDeclaringType();

        String name = clazz.getName() + "." + signature.getName();
        Method method = signature.getMethod();

        String sourceName = this.pointMap.get(name);
        if (null == sourceName || sourceName.length() == 0) {
            DataSource ds = method.getAnnotation(DataSource.class);
            sourceName = (ds != null) ? ds.value() : "";

            if (sourceName.length() > 0) {
                this.pointMap.put(name, sourceName);
            } else {
                logger.info("数据源[{}]不存在，使用默认数据源，{}", ds.value(), joinPoint.getSignature());
            }
        }

        if (sourceName.length() > 0) {
            DynamicDataSourceHolder.setDataSource(sourceName);
        }

        // 执行方法
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } finally {
            // 重置数据源
            DynamicDataSourceHolder.setDataSource(preDataSource);
        }
    }

}
