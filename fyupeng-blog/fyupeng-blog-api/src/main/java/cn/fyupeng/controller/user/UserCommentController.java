package cn.fyupeng.controller.user;

import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.anotion.Reference;
import cn.fyupeng.enums.CommentStatus;
import cn.fyupeng.pojo.Comment;
import cn.fyupeng.pojo.User;
import cn.fyupeng.service.*;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * @Auther: fyp
 * @Date: 2022/4/8
 * @Description:
 * @Package: com.crop.user.controller
 * @Version: 1.0
 */

@CrossOrigin
@Slf4j
@RestController
@Api(value = "评论相关业务的接口", tags = {"评论相关业务的controller"})
@RequestMapping(value = "/user/comment")
public class UserCommentController extends BasicController {
    @Reference(timeout = 8000, asyncTime = 15000)
    private TagService tagServiceProxyProxy = rpcClientProxy.getProxy(TagService.class, UserCommentController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, UserCommentController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private ArticleService articleServiceProxy = rpcClientProxy.getProxy(ArticleService.class, UserCommentController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private ClassficationService classficationServiceProxy = rpcClientProxy.getProxy(ClassficationService.class, UserCommentController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private CommentService commentServiceProxy = rpcClientProxy.getProxy(CommentService.class, UserCommentController.class);


    @PassToken
    @PostMapping(value = "/getAllComments")
    @ApiOperation(value = "获取文章所有评论", notes = "获取文章所有评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序-1-缺省[正序时间]-2[倒序时间]", dataType = "Integer", paramType = "query")
    })
    public BlogJSONResult getAllComments(String articleId, Integer page, Integer pageSize, Integer sort) {

        if (StringUtils.isBlank(articleId)) {
            return BlogJSONResult.errorMsg("文章id不能为空");
        }

        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }

        if (sort == null) {
            sort = 1;
        }

        PagedResult pageResult = commentServiceProxy.queryAllComments(articleId, page, pageSize, sort);

        return BlogJSONResult.ok(pageResult);

    }

    @UserLoginToken
    @PostMapping(value = "saveComment")
    @ApiOperation(value = "发表文章评论", notes = "发表文章评论的接口")
    @ApiImplicitParam(name = "comment", value = "评论", required = true, dataType = "Comment", paramType = "body")
    public BlogJSONResult saveComment(@RequestBody Comment comment) {

        if (StringUtils.isBlank(comment.getArticleId())) {
            return BlogJSONResult.errorMsg("articleId不能为空");
        }

        if (StringUtils.isBlank(comment.getFromUserId())) {
            return BlogJSONResult.errorMsg("留言者userId不能为空");
        }

        if (StringUtils.isBlank(comment.getComment())) {
            return BlogJSONResult.errorMsg("评论内容comment不能为空");
        }

        // fateherCommentId 是可以为 null 但是 不能是 空串 或 空白串
        if (comment.getFatherCommentId() != null && StringUtils.isBlank(comment.getFatherCommentId())) {
            return BlogJSONResult.errorMsg("不允许fatherCommentId为空串");
        }
        // toUserId 是可以为 null 但是 不能是 空串 或 空白串
        if (comment.getToUserId() != null && StringUtils.isBlank(comment.getToUserId())) {
            return BlogJSONResult.errorMsg("不允许toUserId为空串");
        }

        if (StringUtils.isNotBlank(comment.getToUserId()) && comment.getToUserId().equals(comment.getFromUserId())) {
            return BlogJSONResult.errorMsg("不能回复toUserId为fromUserId");
        }

        boolean articleIsExist = articleServiceProxy.queryArticleIsExist(comment.getArticleId());
        boolean userIdIsExist = userServiceProxy.queryUserIdIsExist(comment.getFromUserId());

        if (!articleIsExist || !userIdIsExist) {
            return BlogJSONResult.errorMsg("文章id不存在或留言者id不存在");
        }

        // 父 评论 验证
        if (StringUtils.isNotBlank(comment.getFatherCommentId())) {
            boolean fatherCommentIdIsExist = commentServiceProxy.queryCommentIsExist(comment.getFatherCommentId());
            if (!fatherCommentIdIsExist) {
                return BlogJSONResult.errorMsg("父评论id不存在");
            }
        }
        // 被 回复用户验证
        if (StringUtils.isNotBlank(comment.getToUserId())) {
            boolean toUserIsExist = userServiceProxy.queryUserIdIsExist(comment.getToUserId());
            if (!toUserIsExist) {
                return BlogJSONResult.errorMsg("被回复用户id不存在");
            }
        }

        boolean saveIsTrue = commentServiceProxy.saveComment(comment);

        return saveIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致保存失败");

    }

    @UserLoginToken
    @PostMapping(value = "/updateMyComment")
    @ApiOperation(value = "更新评论", notes = "更新评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "更新内容", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult updateMyComment(String commentId, String userId, String content) {

        if (StringUtils.isBlank(commentId) || StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("commentId或userId不能为空");
        }

        Comment comment = commentServiceProxy.queryComment(commentId);

        if (comment == null || !comment.getFromUserId().equals(userId)) {
            return BlogJSONResult.errorMsg("commentId不存在或者userId与commentId约束的userId不同");
        }

        comment.setComment(content);

        boolean commentIsUpdate = commentServiceProxy.updateComment(comment);

        return commentIsUpdate ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致更新失败");
    }

    /**
     * 该方法 已被 弃用 建议使用 public com.crop.utils.BlogJSONResult rollbackMyComment(String commentId, String userId)
     * @param commentId
     * @param userId
     * @return
     */
    @Deprecated
    @UserLoginToken
    @PostMapping(value = "/removeMyComment")
    @ApiOperation(value = "删除评论 - 已废弃", notes = "删除评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult removeMyComment(String commentId, String userId) {

        if (StringUtils.isBlank(commentId) || StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("commentId或userId不能为空");
        }

        Comment comment = commentServiceProxy.queryComment(commentId);

        if (comment == null || !comment.getFromUserId().equals(userId)) {
            return BlogJSONResult.errorMsg("commentId不存在或者userId与commentId约束的userId不同");
        }

        // 如果评论 已经被 追评，则不可撤销
        boolean commentWithFatherCommentIsExist = commentServiceProxy.queryCommentWithFatherCommentIsExist(commentId);

        if (commentWithFatherCommentIsExist) {
            return BlogJSONResult.errorMsg("有子评论约束，普通用户无权限");
        }

        commentServiceProxy.removeCommentById(commentId);

        return BlogJSONResult.ok();
    }

    @UserLoginToken
    @PostMapping(value = "/rollbackMyComment")
    @ApiOperation(value = "撤回评论", notes = "撤回评论的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult rollbackMyComment(String commentId, String userId) {

        if (StringUtils.isBlank(commentId)) {
            return BlogJSONResult.errorMsg("评论id不能为空");
        }

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        Comment comment = commentServiceProxy.queryComment(commentId);
        if (comment == null) {
            return BlogJSONResult.errorMsg("评论id不存在");
        }

        User user = userServiceProxy.queryUser(userId);
        if (user == null) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        if (!comment.getFromUserId().equals(user.getId())) {
            return BlogJSONResult.errorMsg("非本用户评论无权撤回");
        }

        commentServiceProxy.setCommentStatusWithFatherId(comment, CommentStatus.BLOCKED);

        return BlogJSONResult.ok();

    }

}
