package cn.fyupeng.controller.admin;

import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.annotation.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.pojo.Article;
import cn.fyupeng.pojo.Classfication;
import cn.fyupeng.pojo.User;
import cn.fyupeng.service.ArticleService;
import cn.fyupeng.service.ClassficationService;
import cn.fyupeng.service.UserService;
import cn.fyupeng.service.TagService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: fyp
 * @Date: 2022/4/5
 * @Description:
 * @Package: com.crop.admin.controller
 * @Version: 1.0
 */

@CrossOrigin
@Slf4j
@RestController
@RequestMapping(value = "/admin/article")
@Api(value = "文章相关业务的接口", tags = {"文章相关业务的controller"})
public class AdminArticleController extends BasicController {
    @Reference(timeout = 8000, asyncTime = 15000)
    private TagService tagServiceProxy = rpcClientProxy.getProxy(TagService.class, AdminArticleController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, AdminArticleController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private ArticleService articleServiceProxy = rpcClientProxy.getProxy(ArticleService.class, AdminArticleController.class);
    @Reference(timeout = 8000, asyncTime = 15000)
    private ClassficationService classficationServiceProxy = rpcClientProxy.getProxy(ClassficationService.class, AdminArticleController.class);

    private static ScheduledExecutorService executor;


    @UserLoginToken
    @PostMapping(value = "/removeClassfication")
    @ApiOperation(value = "删除文章分类 - 注意: 文章分类为所有用户公共的分类，方便查询，私有分类情使用标签", notes = "删除文章分类的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classficationId", value = "文章分类id", required = true, dataType = "java.lang.String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")

    })
    public BlogJSONResult removeClassfication(String classficationId, String userId) {

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        User user = userServiceProxy.queryUser(userId);
        // 用户不存在 或 无权 删除
        if (user == null || user.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户不存在或你无权执行该操作");
        }

        Article article = new Article();
        article.setClassId(classficationId);
        PagedResult pagedResult = articleServiceProxy.queryArticleSelective(article, 1, 1);

        if (pagedResult.getRecords() != 0) {
            return BlogJSONResult.errorMsg("删除失败！存在文章绑定了分类id: " + classficationId);
        }

        boolean deleteClassficationIsTrue = classficationServiceProxy.deleteClassfication(classficationId);

        return deleteClassficationIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("分类id不存在或内部错误");
    }

    @UserLoginToken
    @PostMapping(value = "/updateClassfication")
    @ApiOperation(value = "更新文章分类", notes = "更新文章分类的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classfication", value = "文章分类", required = true, dataType = "cn.fyupeng.pojo.Classfication", paramType = "body"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")

    })
    public BlogJSONResult updateClassfication(@RequestBody Classfication classfication, String userId) {

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户id不能为空");
        }

        User user = userServiceProxy.queryUser(userId);
        // 用户不存在 或 无权 删除
        if (user == null || user.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户不存在或你无权执行该操作");
        }

        boolean updateClassficationIsTrue = classficationServiceProxy.updateClassfication(classfication);

        return updateClassficationIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("分类id不存在或内部错误导致更新失败");
    }

    @UserLoginToken
    @PostMapping(value = "/saveClassfication")
    @ApiOperation(value = "新建文章分类", notes = "新建文章分类的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classficationName", value = "分类名", required = true, dataType = "java.lang.String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    })

    public BlogJSONResult saveClassFication(String classficationName, String userId) {

        if (StringUtils.isBlank(classficationName)) {
            return BlogJSONResult.errorMsg("分类名不能为空");
        }

        User user = userServiceProxy.queryUser(userId);
        // 用户不存在 或 无权 删除
        if (user == null || user.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户不存在或你无权执行该操作");
        }

        Classfication classfication = new Classfication();
        classfication.setName(classficationName);

        Classfication classficationIsExist = classficationServiceProxy.queryClassfication(classfication);
        if (classficationIsExist != null) {
            return BlogJSONResult.errorMsg("分类名已存在");
        }
        boolean saveIsTrue = classficationServiceProxy.saveClassfication(classfication);

        return saveIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致保存失败");
    }


    @UserLoginToken
    @PostMapping(value = "/startTimeTask")
    @ApiOperation(value = "开启任务 - 自动更新阅读量", notes = "开启任务的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult startTimeTask(String userId) {

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户Id不能为空");
        }

        User user = userServiceProxy.queryUser(userId);
        if (user == null || user.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户Id不存在或无权限");
        }

        if (executor != null && !executor.isShutdown()) {
            return BlogJSONResult.errorMsg("任务已启动，无需重启");
        }
        executor = Executors.newScheduledThreadPool(2);
        log.info("正在开启任务线程池...");
        long initialDelay = 1 * 1000;
        long fiveMinute = 5 * 60 * 1000;
        executor.scheduleAtFixedRate(() -> {
            /**
             * 定时任务 - 更新 阅读量
             */
            // 获取 所有用户 对Id的 阅读量
            String viewCount = RedisUtils.getViewCount();
            List<String> keys = redis.getKeysByPrefix(viewCount);
            /**
             * key格式: "crop:viewCount:2204057942HA6Z7C"
             * 获取 articleId : 2204057942HA6Z7C
             */
            List<String> articleIdKeys = new ArrayList<>();
            Map<String, String> articleMap = new HashMap<>();



            for (String k : keys) {
                // 匹配 最后一个 : 到结束
                String tempArticleId = k.substring(k.lastIndexOf(":") + 1);
                articleIdKeys.add(tempArticleId);
            }

            List<String> articleIdCounts = redis.multiGet(keys);

            for (int i = 0; i < articleIdKeys.size(); i++) {
                articleMap.put(articleIdKeys.get(i), articleIdCounts.get(i));
            }

            articleServiceProxy.multiUpdateArticleReadCounts(articleIdKeys, articleMap);
            log.info("完成一次周期任务 - 任务正常");

        }, initialDelay, fiveMinute, TimeUnit.MILLISECONDS);

        return BlogJSONResult.ok("任务启动成功");

    }

    @UserLoginToken
    @PostMapping(value = "/stopTimeTask")
    @ApiOperation(value = "关闭任务", notes = "关闭任务的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "java.lang.String", paramType = "query")
    public BlogJSONResult stopTimeTask(String userId) {

        if (StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("用户Id不能为空");
        }

        User user = userServiceProxy.queryUser(userId);
        if (user == null || user.getPermission() != 3) {
            return BlogJSONResult.errorMsg("用户Id不存在或无权限");
        }

        if (executor == null || executor.isTerminated()) {
            return BlogJSONResult.errorMsg("任务未启动");
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("任务线程池关闭失败: ",e);
            executor.shutdownNow();
            //e.printStackTrace();
        }
        log.info("任务线程池已成功关闭");
        return BlogJSONResult.ok("任务关闭成功");
    }



}
