package cn.fyupeng.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.pojo.User;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.RedisUtils;
import cn.fyupeng.utils.TokenUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Auther: fyp
 * @Date: 2022/8/18
 * @Description:
 * @Package: cn.fyupeng.controller.interceptor
 * @Version: 1.0
 */
@Component
public class LoginInterceptor extends BasicController implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //检查方法是否有passtoken注解，有则跳过认证，直接通过
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                String userId;
                String userName;
                String password;
                try {
                    userId = JWT.decode(token).getClaim("userId").asString();
                    userName = JWT.decode(token).getClaim("username").asString();
                    password = JWT.decode(token).getClaim("password").asString();
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("The token is incorrect, please do not create the token by illegal means");
                }
                //查询数据库，看看是否存在此用户，方法要自己写
                UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class);
                // password 为 MD5 加密密文
                User user = userServiceProxy.queryUserForLogin(userName, password);
                if (user == null) {
                    throw new RuntimeException("User does not exist, please log in again");
                }

                // 验证 token
                if (TokenUtils.verify(token)) {
                    String userRedisSession = RedisUtils.getUserRedisSession(userId);
                    if(redis.get(userRedisSession) != null)
                        return true;
                    return false;
                } else {
                    throw new RuntimeException("Token expired or incorrect, please log in again");
                }

            }
        }
        throw new RuntimeException("Annotation without permission will not pass");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
