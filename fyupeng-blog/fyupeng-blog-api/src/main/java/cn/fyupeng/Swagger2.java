package cn.fyupeng;

import cn.fyupeng.enums.RequestHeaderKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableSwagger2
@EnableOpenApi
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
                .build()
                .globalRequestParameters(getRequestParameter());
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
                .build()
                .globalRequestParameters(getRequestParameter());
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                //设置页面标题
                .title("使用swagger2构建RPC分布式博客管理平台后端user-api接口文档")
                .contact(new Contact("distributed-blog-api - 仓库","git@github.com:fyupeng/distributed-blog-api/blob/main/README.md","fyp010311@163.com"))
                .description("欢迎访问RPC分布式博客管理平台接口文档，本文档描述了博客服务接口定义")
                .termsOfServiceUrl("https://www.zybuluo.com/mdeditor#2281023-full-reader")
                .version("1.0")
                .build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                //设置页面标题
                .title("使用swagger2构建RPC分布式博客管理平台后端admin-api接口文档")
                .contact(new Contact("distributed-blog-api - 仓库","git@github.com:fyupeng/distributed-blog-api/blob/main/README.md","fyp010311@163.com"))
                .description("欢迎访问RPC分布式博客管理平台接口文档，本文档描述了博客服务接口定义")
                .termsOfServiceUrl("https://www.zybuluo.com/mdeditor#2281023-full-reader")
                .version("1.0")
                .build();
    }

    private List<RequestParameter> getRequestParameter() {
        //ParameterBuilder builder = new ParameterBuilder();
        RequestParameterBuilder builder = new RequestParameterBuilder();
        List<RequestParameter> pars = new ArrayList<>();
        builder.name(RequestHeaderKey.TOKEN_HEADER_KEY.getName())
                .description("token令牌")
                .in(ParameterType.HEADER)
                .query(parameterSpecificationBuilder -> parameterSpecificationBuilder.defaultValue("1"))
                .build();
        pars.add(builder.build());
        return pars;
    }

}
