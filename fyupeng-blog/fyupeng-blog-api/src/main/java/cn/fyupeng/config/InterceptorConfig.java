package cn.fyupeng.config;

import cn.fyupeng.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: fyp
 * @Date: 2022/8/18
 * @Description: 拦截器
 * @Package: cn.fyupeng.controller.config
 * @Version: 1.0
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns= {
                "/user/**",
                "/admin/**"
        };
        String[] excludePathPatterns={
                "/user/login",
                "/user/regist"
        };
        registry.addInterceptor(loginInterceptor).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}
