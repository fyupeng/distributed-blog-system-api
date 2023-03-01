package cn.fyupeng.annotation;

import cn.fyupeng.enums.DataSourceEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源切换
 * @Package: cn.fyupeng.annotation
 * @Version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceSwitcher {

    DataSourceEnum value() default DataSourceEnum.MASTER; // 默认 数据源 选用主库

}
