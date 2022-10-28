import cn.fyupeng.utils.BlogJSONResult;

import java.io.UnsupportedEncodingException;

/**
 * @Auther: fyp
 * @Date: 2022/8/15
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {
   public static void main(String[] args) throws UnsupportedEncodingException {
      BlogJSONResult result1 = new BlogJSONResult("你好呀");
      BlogJSONResult result2 = new BlogJSONResult("你好呀");
      System.out.println(result1);
      System.out.println(result2);
      System.out.println(result1.equals(result2));
      System.out.println(result1 == result2);

      byte[] bytes1 = result1.toString().getBytes("UTF-8");
      byte[] bytes2 = result2.toString().getBytes("UTF-8");
      System.out.println(bytes1);
      System.out.println(bytes2);
      System.out.println(bytes1 == bytes2);
      System.out.println(bytes1.equals(bytes2));

      String s1 = new String(bytes1);
      String s2 = new String(bytes2);
      System.out.println(s1);
      System.out.println(s2);
      System.out.println(s1 == s2);
      System.out.println(s1.equals(s2));

   }
}
