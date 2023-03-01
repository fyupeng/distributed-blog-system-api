package cn.fyupeng.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: Spring容器上下文工具类
 * @Package: cn.fyupeng.util
 * @Version: 1.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    // Spring 上下文 对象
    private static ApplicationContext applicationContext;

    /**
     * 实现 ApplicationContextAware 接口的 回调方法，设置上下文环境
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static Object getBean(String name, Class cla) throws BeansException {
        return applicationContext.getBean(name, cla);
    }

}
