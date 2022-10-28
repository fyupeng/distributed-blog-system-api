package cn.fyupeng;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"cn.fyupeng", "org.n3r.idworker"})
public class FyupengBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(FyupengBlogApplication.class, args);
    }

}
