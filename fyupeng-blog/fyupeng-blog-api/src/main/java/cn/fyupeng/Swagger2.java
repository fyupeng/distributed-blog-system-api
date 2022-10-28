package cn.fyupeng;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createWebApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("userApi")
                .apiInfo(webApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.fyupeng.controller.user"))
                //.paths(Predicates.and(PathSelectors.regex("/.*")))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket createAdminApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.fyupeng.controller.admin"))
                // .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                //设置页面标题
                .title("使用swagger2构建RPC分布式博客管理平台后端user-api接口文档")
                .contact(new Contact("distributed-blog-api - 仓库","git@github.com:fyupeng/distributed-blog-api/blob/main/README.md","fyp010311@163.com"))
                .description("欢迎访问RPC分布式博客管理平台接口文档，本文档描述了博客服务接口定义")
                .version("1.0.1")
                .build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                //设置页面标题
                .title("使用swagger2构建RPC分布式博客管理平台后端admin-api接口文档")
                .contact(new Contact("distributed-blog-api - 仓库","git@github.com:fyupeng/distributed-blog-api/blob/main/README.md","fyp010311@163.com"))
                .description("欢迎访问RPC分布式博客管理平台接口文档，本文档描述了博客服务接口定义")
                .version("1.0.1")
                .build();
    }

}
