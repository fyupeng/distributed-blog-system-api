
import java.util.UUID;

/**
 * @Auther: fyp
 * @Date: 2022/12/8
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args) {
        for(int i = 0 ; i < 10; i++) {
            System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }




}
