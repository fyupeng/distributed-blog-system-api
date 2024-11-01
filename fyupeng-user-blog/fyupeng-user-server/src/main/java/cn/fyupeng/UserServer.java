package cn.fyupeng;

import cn.fyupeng.config.ResourceConfig;
import cn.fyupeng.annotation.ServiceScan;
import cn.fyupeng.enums.SerializerCode;
import cn.fyupeng.exception.RpcException;
import cn.fyupeng.net.netty.server.NettyServer;
import cn.fyupeng.util.IpUtils;
import cn.fyupeng.util.SpringContextUtil;
import cn.fyupeng.utils.ResourceLoadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.Map;


/**
 * @Auther: fyp
 * @Date: 2022/8/13
 * @Description:
 * @Package: cn.fyupeng
 * @Version: 1.0
 */


@Slf4j
@ServiceScan
@EnableAsync
@EnableScheduling
@MapperScan(basePackages = "cn.fyupeng.mapper")
@SpringBootApplication
@ComponentScan(basePackages = {"cn.fyupeng", "org.n3r.idworker"})
public class UserServer implements CommandLineRunner {

    @Lazy
    @Autowired
    private ResourceConfig resourceConfig;

    @PostConstruct
    public void init() {
        Map<String, String> resourceLoaders = ResourceLoadUtils.load("resource.properties");
        if (resourceLoaders != null) {
            String serverAddr = resourceLoaders.get("cn.fyupeng.config.server-addr");
            String[] addrArray = serverAddr.split(":");
            resourceConfig.setServerIp(addrArray[0]);
            resourceConfig.setServerPort(Integer.parseInt(addrArray[1]));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServer.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        //这里也可以添加一些业务处理方法，比如一些初始化参数等
        while(true){
            NettyServer nettyServer = null;
            try {
                nettyServer = new NettyServer(resourceConfig.getServerIp(), resourceConfig.getServerPort(), SerializerCode.HESSIAN.getCode()) {
                    @Override
                    public Object newInstance(String fullName, String simpleName, String firstLowCaseName, Class<?> clazz) throws InstantiationException, IllegalAccessException {
                        return SpringContextUtil.getBean(firstLowCaseName, clazz);
                    }
                };
            } catch (RpcException e) {
                e.printStackTrace();
            }
            log.info("Service bind in port with "+ resourceConfig.getServerPort() +" and start successfully!");
            nettyServer.start();
            log.error("RegisterAndLoginService is died，Service is restarting....");
        }
    }

}
