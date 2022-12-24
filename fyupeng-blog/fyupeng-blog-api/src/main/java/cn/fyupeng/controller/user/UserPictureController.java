package cn.fyupeng.controller.user;

import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.anotion.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.pojo.Picture;
import cn.fyupeng.pojo.bo.PictureForUploadBO;
import cn.fyupeng.service.PictureService;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.PagedResult;
import io.swagger.annotations.*;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @Auther: fyp
 * @Date: 2022/4/1
 * @Description:
 * @Package: com.crop.controller
 * @Version: 1.0
 */

@Slf4j
@RestController
@RequestMapping(value = "/user/picture")
@Api(value = "图片上传识别相关业务的接口", tags = {"图片上传识别相关业务的controller"})
public class UserPictureController extends BasicController {
    @Reference(timeout = 8000, asyncTime = 15000)
    private PictureService pictureService = rpcClientProxy.getProxy(PictureService.class, UserPictureController.class);

    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userService = rpcClientProxy.getProxy(UserService.class, UserPictureController.class);

    @PassToken
    @ApiOperation(value = "获取用户图片", notes = "获取用户图片的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "java.lang.Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "java.lang.Integer", paramType = "query")
    })
    @PostMapping(value = "/getAllPictures")
    public BlogJSONResult getAllPictures(String userId, Integer page, Integer pageSize) {

        if(StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = PICTURE_PAGE_SIZE;
        }

        Picture picture = new Picture();
        picture.setUserId(userId);

        PagedResult pageResult = pictureService.getAllPictures(picture, page, pageSize);

        return BlogJSONResult.ok(pageResult);
    }

    @UserLoginToken
    @ApiOperation(value = "上传图片 - 图片id 请忽略", notes = "上传图片的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "form"),
            //@ApiImplicitParam(name = "pictureBOs", value = "图片列表", required = true, dataType = "cn.fyupeng.pojo.bo.PictureForUploadBO", paramType = "form"),
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public BlogJSONResult upload(@RequestParam String userId, @RequestParam(value = "pictureBOs") List<PictureForUploadBO> pictureBOs,
            @RequestPart(value = "file")  /**这两个注解不能搭配使用，会导致 文件上传按钮失效*/
            /**@RequestParam(value = "file") **/
                                 @ApiParam(name = "file", value = "图片", allowMultiple = true) MultipartFile[] file) throws Exception{

        if (file == null || pictureBOs == null || file.length != pictureBOs.size()) {
            return BlogJSONResult.errorMsg("上传文件数量与实际图片数量不符");
        }

        if(StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        for (int index = 0; index < pictureBOs.size(); index++)
            if (StringUtils.isBlank(pictureBOs.get(index).getPictureDesc())){
                return BlogJSONResult.errorMsg("item[ "+(index+1)+" ] 图片描述不能为空");
            }

        if (!userService.queryUserIdIsExist(userId)) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        //保存到数据库中的相对路径
        String uploadPathDBDir = "/" + userId + "/picture";
        String uploadPathDB = uploadPathDBDir;

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String fileName;

        for (int index = 0; index < file.length; index++) {
            try {
                if (file != null) {
                    fileName = file[index].getOriginalFilename();
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
                        inputStream = file[index].getInputStream();

                        IOUtils.copy(inputStream, fileOutputStream);//把输入流赋值给输出流，就是把图片复制到输出流对应的路径下
                    }
                } else {
                    return BlogJSONResult.errorMsg("上传出错....");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return BlogJSONResult.errorMsg("上传出错....");
            } finally {
                pictureBOs.get(index).setPicturePath(uploadPathDB);
                uploadPathDB = uploadPathDBDir;
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        }

        List<Picture> pictureList = new ArrayList<>();
        for (PictureForUploadBO pbo : pictureBOs) {
            Picture p = new Picture();
            p.setUserId(userId);
            p.setPicturePath(pbo.getPicturePath());
            p.setPictureDesc(pbo.getPictureDesc());
            pictureList.add(p);
        }

        pictureService.upload(pictureList);

        return BlogJSONResult.ok(uploadPathDB);
    }

    @UserLoginToken
    @ApiOperation(value = "更改图片信息", notes = "更改图片信息的接口")
    @ApiImplicitParam(name = "picture", value = "图片", required = true, dataType = "cn.fyupeng.pojo.Picture", paramType = "body")
    @PostMapping(value = "/modifyePicture")
    public BlogJSONResult modifyePicture(@RequestBody Picture picture) {

        if (StringUtils.isBlank(picture.getId()) || StringUtils.isBlank(picture.getUserId())) {
            return BlogJSONResult.errorMsg("图片id或用户id不能为空");
        }

        Picture pictureWithIdAndUserId = new Picture();
        pictureWithIdAndUserId.setId(picture.getId());
        pictureWithIdAndUserId.setUserId(picture.getUserId());
        boolean pictureIsExist = pictureService.queryPictureIsExist(pictureWithIdAndUserId);
        if (!pictureIsExist) {
            return BlogJSONResult.errorMsg("用户不存在该图片");
        }

        boolean deletePictureIsTrue = pictureService.updatePicture(picture);

        return deletePictureIsTrue ?  BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致删除失败");

    }

    @UserLoginToken
    @ApiOperation(value = "删除用户图片", notes = "删除用户图片的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pictureId", value = "图片id", required = true, dataType = "java.lang.String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query"),
    })
    @PostMapping(value = "/removePicture")
    public BlogJSONResult removePicture(String pictureId, String userId) {

        if (StringUtils.isBlank(pictureId) || StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("图片id或用户id不能为空");
        }

        Picture picture = new Picture();
        picture.setId(pictureId);
        picture.setUserId(userId);
        Picture result = pictureService.queryPicture(picture);
        if (result == null) {
            return BlogJSONResult.errorMsg("用户不存在该图片");
        } else {
            String realPath = FILE_SPACE + result.getPicturePath();

            File file = new File(realPath);
            try {
                FileUtils.forceDeleteOnExit(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        boolean deletePictureIsTrue = pictureService.deletePicture(picture);

        return deletePictureIsTrue ?  BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致删除失败");

    }


}
