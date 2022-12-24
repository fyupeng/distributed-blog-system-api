import cn.fyupeng.util.IpUtils;
import org.n3r.idworker.utils.Ip;

/**
 * @Auther: fyp
 * @Date: 2022/12/24
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {
   public static void main(String[] args) {
      for (int i = 0; i < 10; i++) {
         String pubIpAddr = IpUtils.getPubIpAddr();
         System.out.println(pubIpAddr);
      }
   }
}
