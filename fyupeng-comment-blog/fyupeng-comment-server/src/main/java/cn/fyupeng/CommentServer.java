package cn.fyupeng;

import cn.fyupeng.config.ResourceConfig;
import cn.fyupeng.anotion.ServiceScan;
import cn.fyupeng.enums.SerializerCode;
import cn.fyupeng.net.netty.server.NettyServer;
import cn.fyupeng.utils.ResourceLoadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.Map;


/**
 * @Auther: fyp
 * @Date: 2022/8/13
 * @Description:
 * @Package: com.fyupeng
 * @Version: 1.0
 */

@Slf4j
@ServiceScan
@SpringBootApplication
@MapperScan(basePackages = "cn.fyupeng.mapper")
@ComponentScan(basePackages = {"cn.fyupeng", "org.n3r.idworker"})
public class CommentServer implements CommandLineRunner {

    @Autowired
    private ResourceConfig resourceConfig;

    @PostConstruct
    public void init() {
        Map<String, String> resourceLoaders = ResourceLoadUtils.load("resource.properties");
        if (resourceLoaders != null) {
            String serverPort = resourceLoaders.get("cn.fyupeng.config.serverPort");
            resourceConfig.setServerPort(Integer.parseInt(serverPort));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CommentServer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //这里也可以添加一些业务处理方法，比如一些初始化参数等
        while(true){
            NettyServer nettyServer = new NettyServer("127.0.0.1", resourceConfig.getServerPort(), SerializerCode.KRYO.getCode());
            log.info("Service bind in port with "+ resourceConfig.getServerPort() +" and start successfully!");
            nettyServer.start();
            log.error("RegisterAndLoginService is died，Service is restarting....");
        }
    }
}
