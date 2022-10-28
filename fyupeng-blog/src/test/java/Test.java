import java.net.URL;
import java.util.*;

/**
 * @Auther: fyp
 * @Date: 2022/8/3
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {
   public static void main(String[] args) {
      URL urlOfClass = Test.class.getClassLoader().getResource("org.slf4j.spi.LocationAwareLogger.class");
      System.out.println(urlOfClass);
   }

}
