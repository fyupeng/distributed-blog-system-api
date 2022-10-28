package cn.fyupeng.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: fyp
 * @Date: 2022/8/18
 * @Description:
 * @Package: cn.fyupeng.utils
 * @Version: 1.0
 */
public class TokenUtils {
    //设置过期时间
    private static final long EXPIRE_DATE=1000*60*5; //5 分钟
    //token秘钥
    private static final String TOKEN_SECRET = "vL4gA0bL4jG0cA4kK0bV2bI0lI0nB5aC2aV4";

    public static String token (String userId, String username,String password) {

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("userId",userId)
                    .withClaim("username",username)
                    .withClaim("password",password).withExpiresAt(date)
                    .sign(algorithm);

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }

    public static boolean verify(String token){
        /**
         * @desc   验证token，通过返回true
         * @params [token]需要校验的串
         **/
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            //System.out.println("校验失败");
            return  false;
        }
    }
}
