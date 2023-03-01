package cn.fyupeng.aop;

import cn.fyupeng.annotation.DataSourceSwitcher;
import cn.fyupeng.utils.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源切换切面
 * @Package: cn.fyupeng.aspect
 * @Version: 1.0
 */
@Aspect
@Order(1)
@Component
@Slf4j
public class DataSourceAspect {

    @Pointcut("execution(* cn.fyupeng.service.impl..*.*(..))")
    public void aspect() {
    }

    @Before("aspect()")
    private void Before(JoinPoint point) {
        Object target = point.getTarget();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String method = signature.getName();

        Class<?> clazz = target.getClass();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();

        try {
            Method m = clazz.getMethod(method, parameterTypes);
            if (m.isAnnotationPresent(DataSourceSwitcher.class)) {
                DataSourceSwitcher dataSourceSwitcher = m.getAnnotation(DataSourceSwitcher.class);
                String dataSourceName = dataSourceSwitcher.value().getName();
                DataSourceContextHolder.put(dataSourceName);
                log.info("----------- Switch data sources, context preparation assignment -----------: {}", dataSourceName);
                log.info("----------- Switch the data source, the actual value of the data source context -----------: {}", DataSourceContextHolder.get());


            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @After("aspect()")
    private void After(JoinPoint point) {
        // 事务结束，重置线程变量，防止污染其他线程 或 造成 内存泄露问题
        DataSourceContextHolder.remove();
    }

}
