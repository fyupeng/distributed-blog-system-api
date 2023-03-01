package cn.fyupeng.controller.user;

import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.annotation.Reference;
import cn.fyupeng.pojo.Articles2tags;
import cn.fyupeng.pojo.Tag;
import cn.fyupeng.pojo.vo.ArticleVO;
import cn.fyupeng.pojo.vo.Articles2tagsVO;
import cn.fyupeng.pojo.vo.TagVO;
import cn.fyupeng.service.*;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.utils.BlogJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Api(value = "标签相关业务的接口", tags = {"标签相关业务的controller"})
@RequestMapping(value = "/user/tag")
public class UserTagController extends BasicController {
    @Reference(timeout = 8000, asyncTime = 15000)
    private TagService tagServiceProxy = rpcClientProxy.getProxy(TagService.class, UserTagController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, UserTagController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private ArticleService articleServiceProxy = rpcClientProxy.getProxy(ArticleService.class, UserTagController.class);

    @UserLoginToken
    @PostMapping(value = "/getTag")
    @ApiOperation(value = "获取标签", notes = "获取标签的接口")
    @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult getTag(String tagId) {

        if (StringUtils.isBlank(tagId)) {
            return BlogJSONResult.errorMsg("标签id不能为空");
        }

        Tag tagList = tagServiceProxy.queryTag(tagId);

        return BlogJSONResult.ok(tagList);
    }

    @UserLoginToken
    @PostMapping(value = "/getAllTags")
    @ApiOperation(value = "获取所有标签", notes = "获取所有标签的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult getAllTags(String userId) {
        Tag tagWithUserId = new Tag();
        tagWithUserId.setUserId(userId);

        List<TagVO> tagVOList = tagServiceProxy.queryAllTags(tagWithUserId);

        return BlogJSONResult.ok(tagVOList);
    }

    @UserLoginToken
    @PostMapping(value = "/saveTag")
    @ApiOperation(value = "保存标签 - id字段请忽略", notes = "保存标签的接口")
    @ApiImplicitParam(name = "tag", value = "标签", required = true, dataType = "cn.fyupeng.pojo.Tag", paramType = "body")
    public BlogJSONResult saveTag(@RequestBody Tag tag) {

        if (StringUtils.isBlank(tag.getName()) || StringUtils.isBlank(tag.getUserId())) {
            return BlogJSONResult.errorMsg("标签名或用户id不能为空");
        }

        tag.setId(null);
        /**
         * id 是候选键 - 标签名 + userId 也是候选键，能唯一 识别 标签
         */
        if (tagServiceProxy.queryTagIsExist(tag)) {
            return BlogJSONResult.errorMsg("标签名已存在");
        }

        boolean saveIsTrue = tagServiceProxy.saveTag(tag);

        return saveIsTrue ? BlogJSONResult.ok(): BlogJSONResult.errorMsg("内部错误导致保存失败");
    }

    @UserLoginToken
    @PostMapping(value = "/updateTag")
    @ApiOperation(value = "更新标签 - userId字段请忽略", notes = "更新标签的接口")
    @ApiImplicitParam(name = "tag", value = "标签", required = true, dataType = "cn.fyupeng.pojo.Tag", paramType = "body")
    public BlogJSONResult updateTag(@RequestBody Tag tag) {

        if (StringUtils.isBlank(tag.getId()) || StringUtils.isBlank(tag.getName())) {
            return BlogJSONResult.errorMsg("标签id或标签名不能为空");
        }

        // 用來 防止 其他 用户 对非本地 标签的 更改
        if (StringUtils.isBlank(tag.getUserId())) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        Tag tagWithIdAndUserId = new Tag();
        tagWithIdAndUserId.setId(tag.getId());
        tagWithIdAndUserId.setUserId(tag.getUserId());

        if (!tagServiceProxy.queryTagIsExist(tagWithIdAndUserId)) {
            return BlogJSONResult.errorMsg("该用户没有该标签");
        }

        boolean updateIsTrue = tagServiceProxy.updateTag(tag);

        return updateIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误");
    }

    @UserLoginToken
    @PostMapping(value = "/removeTag")
    @ApiOperation(value = "删除标签 - 连同已标记的文章标签一并删除", notes = "删除标签签的接口")
    @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult removeTag(String tagId) {

        if (StringUtils.isBlank(tagId)) {
            return BlogJSONResult.errorMsg("tagId不能为空");
        }

        // 1. 移除 Tag
        // 2. 移除 Articles2Tags
        boolean delTagIsTrue = tagServiceProxy.deleteTagAndArticleTagWithTagId(tagId);

        if (!delTagIsTrue) {
            return BlogJSONResult.errorMsg("标签id不存在或内部错误导致删除失败");
        }

        return BlogJSONResult.ok();
    }

    @UserLoginToken
    @PostMapping(value = "/getArticleWithNoneTag")
    @ApiOperation(value = "获取无标签文章", notes = "获取无标签文章的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult getArticleWithNoneTag(String userId) {

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        if (!userServiceProxy.queryUserIdIsExist(userId)) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        List<ArticleVO> articleVOList = articleServiceProxy.queryArticleWithNoneTagByUser(userId);

        return BlogJSONResult.ok(articleVOList);
    }

    @UserLoginToken
    @PostMapping(value = "/getArticleTag")
    @ApiOperation(value = "获取标签文章", notes = "获取标签文章的接口")
    @ApiImplicitParam(name = "tagId", value = "标签id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult getArticleTag(String tagId) {

        if (StringUtils.isBlank(tagId)) {
            return BlogJSONResult.errorMsg("标签id不能为空");
        }

        Articles2tags articles2tagsWithTagId = new Articles2tags();
        articles2tagsWithTagId.setTagId(tagId);

        List<Articles2tagsVO> articles2tagsVOList = tagServiceProxy.queryArticleTag(articles2tagsWithTagId);

        return BlogJSONResult.ok(articles2tagsVOList);
    }

    @UserLoginToken
    @PostMapping(value = "/markArticleTag")
    @ApiOperation(value = "标记文章标签 - id字段请忽略", notes = "标记文章标签的接口")
    @ApiImplicitParam(name = "articles2tags", value = "文章标签关联", required = true, dataType = "cn.fyupeng.pojo.Articles2tags", paramType = "body")
    public BlogJSONResult markArticleTag(@RequestBody Articles2tags articles2tags) {

        if (StringUtils.isBlank(articles2tags.getArticleId()) || StringUtils.isBlank(articles2tags.getTagId())) {
            return BlogJSONResult.errorMsg("文章id或标签id不能为空");
        }

        boolean articleIsExist = articleServiceProxy.queryArticleIsExist(articles2tags.getArticleId());

        Tag tagWithId = new Tag();
        tagWithId.setId(articles2tags.getTagId());
        boolean tagIsExist = tagServiceProxy.queryTagIsExist(tagWithId);

        if (!articleIsExist || !tagIsExist) {
            return BlogJSONResult.errorMsg("文章id或标签id不存在");
        }

        articles2tags.setId(null);
        // 已标记 的 不能 重复标记 - 文章id和标签id 也是一组 候选键
        if (tagServiceProxy.queryArticleTagIsExist(articles2tags)) {
            return BlogJSONResult.errorMsg("关联id已存在,不可重复标记");
        }

        // 标记 Articles2Tags
        boolean saveIsTrue = tagServiceProxy.saveArticleTag(articles2tags);

        return saveIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致保存失败");
    }

    @UserLoginToken
    @PostMapping(value = "/reMarkArticleTag")
    @ApiOperation(value = "重新标记文章标签", notes = "重新标记文章标签的接口")
    @ApiImplicitParam(name = "articles2tags", value = "文章标签关联", required = true, dataType = "cn.fyupeng.pojo.Articles2tags", paramType = "body")
    public BlogJSONResult reMarkArticleTag(@RequestBody Articles2tags articles2tags) {

        if (StringUtils.isBlank(articles2tags.getId())) {
            return BlogJSONResult.errorMsg("文章标签关联id不能为空");
        }

        // 重新标记 Articles2Tags
        if (StringUtils.isBlank(articles2tags.getArticleId()) || StringUtils.isBlank(articles2tags.getTagId())) {
            return BlogJSONResult.errorMsg("文章id或标签id不能为空");
        }

        boolean articleIsExist = articleServiceProxy.queryArticleIsExist(articles2tags.getArticleId());

        Tag tagWithId = new Tag();
        tagWithId.setId(articles2tags.getTagId());
        boolean tagIsExist = tagServiceProxy.queryTagIsExist(tagWithId);

        if (!articleIsExist || !tagIsExist) {
            return BlogJSONResult.errorMsg("文章id或标签id不存在");
        }

        // 已标记 的 才可以更新
        if (!tagServiceProxy.queryArticleTagIsExist(articles2tags.getId())) {
            return BlogJSONResult.errorMsg("关联id不存在");
        }

        // 标记 Articles2Tags
        boolean saveIsTrue = tagServiceProxy.updateArticleTag(articles2tags);

        return saveIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致保存失败");
    }


}
