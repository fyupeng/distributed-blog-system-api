package cn.fyupeng.controller.user;

import cn.fyupeng.anotion.Reference;
import cn.fyupeng.controller.BasicController;
import cn.fyupeng.annotion.PassToken;
import cn.fyupeng.annotion.UserLoginToken;
import cn.fyupeng.filter.SensitiveFilter;
import cn.fyupeng.pojo.Article;
import cn.fyupeng.pojo.Classfication;
import cn.fyupeng.pojo.vo.ArticleVO;
import cn.fyupeng.service.ArticleService;
import cn.fyupeng.service.ClassficationService;
import cn.fyupeng.service.TagService;
import cn.fyupeng.service.UserService;
import cn.fyupeng.utils.BlogJSONResult;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.utils.RedisUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Auther: fyp
 * @Date: 2022/4/2
 * @Description: 文章 Controller
 * @Package: com.crop.controller
 * @Version: 1.0
 */

@CrossOrigin
@Slf4j
@RestController
@Api(value = "文章相关业务的接口", tags = {"文章相关业务的controller"})
@RequestMapping(value = "/user/article")
public class UserArticleController extends BasicController {
    @Reference
    private TagService tagServiceProxy = rpcClientProxy.getProxy(TagService.class, UserArticleController.class);
    @Reference
    private UserService userServiceProxy = rpcClientProxy.getProxy(UserService.class, UserArticleController.class);
    @Reference
    private ArticleService articleServiceProxy = rpcClientProxy.getProxy(ArticleService.class, UserArticleController.class);
    @Reference
    private ClassficationService classficationServiceProxy = rpcClientProxy.getProxy(ClassficationService.class, UserArticleController.class);


    @PassToken
    @PostMapping(value = "/getAllArticles")
    @ApiOperation(value = "查找文章信息", notes = "查找文章信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult getAllArticles(String searchKey, Integer page, Integer pageSize, String userId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(searchKey)) {
            return BlogJSONResult.errorMsg("用户id和搜索关键字不能为空");
        }

        boolean userIsExist = userServiceProxy.queryUserIdIsExist(userId);
        if (!userIsExist) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        //非法敏感词汇判断
        SensitiveFilter filter = null;
        try {
            filter = SensitiveFilter.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = filter.CheckSensitiveWord(searchKey,0,1);
        if(n > 0){ //存在非法字符
            log.info("用户[{}]使用非法字符[{}]进行检索--",userId, searchKey);
            Set<String> sensitiveWord = filter.getSensitiveWord(searchKey, 1);
            return BlogJSONResult.errorMsg("捕捉敏感关键字: " + sensitiveWord);
        }

        // 进行 热度 维护
        incrementArticleScore(searchKey);

        boolean addTrue = addSearchHistory(userId, searchKey);
        if (!addTrue) {
            log.info("已存在key：{}",searchKey);
        }

        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = ARTICLE_PAGE_SIZE;
        }

        List<Article> articleList = null;

        Classfication classfication = new Classfication();
        classfication.setName(searchKey);

        Classfication cf = classficationServiceProxy.queryClassfication(classfication);

        Article article = new Article();
        if (cf != null) {
            article.setClassId(cf.getId());
        } else {
            article.setTitle(searchKey);
            article.setSummary(searchKey);
            article.setContent(searchKey);
        }
        // 优先 匹配 分类 id
        // 第二 优先 匹配 标题
        // 第三 匹配 摘要
        // 第四 优先 匹配 内容
        PagedResult pageResult = articleServiceProxy.queryArticleSelective(article, page,pageSize);

