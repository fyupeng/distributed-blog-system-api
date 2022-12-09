package cn.fyupeng.utils;

import cn.fyupeng.util.PropertiesConstants;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Auther: fyp
 * @Date: 2022/8/24
 * @Description:
 * @Package: cn.fyupeng.utils
 * @Version: 1.0
 */

@Slf4j
public class ResourceLoadUtils {

    private static Map<String, String> resourceLoaders = new HashMap<>();

    /**
     * 加载 Jar 包外置配置文件
     * 找不到返回 null
     * @param resourceName
     * @return
     */
    public static Map<String, String> load(String resourceName) {
        //2.1 创建Properties对象
        Properties p = new Properties();
        //2.2 调用p对象中的load方法进行配置文件的加载
        // 使用InPutStream流读取properties文件
        String currentWorkPath = System.getProperty("user.dir");
        InputStream is = null;
        String propertyValue = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(currentWorkPath + "/config/" + resourceName));) {
            p.load(bufferedReader);
            Enumeration<Object> keys = p.keys();
            while(keys.hasMoreElements()) {
                String property = (String) keys.nextElement();
                propertyValue = p.getProperty(property);
                log.info("discover key: {}, value: {} in {}", property, propertyValue, resourceName);
                resourceLoaders.put(property, propertyValue);
            }
            log.info("read resource from resource path: {}", currentWorkPath + "/config/" + resourceName);
            return resourceLoaders;
        } catch (IOException ioException) {
            log.info("not found resource from resource path: {}", currentWorkPath + "/config/" + resourceName);
            try {
                ResourceBundle resource = ResourceBundle.getBundle("resource");
                Set<String> keys = resource.keySet();
                Iterator<String> keysIterator = keys.iterator();
                while (keysIterator.hasNext()) {
                    String property = keysIterator.next();
                    propertyValue = resource.getString(property);
                    resourceLoaders.put(property, propertyValue);
                }
            } catch (MissingResourceException resourceException) {
                log.info("not found resource from resource path: {}", "resource.properties");
            }
            log.info("read resource from resource path: {}", "resource.properties");
            return resourceLoaders;
        }

    }
}
