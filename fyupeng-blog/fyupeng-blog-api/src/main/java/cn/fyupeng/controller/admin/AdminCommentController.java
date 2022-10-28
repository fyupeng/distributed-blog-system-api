package cn.fyupeng.controller.admin;

import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.anotion.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.enums.CommentStatus;
import cn.fyupeng.pojo.Comment;
import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.vo.CommentVO;
import cn.fyupeng.service.*;
import cn.fyupeng.utils.BlogJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/8
 * @Description:
 * @Package: com.crop.admin.controller
 * @Version: 1.0
 */

@CrossOrigin
@Slf4j
@RestController
@RequestMapping(value = "/admin/comment")
@Api(value = "评论相关业务的接口", tags = {"评论相关业务的controller"})
public class AdminCommentController extends BasicController {
    @Reference
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, AdminCommentController.class);
    @Reference
    private CommentService commentServiceProxy = rpcClientProxy.getProxy(CommentService.class, AdminCommentController.class);


    @UserLoginToken
    @PostMapping(value = "removeComment")
    @ApiOperation(value = "强制删除评论", notes = "强制删除评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult removeComment(String commentId, String userId) {

        if (StringUtils.isBlank(commentId) || StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("commentId或userId不能为空");
        }

        boolean commentIsExist = commentServiceProxy.queryCommentIsExist(commentId);

        if (!commentIsExist) {
            return BlogJSONResult.errorMsg("commentId不存在");
        }

        User identifyUser = userServiceProxy.queryUser(userId);
        if (identifyUser == null || identifyUser.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户Id不存在或无权限");
        }

        commentServiceProxy.removeCommentById(commentId);
        commentServiceProxy.removeCommentWithFatherCommentId(commentId);

        return BlogJSONResult.ok();
    }


    @UserLoginToken
    @PostMapping(value = "/filterComments")
    @ApiOperation(value = "过滤查询评论", notes = "过滤查询评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aPattern", value = "文章匹配", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cPattern", value = "评论匹配", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "评论开始时间 - yyyy-MM-dd HH:mm:ss", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "评论结束时间 - yyyy-MM-dd HH:mm:ss", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult filterComments(String aPattern, String cPattern, String userId, String startTime, String endTime) {

        if (StringUtils.isBlank(aPattern) && StringUtils.isBlank(cPattern)) {
            return BlogJSONResult.errorMsg("至少指定文章匹配或评论匹配");
        }

        if (!(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime))) {
            return BlogJSONResult.errorMsg("必须指定时间跨度");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return BlogJSONResult.errorMsg("时间格式不正确");
        }

        List<CommentVO> result = null;
        if (StringUtils.isBlank(userId)) {
            result = commentServiceProxy.queryAllComments(aPattern, cPattern, null, startDate, endDate);
        } else {
            result = commentServiceProxy.queryAllComments(aPattern, cPattern, userId, startDate, endDate);
        }

        return BlogJSONResult.ok(result);

    }

    @UserLoginToken
    @PostMapping(value = "/setCommentStatus")
    @ApiOperation(value = "屏蔽评论 - 1 正常 - 2 屏蔽", notes = "屏蔽评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "评论状态", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "query"),
    })
    public BlogJSONResult setCommentStatus(String commentId, Integer status, String userId) {

        if (StringUtils.isBlank(commentId) || status == null) {
            return BlogJSONResult.errorMsg("评论id或状态不能为空");
        }

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        Comment comment = commentServiceProxy.queryComment(commentId);
        if (comment == null) {
            return BlogJSONResult.errorMsg("评论id不存在");
        }

        User user = userServiceProxy.queryUser(userId);
        if (user == null || user.getPermission() == 2) {
            return BlogJSONResult.errorMsg("用户id不存在或无权限访问");
        }

        if (status == 1) {
            commentServiceProxy.setCommentStatusWithFatherId(comment, CommentStatus.NORMAL);

        } else if(status == 2) {
            commentServiceProxy.setCommentStatusWithFatherId(comment, CommentStatus.BLOCKED);
        } else {
            return BlogJSONResult.errorMsg("无其他状态可操作");
        }

        return BlogJSONResult.ok();

    }

}
