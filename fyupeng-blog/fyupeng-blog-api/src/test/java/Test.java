import java.sql.DriverManager;
import java.util.UUID;

/**
 * @Auther: fyp
 * @Date: 2022/12/13
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {

    public static void main(String[] args) {


        for (int i = 0; i < 10; i++) {
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            System.out.println(id);
        }

        new Thread(() -> {
            System.out.println();
        }).run();


    }

}
