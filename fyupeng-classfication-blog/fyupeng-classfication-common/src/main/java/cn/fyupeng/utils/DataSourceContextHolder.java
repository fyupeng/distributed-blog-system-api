package cn.fyupeng.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源上下文 ThreadLocal
 * @Package: cn.fyupeng.utils
 * @Version: 1.0
 */
@Slf4j
public class DataSourceContextHolder {
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void put(String dataSourceName) {
        threadLocal.set(dataSourceName);
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

}

