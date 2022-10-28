package cn.fyupeng.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: fyp
 * @Date: 2022/8/18
 * @Description: 用户登录Token注解
 * @Package: cn.fyupeng.controller.annotion
 * @Version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLoginToken {
   boolean required() default true;
}
