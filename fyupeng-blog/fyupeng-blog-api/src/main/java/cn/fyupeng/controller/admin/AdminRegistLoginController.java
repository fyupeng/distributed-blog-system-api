package cn.fyupeng.controller.admin;

import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.annotation.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.vo.UserVO;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.MD5Utils;
import cn.fyupeng.utils.RedisUtils;
import cn.fyupeng.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@SuppressWarnings("all")
@RestController
@RequestMapping(value = "/admin")
@Api(value = "管理员注册登录的接口", tags = {"管理员注册和登录的controller"})
public class AdminRegistLoginController extends BasicController {
    @Reference(timeout = 5000, asyncTime = 10000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, AdminRegistLoginController.class);

    @UserLoginToken
    @ApiOperation(value = "管理员注册", notes = "用管理员注册的接口")
    @ApiImplicitParam(name = "user", value = "用户", required = true, dataType = "cn.fyupeng.pojo.User", paramType = "body")
    @PostMapping(value = "/regist")
    public BlogJSONResult regist(@RequestBody User user) throws Exception{

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
            user.setPermission(3);
            userServiceProxy.saveUser(user);
        }else {
            return BlogJSONResult.errorMsg("用户名已存在");
        }
        user.setPassword("");
        //String uniqueToken = UUID.randomUUID().toString();
        //redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);
        //
        //UsersVO usersVO = new UsersVO();
        //BeanUtils.copyProperties(user, usersVO);
        //usersVO.setUserToken(uniqueToken);

        //UserVO userVO = setUserRedisSessionToken(user);
        user.setPassword("");

        return BlogJSONResult.ok(user);
    }

    public UserVO setUserRedisSessionToken(User userModel) throws Exception {
        //String uniqueToken = UUID.randomUUID().toString();
        //userModel.getPassword() 已经是加密的了
        String token = TokenUtils.token(userModel.getId(), userModel.getUsername(), userModel.getPassword());

        String userRedisSession = RedisUtils.getUserRedisSession(userModel.getId());

        redis.set(userRedisSession, token,  60 * 5);

        UserVO usersVO = new UserVO();
        BeanUtils.copyProperties(userModel, usersVO);
        usersVO.setUserToken(token);
        return usersVO;
    }

    @PassToken
    @ApiOperation(value = "管理员登录", notes = "管理员登录的接口")
    @ApiImplicitParam(name = "user", value = "用户", required = true, dataType = "cn.fyupeng.pojo.User", paramType = "body")
    @PostMapping(value = "/login")
    public BlogJSONResult login(@RequestBody User user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();


        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return BlogJSONResult.ok("用户名或密码不能为空....");
        }

        User userResult = userServiceProxy.queryUserForLogin(username, MD5Utils.getMD5Str(user.getPassword()));

        if(userResult == null || userResult.getPermission() != 3){
            return BlogJSONResult.errorMsg("用户名密码不正确,或为非管理员登录");
        }else {
            UserVO usersVO = setUserRedisSessionToken(userResult);
            userResult.setPassword("");
            return BlogJSONResult.ok(usersVO);

        }
    }

    @ApiOperation(value = "管理员注销", notes = "管理员注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    @PostMapping(value = "/logout")
    public BlogJSONResult logout(String userId) throws Exception {

        String userRedisSession = RedisUtils.getAdminRedisSession(userId);
        redis.del(userRedisSession);

        return BlogJSONResult.ok("注销成功");

    }

}
