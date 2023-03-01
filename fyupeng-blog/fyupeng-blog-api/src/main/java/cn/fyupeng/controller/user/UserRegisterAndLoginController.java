package cn.fyupeng.controller.user;

import cn.fyupeng.annotation.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.vo.UserVO;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.MD5Utils;
import cn.fyupeng.utils.RedisUtils;
import cn.fyupeng.utils.TokenUtils;
import com.auth0.jwt.JWT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: fyp
 * @Date: 2022/8/17
 * @Description: 注册和登录 控制器
 * @Package: cn.fyupeng.controller
 * @Version: 1.0
 */

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
public class UserRegisterAndLoginController extends BasicController {
    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, UserRegisterAndLoginController.class);

    /**
     * 拦截器不拦截注册，所以不需要注解 @PassToken
     * @param user
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @ApiImplicitParam(name = "user", value = "用户", required = true, dataType = "cn.fyupeng.pojo.User", paramType = "body")
    @PostMapping(value = "/regist")
    public BlogJSONResult regist(@RequestBody User user) throws Exception {

        //1. 判断用户名和密码必须不为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return BlogJSONResult.errorMsg("用户名和密码不能为空");
        }

        //2. 判断用户是否存在
        boolean usernameIsExist = userServiceProxy.queryUsernameIsExist(user.getUsername());
        //3. 保存用户，注册信息
        if(!usernameIsExist){
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            // 防止 被 注入
            user.setPermission(2);
            userServiceProxy.saveUser(user);
        }else {
            return BlogJSONResult.errorMsg("用户名已存在");
        }
        user.setPassword("");

        //UserVO userVO = setUserRedisSessionToken(user);

        return BlogJSONResult.ok(user);
    }

    public UserVO setUserRedisSessionToken(User userModel) throws Exception {
        //String uniqueToken = UUID.randomUUID().toString();
        String token = TokenUtils.token(userModel.getId(), userModel.getUsername(), userModel.getPassword());

        String userRedisSession = RedisUtils.getUserRedisSession(userModel.getId());

        redis.set(userRedisSession, token,  60 * 5);

        UserVO usersVO = new UserVO();
        BeanUtils.copyProperties(userModel, usersVO);
        usersVO.setUserToken(token);
        return usersVO;
    }
    /**
     * 拦截器不拦截登录，所以不需要注解 @PassToken
     * @param user
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @ApiImplicitParam(name = "user", value = "用户", required = true, dataType = "cn.fyupeng.pojo.User", paramType = "body")
    @PostMapping(value = "/login")
    public BlogJSONResult login(@RequestBody User user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return BlogJSONResult.errorMsg("用户名或密码不能为空....");
        }

        User userResult = userServiceProxy.queryUserForLogin(username, MD5Utils.getMD5Str(user.getPassword()));

        if(userResult == null || userResult.getPermission() != 2){
            return BlogJSONResult.errorMsg("用户名密码不正确,或为非用户登录");
        }else {
            UserVO usersVO = setUserRedisSessionToken(userResult);
            usersVO.setPassword("");
            return BlogJSONResult.ok(usersVO);

        }
    }

    @UserLoginToken
    @ApiOperation(value = "更新秘钥", notes = "用户更新秘钥的接口")
    @ApiImplicitParam(name = "request", value = "请求", dataType = "javax.servlet.http.HttpServletRequest", paramType = "query", readOnly = true)
    @PostMapping(value = "/updateToken")
    public BlogJSONResult updateToken(HttpServletRequest request) throws Exception {

        String userId;
        String username;
        String password;
        String token = request.getHeader("token");


        userId = JWT.decode(token).getClaim("userId").asString();
        username = JWT.decode(token).getClaim("username").asString();
        password = JWT.decode(token).getClaim("password").asString();

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return BlogJSONResult.errorMsg("用户名或密码不能为空....");
        }

        User userResult = userServiceProxy.queryUserForLogin(username, password);

        if(userResult == null){
            return BlogJSONResult.errorMsg("Illegal key, update failed");
        }else {
            User tokenUser = new User();
            tokenUser.setId(userId);
            tokenUser.setUsername(username);
            tokenUser.setPassword(password);

            UserVO usersVO = setUserRedisSessionToken(tokenUser);
            usersVO.setPassword("");
            return BlogJSONResult.ok(usersVO);
        }
    }

    @UserLoginToken
    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    @PostMapping(value = "/logout")
    public BlogJSONResult logout(String userId) throws Exception {

        String userRedisSession = RedisUtils.getUserRedisSession(userId);
        redis.del(userRedisSession);

        return BlogJSONResult.ok("注销成功");

    }

}
