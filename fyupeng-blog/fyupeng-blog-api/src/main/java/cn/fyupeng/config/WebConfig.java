package cn.fyupeng.config;

import cn.fyupeng.controller.BasicController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @Auther: fyp
 * @Date: 2022/8/29
 * @Description:
 * @Package: cn.fyupeng.config
 * @Version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(BasicController.URL_SPACE).addResourceLocations("file:" + BasicController.FILE_SPACE);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
