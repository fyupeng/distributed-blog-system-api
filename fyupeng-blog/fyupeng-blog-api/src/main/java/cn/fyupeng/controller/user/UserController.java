package cn.fyupeng.controller.user;

import cn.fyupeng.annotation.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.UserInfo;
import cn.fyupeng.pojo.vo.bo.UserForUpdateBO;
import cn.fyupeng.pojo.vo.UserInfoVO;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.MD5Utils;
import io.swagger.annotations.*;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
@Api(value = "用户相关业务的接口", tags = {"用户相关业务的controller"})
public class UserController extends BasicController {

    private static String baidu_server = "https://aip.baidubce.com/oauth/2.0/token?";
    private static String grant_type = "client_credentials";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("resource");

    //private static String client_id = "BBVy0dHqG8CZSpExlPdksj4G"; // 应用的API Key
    //private static String client_secret = "AXjeTWzolNTrdn7kDzo8NCr0dlb4AQtI"; // 应用的Secret Key

    private static String client_id = ""; // 应用的API Key
    private static String client_secret = ""; // 应用的Secret Key

    static {
        client_id = resourceBundle.getString("client_id");
        client_secret = resourceBundle.getString("client_secret");
    }

    private static String url =
            baidu_server +
                    "grant_type=" +
                    grant_type +
                    "&client_id=" +
                    client_id +
                    "&client_secret=" +
                    client_secret;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, UserController.class);

    @PassToken
    @GetMapping(value = "/pingNetWork")
    @ApiOperation(value = "测试网络环境", notes = "测试网络环境的接口")
    public BlogJSONResult pingNetWork() {
        return BlogJSONResult.build(200, "ping successful!", null);
    }

    @PassToken
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    @PostMapping(value = "/query")
    public BlogJSONResult query(String userId) {

        if(StringUtils.isBlank(userId)){
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        UserInfo userInfo = userServiceProxy.queryUserInfo(userId);
        UserInfoVO userInfoVO = new UserInfoVO();

        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo,userInfoVO);
            User user = userServiceProxy.queryUser(userId);
            userInfoVO.setPermission(user.getPermission());
        }


        return BlogJSONResult.ok(userInfoVO);
    }


    @UserLoginToken
    @ApiOperation(value = "完善个人信息 - id字段请忽略", notes = "完善个人信息的接口")
    @ApiImplicitParam(name = "userInfo", value = "用户详情", required = true, dataType = "cn.fyupeng.pojo.UserInfo", paramType = "body")
    @PostMapping(value = "/completeUserInfo")
    public BlogJSONResult completeUserInfo(@RequestBody UserInfo userInfo) {

        if (StringUtils.isBlank(userInfo.getUserId())) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        UserInfo userInfoByUserId = userServiceProxy.queryUserInfo(userInfo.getUserId());
        if (userInfoByUserId == null) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        // id 是唯一标识符，不可更改
        userInfo.setId(userInfoByUserId.getId());

        userServiceProxy.updateUserInfo(userInfo);

        return BlogJSONResult.ok();
    }


    @UserLoginToken
    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "form")
    @PostMapping(value = "/uploadFace", headers = "content-type=multipart/form-data")
    public BlogJSONResult uploadFace(@RequestParam(value = "userId") String userId,
                                     @RequestPart(value = "file") /* 这两个注解不能搭配使用，会导致 文件上传按钮失效*/
                                     @ApiParam(name = "file", value = "头像") MultipartFile file) throws Exception{

        if(StringUtils.isBlank(userId)){
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        if (!userServiceProxy.queryUserIdIsExist(userId)) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";


        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;


        try {
            if(file != null){
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    String finalFacePath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalFacePath);
                    //创建用户文件夹
                    if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);//把输入流赋值给输出流，就是把图片复制到输出流对应的路径下
                }
            }else {
                return BlogJSONResult.errorMsg("上传出错1....");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return BlogJSONResult.errorMsg("上传出错2....");
        }finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }


        /**
         * User 与 UserInfo 是 一一对应的关系，UserInfo 有两个候选键 id 和 userId
         */

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setAvatar(uploadPathDB);

        userServiceProxy.updateUserInfo(userInfo);

        return BlogJSONResult.ok(uploadPathDB);
    }

    @UserLoginToken
    @ApiOperation(value = "用户修改密码", notes = "用户修改密码的接口")
    @ApiImplicitParam(name = "userBO", value = "更新密码业务用户", required = true, dataType = "cn.fyupeng.pojo.bo.UserForUpdateBO", paramType = "body")
    @PostMapping(value = "/updatePassword")
    public BlogJSONResult updatePassword(@RequestBody UserForUpdateBO userBO) throws Exception {

        if (StringUtils.isBlank(userBO.getUsername()) || StringUtils.isBlank(userBO.getOldPassword())) {
            return BlogJSONResult.errorMsg("用户名和原密码不能为空");
        }

        if (StringUtils.isBlank(userBO.getNewPassword())) {
            return BlogJSONResult.errorMsg("新密码不能为空");
        }

        User userResult = userServiceProxy.queryUserForLogin(userBO.getUsername(), MD5Utils.getMD5Str(userBO.getOldPassword()));

        if (userResult == null) {
            BlogJSONResult.errorMsg("用户名或原密码不正确");
        }

        User userForUpdate = new User();
        userForUpdate.setId(userResult.getId());
        userForUpdate.setUsername(userBO.getUsername());
        userForUpdate.setPassword(MD5Utils.getMD5Str(userBO.getNewPassword()));
        userForUpdate.setPermission(userResult.getPermission());

        boolean updateTrue = userServiceProxy.updateUser(userForUpdate);

        userForUpdate.setPassword("");

        return updateTrue ? BlogJSONResult.ok(userForUpdate) : BlogJSONResult.errorMsg("修改失败");
    }

    @UserLoginToken
    @ApiOperation(value = "植物鉴别", notes = "植物鉴别的接口")
    @GetMapping(value = "/plantIdentification")
    public BlogJSONResult plantIdentification() {
        return BlogJSONResult.ok();
    }

    @UserLoginToken
    @ApiOperation(value = "植物鉴别", notes = "植物鉴别的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "百科数目", required = true, dataType = "java.lang.Integer", paramType = "body"),
            @ApiImplicitParam(name = "image", value = "图片base64字符串", required = true, dataType = "java.lang.String", paramType = "body")
    })
    @PostMapping(value = "/plantIdentification", headers = "content-type=multipart/form-data")
    public BlogJSONResult plantIdentification(@RequestParam Integer num, String image) {

        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "");

        Request tokenRequest = new Request.Builder().url(url).get().build();

        Response response = null;
        String result = "";
        try {
            Response jsonResponse = HTTP_CLIENT.newCall(tokenRequest).execute();
            if (jsonResponse.isSuccessful()) {
                String jsonString = jsonResponse.body().string();
                int begin = jsonString.indexOf("\"access_token\":\"") + 15;
                int end = jsonString.indexOf("\",\"scope\"");

                String substring = jsonString.substring(begin, end);

                try {
                    String accessToken = jsonString.substring(begin, end);

                    MediaType MutilPart_Form_Data = MediaType.parse("application/x-www-form-urlencoded");
                    okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", "", okhttp3.RequestBody.create(MutilPart_Form_Data, image))
                            .addFormDataPart("baike_num", "", okhttp3.RequestBody.create(MutilPart_Form_Data, "" + num))
                            .build();

                    Request request = new Request.Builder()
                            .url("https://aip.baidubce.com/rest/2.0/image-classify/v1/plant?access_token=" + accessToken)
                            .method("POST", requestBody)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("Accept", "application/json")
                            .build();
                    response = HTTP_CLIENT.newCall(request).execute();
                    result = response.body().string();

                } catch (IndexOutOfBoundsException e) {
                    return BlogJSONResult.errorMsg("内部错误，请联系管理员");
                }

            } else {
                System.out.println(jsonResponse.body());
            }
        } catch (IOException exception) {
            return BlogJSONResult.errorMsg(exception.getMessage());
        }

        return BlogJSONResult.ok(result);

    }


}
