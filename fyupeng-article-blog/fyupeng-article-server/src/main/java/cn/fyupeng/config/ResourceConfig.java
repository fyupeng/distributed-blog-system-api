package cn.fyupeng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Auther: fyp
 * @Date: 2022/8/17
 * @Description: 资源配置
 * @Package: cn.fyupeng.config
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix="cn.fyupeng.config")
//不使用默认配置文件application.properties和application.yml
@PropertySource("classpath:resource.properties")
public class ResourceConfig {
    private int serverPort;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