        return BlogJSONResult.ok(pageResult);
    }

    /**
     * 策略：
     *      1. 查询范围：最近一周 数据
     *      2. 排序方式：时间 最近排序
     * @param page
     * @param pageSize
     * @return
     */
    @PassToken
    @PostMapping(value = "/getRecentArticles")
    @ApiOperation(value = "获取最近文章", notes = "获取最近文章的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "String", paramType = "query"),
    })
    public BlogJSONResult getRecentArticles(Integer page, Integer pageSize) {

        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = ARTICLE_PAGE_SIZE;
        }

        // 最大 匹配 一 礼拜前的 文章
        Long timeDifference = ONE_WEEK; // 一周

        PagedResult pageResult = articleServiceProxy.queryArticleByTime(timeDifference,page,pageSize);

        return BlogJSONResult.ok(pageResult);
    }

    /**
     * 策略：
     *      1. 更新频率： 每小时
     *      2. 更新策略： 用户搜索率最高（过滤敏感信息）
     * @param page
     * @param pageSize
     * @return
     */
    @PassToken
    @PostMapping(value = "/getHotArticles")
    @ApiOperation(value = "获取推荐文章", notes = "获取推荐文章的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "String", paramType = "query")
    })
    public BlogJSONResult getHotArticles(Integer page, Integer pageSize) {

        List<String> hotSearchKeyList = getHotSearchKey(null, null);
        //// 随机从十条数据中拿出一条来搜索
        //Random random = new Random();
        //int index = random.nextInt(hotSearchKeyList.size());
        /**
         * 保证 1小时 之内查询的数据一致，1小时更新一次
         */
        int seed = Calendar.HOUR_OF_DAY;
        // 保证 不越过 数组长度
        if (hotSearchKeyList.size() == 0) {
            return BlogJSONResult.ok("缓存数据库中无缓存");
        }
        seed %= hotSearchKeyList.size();
        String searchKey = hotSearchKeyList.get(seed);

        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = ARTICLE_PAGE_SIZE;
        }

        List<Article> articleList = null;

        Classfication classfication = new Classfication();
        classfication.setName(searchKey);

        Classfication cf = classficationServiceProxy.queryClassfication(classfication);

        Article article = new Article();
        if (cf != null) {
            article.setClassId(cf.getId());
        } else {
            article.setTitle(searchKey);
            article.setSummary(searchKey);
            article.setContent(searchKey);
        }
        // 优先 匹配 分类 id
        // 第二 优先 匹配 标题
        // 第三 匹配 摘要
        // 第四 优先 匹配 内容
        PagedResult pageResult = articleServiceProxy.queryArticleSelective(article, page,pageSize);

        return BlogJSONResult.ok(pageResult);
    }

    @UserLoginToken
    @PostMapping(value = "/getSearchHistory")
    @ApiOperation(value = "获取搜索历史记录", notes = "获取搜索历史记录的接口")
    @ApiImplicitParam(name = "userId",  value = "用户id",required = true, dataType = "String", paramType = "query")
    public BlogJSONResult getSearchHistory(String userId) {
        List<String> searchHistoryList = querySearchHistory(userId);
        return BlogJSONResult.ok(searchHistoryList);
    }

    @UserLoginToken
    @PostMapping(value = "/removeHistory")
    @ApiOperation(value = "删除搜索历史记录", notes = "删除搜索历史记录的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",  value = "用户id",required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "searchKey",  value = "搜索关键字",required = true, dataType = "String", paramType = "query")
    })
    public BlogJSONResult removeHistory(String userId, String searchKey) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(searchKey)) {
            return BlogJSONResult.errorMsg("用户id和搜索关键字不能为空");
        }

        Long history = delSearchHistory(userId, searchKey);
        return BlogJSONResult.ok(history);
    }

    /**
     * 添加个人 历史数据
     * @param userId
     * @param searchKey
     * @return false: 已存在 - true : 添加成功
     */
    private boolean addSearchHistory(String userId, String searchKey) {

        String searchHistoryKey = RedisUtils.getSearchHistoryKey(userId);
        boolean keyIsExist = redis.hasKey(searchHistoryKey);
        if (keyIsExist) {
            String hk = redis.hget(searchHistoryKey, searchKey);
            // 关键字 key 存在
            if (hk != null) {
                return false;
            // 关键 key 不存在
            } else {
                redis.hset(searchHistoryKey, searchKey, "1");
            }
        // 首次 会 创建 包含 userId 的 key
        } else {
            redis.hset(searchHistoryKey, searchKey, "1");
        }
        return true;
    }

    /**
     * 获取个人 历史数据
     * @param userId
     * @return null : 没有数据 - List<String> ${ketList}
     */
    private List<String> querySearchHistory(String userId) {
        String searchHistoryKey = RedisUtils.getSearchHistoryKey(userId);
        boolean keyExist = redis.hasKey(searchHistoryKey);
        if (keyExist) {
            Cursor<Map.Entry<Object, Object>> cursor = redis.hscan(searchHistoryKey);
            List<String> keyList = new ArrayList<>();
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> current = cursor.next();
                String key = current.getKey().toString();
                keyList.add(key);
            }
            return keyList;
        }
        return null;
    }
    /**
     * 删除个人 历史数据
     * @param userId
     * @param searchKey
     * @return
     */
    private Long delSearchHistory(String userId, String searchKey) {

        String searchHistoryKey = RedisUtils.getSearchHistoryKey(userId);
        return redis.hdel(searchHistoryKey, searchKey);
    }

    private void incrementArticleScore(String searchKey) {
        /**
         * 规则： key: search-score
         *       value: java-1, linux-2 python-3
         */
        String searchScoreKey = RedisUtils.getSearchScoreKey();
        Long now = System.currentTimeMillis();

        // 只需第一次设置 时间戳
        if (redis.zScore(searchScoreKey, searchKey) == null) {
            // 统计 时间戳
            /**
             * 规则： key: search-score:java
             *       value: 时间戳
             */
            String keyWithSearchKey = RedisUtils.getSearchScoreKeyWithSearchKey(searchKey);
            redis.set(keyWithSearchKey, String.valueOf(now));
        }
        // 统计 点击量
        redis.zIncrementScore(searchScoreKey, searchKey, 1);
    }

    @PassToken
    @PostMapping(value = "/getHotSearchKey")
    @ApiOperation(value = "获取关键字热度", notes = "获取关键字热度的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字-缺省则匹配热度", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "检索数-缺省为10", dataType = "String", paramType = "query")
    })
    public BlogJSONResult getHotPot(String searchKey, Integer size) {
        if (StringUtils.isNotBlank(searchKey)) {
            //非法敏感词汇判断
            SensitiveFilter filter = null;
            try {
                filter = SensitiveFilter.getInstance();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int n = filter.CheckSensitiveWord(searchKey,0,1);
            if(n > 0){ //存在非法字符
                log.info("使用非法字符[{}]进行检索热度--",searchKey);
                Set<String> sensitiveWord = filter.getSensitiveWord(searchKey, 1);
                return BlogJSONResult.ok(sensitiveWord);
            }
        }
        List<String> hotSearchKeyList = getHotSearchKey(searchKey, size);
        return BlogJSONResult.ok(hotSearchKeyList);
    }

    /**
     * 搜索引擎
     * @param searchKey
     * @param size
     * @return
     */
    public List<String> getHotSearchKey(String searchKey, Integer size) {

        if (size == null) {
            size = SEARCH_SIZE;
        }

        Long now = System.currentTimeMillis();

        String searchScoreKey = RedisUtils.getSearchScoreKey();
        Set<String> allKeys = redis.zRevRangeByScore(searchScoreKey, 0, Double.MAX_VALUE);

        List<String> resultList = new ArrayList<>();
        if (!StringUtils.isBlank(searchKey)) {
            for (String key : allKeys) {
                // 在所有的 key 中包含 用户输入的 ${searchKey}
                if (StringUtils.containsIgnoreCase(key, searchKey)) {
                    // 记录数 已达 期待数，停止检索
                    if (resultList.size() >= size) {
                        break;
                    }
                    // 规则：与 新增 关键字 时做的 时间戳 key 值相对应
                    String searchScoreKeyWithSearchKey = RedisUtils.getSearchScoreKeyWithSearchKey(key);
                    String timeStamp = redis.get(searchScoreKeyWithSearchKey);
                    if (timeStamp != null) {
                        Long time = Long.valueOf(timeStamp);
                        // 查询 最近一个礼拜的 数据
                        if ((now - time) < 604800000L) {
                            resultList.add(key);
                        } else {
                            redis.zset(searchScoreKey, key, 0);
                        }
                    }
                }
            }
        } else {
            for (String key : allKeys) {
                // 在所有的 key 中包含 用户输入的 ${searchKey}
                // 记录数 已达 期待数，停止检索
                if (resultList.size() >= size) {
                    break;
                }
                // 规则：与 新增 关键字 时做的 时间戳 key 值相对应
                String searchScoreKeyWithSearchKey = RedisUtils.getSearchScoreKeyWithSearchKey(key);
                String timeStamp = redis.get(searchScoreKeyWithSearchKey);
                if (timeStamp != null) {
                    Long time = Long.valueOf(timeStamp);
                    // 查询 最近一个礼拜的 数据
                    if ((now - time) < 604800000L) {
                        resultList.add(key);
                    } else {
                        redis.zset(searchScoreKey, key, 0);
                    }
                }
            }
        }

        return resultList;
    }


    @PassToken
    @PostMapping(value = "/getArticleDetail")
    @ApiOperation(value = "获取文章详细信息", notes = "获取文章详细信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "String", paramType = "query"),
            //@ApiImplicitParam(name = "request",value = "请求", dataType = "HttpServletRequest", readOnly = true)
    })

    public BlogJSONResult getArticleDetail(String articleId, @ApiParam(hidden = true)HttpServletRequest request) {

        if (StringUtils.isBlank(articleId)) {
            return BlogJSONResult.errorMsg("文章id不能为空");
        }

        Article article = new Article();
        article.setId(articleId);

        ArticleVO articleVO = articleServiceProxy.queryArticleDetail(articleId);
        // 文章 id 不存在
        if (articleVO == null) {
            return BlogJSONResult.ok("articleId不存在");
        }

        String articleView = RedisUtils.getIdView(articleVO.getId(), request);
        String articleViewCount = RedisUtils.getIdViewCount(articleVO.getId());
        //用户短时间 已 访问，redis.get(userView) 返回值是 对象，不能 通过 ""判断
        if (redis.get(articleView) != null) {
            return BlogJSONResult.ok(articleVO);
        }
        /**
         * 统计量 一小时 内 同一个 ip 地址 只能算 1 次 阅读
         */
        redis.set(articleView, "1", 60 * 60);
        /**
         * articleViewCount 如果 未 初始化，redis 会 初始化为 0
         */
        redis.incr(articleViewCount,1);

        return BlogJSONResult.ok(articleVO);
    }

    @UserLoginToken
    @PostMapping(value = "/saveArticle")
    @ApiOperation(value = "保存文章信息 - id 字段请忽略", notes = "保存文章信息的接口")
    @ApiImplicitParam(name = "article", value = "文章", required = true, dataType = "Article", paramType = "body")
    public BlogJSONResult saveArticle(@RequestBody Article article) {


        if(StringUtils.isBlank(article.getUserId()) || StringUtils.isBlank(article.getTitle())
                || StringUtils.isBlank(article.getSummary() ) || StringUtils.isBlank(article.getClassId())
                || StringUtils.isBlank(article.getContent())){
            return BlogJSONResult.errorMsg("用户id、标题、摘要、分类id和内容不能为空");
        }

        boolean userIdIsExist = userServiceProxy.queryUserIdIsExist(article.getUserId());

        if (!userIdIsExist) {
            return BlogJSONResult.errorMsg("用户id不存在");
        }

        boolean classficationIsExist = classficationServiceProxy.queryClassficationIdIsExist(article.getClassId());

        if (!classficationIsExist) {
            return BlogJSONResult.errorMsg("分类id不存在");
        }

        boolean saveIsTrue = articleServiceProxy.save(article);

        return saveIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误导致保存失败");
    }

    @UserLoginToken
    @PostMapping(value = "/updateArticle")
    @ApiOperation(value = "更新文章信息", notes = "更新文章信息的接口")
    @ApiImplicitParam(name = "article", value = "文章", required = true, dataType = "Article", paramType = "body")
    public BlogJSONResult updateArticle(@RequestBody  Article article) {

        if (StringUtils.isBlank(article.getId()) || StringUtils.isBlank(article.getUserId())) {
            return BlogJSONResult.errorMsg("articleId 和 userId 不能为空");
        }

        Article articleWithIdAndUserId =  new Article();
        articleWithIdAndUserId.setId(article.getId());
        articleWithIdAndUserId.setUserId(article.getUserId());
        // 文章id 属于 用户id
        boolean articleIsUser = articleServiceProxy.queryArticleIsUser(articleWithIdAndUserId);
        if (articleIsUser) {
            Article ac = new Article();
            ac.setId(article.getId());
            ac.setUserId(article.getUserId());
            // 文章 标题 要更新
            if (StringUtils.isNotBlank(article.getTitle())) {
                ac.setTitle(article.getTitle());
            }
            // 文章 摘要 要更新
            if (StringUtils.isNotBlank(article.getSummary())) {
                ac.setSummary(article.getSummary());
            }
            // 文章 内容 要更新
            if (StringUtils.isNotBlank(article.getContent())) {
                ac.setContent(article.getContent());
            }
            // 文章 分类 要更新
            if (StringUtils.isNotBlank(article.getClassId())) {
                boolean classficationIdIsExist = classficationServiceProxy.queryClassficationIdIsExist(article.getClassId());
                if (!classficationIdIsExist)
                    return BlogJSONResult.errorMsg("分类id不存在");
                ac.setClassId(article.getClassId());
            }
            boolean updateIsTrue = articleServiceProxy.saveWithIdAndUserId(ac);

            return updateIsTrue ? BlogJSONResult.ok() : BlogJSONResult.errorMsg("内部错误更新失败");
        }

        return BlogJSONResult.errorMsg("用户 id 与 articleId 不存在或匹配失败");

    }

    @UserLoginToken
    @PostMapping(value = "/removeArticle")
    @ApiOperation(value = "删除文章", notes = "删除文章的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    })

    public BlogJSONResult removeArticle(String articleId, String userId) {
        if (StringUtils.isBlank(articleId) || StringUtils.isBlank(userId)) {
            return BlogJSONResult.errorMsg("articleId或userId不能为空");
        }

        Article article = new Article();
        article.setId(articleId);
        article.setUserId(userId);
        boolean articleIsUser = articleServiceProxy.queryArticleIsUser(article);

        if (!articleIsUser) {
            return BlogJSONResult.errorMsg("articleId不存在或者userId与commentId约束的userId不同");
        }

        /**
         * 删除文章 以及删除 与 文章 id 关联的 其他表数据
         */
        articleServiceProxy.removeArticle(articleId);

        return BlogJSONResult.ok();
    }

    @PassToken
    @PostMapping(value = "/getAllClassfications")
    @ApiOperation(value = "获取文章分类信息", notes = "获取文章分类信息的接口")
    public BlogJSONResult getAllClassfications() {

        List<Classfication> classficationList = classficationServiceProxy.queryAllClassfications();

        return BlogJSONResult.ok(classficationList);
    }


}
