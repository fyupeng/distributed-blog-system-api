package cn.fyupeng.service.impl;

import cn.fyupeng.annotation.DataSourceSwitcher;
import cn.fyupeng.enums.DataSourceEnum;
import cn.fyupeng.mapper.*;
import cn.fyupeng.pojo.*;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.utils.TimeAgoUtils;
import cn.fyupeng.pojo.vo.ArticleVO;
import cn.fyupeng.service.ArticleService;
import cn.fyupeng.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: fyp
 * @Date: 2022/4/2
 * @Description:
 * @Package: com.crop.service.impl
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private static ArticleServiceImpl basicService;

    @Lazy
    @Autowired
    private ArticleRepository articleRepository;

    @Lazy
    @Autowired
    private MongoTemplate mongoTemplate;

    @Lazy
    @Autowired
    private UserInfoMapper userinfoMapper;

    @Lazy
    @Autowired
    private ClassficationMapper classficationMapper;

    @Lazy
    @Autowired
    private TagMapper tagMapper;

    @Lazy
    @Autowired
    private Articles2tagsMapper articles2tagsMapper;

    @Lazy
    @Autowired
    private PictureMapper pictureMapper;

    @Lazy
    @Autowired
    private Sid sid;

    @PostConstruct
    public void init() {
        this.basicService = this;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryArticleIsExist(String articleId) {

        Article article = new Article();
        article.setId(articleId);

        Example<Article> articleExample = Example.of(article);

        Optional<Article> one = basicService.articleRepository.findOne(articleExample);
        Article result = one.isPresent() ? one.get() : null;

        return article == null ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryArticleIsUser(Article article) {

        ExampleMatcher matching = ExampleMatcher.matching();
        ExampleMatcher exampleMatcher = matching.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Article> articleExample = Example.of(article, exampleMatcher);

        Optional<Article> one = basicService.articleRepository.findOne(articleExample);
        Article result = one.isPresent() ? one.get() : null;

        return result == null ? false : true;
    }

    /**
     * 查询 所有文章 时， 不做 流量统计，查询单篇文章，统计 阅读量
     * @param article
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryArticleSelective(Article article, Integer page, Integer pageSize) {

        //分页查询对象
        if(page <= 0){
            page = 1;
        }
        // page 分页 在 mongodb 中是 从 0 开始的，转换为物理位置
        page = page - 1;

        if(pageSize <= 0){
            pageSize = 6;
        }

        ExampleMatcher matching = ExampleMatcher.matching();
        // 当前页记录
        List<Article> all = null;
        // 总 记录数
        long records = 0;

        Article a = new Article();



        // 存在 分类名 为 key
        if (!StringUtils.isBlank(article.getClassId())) {
            all = readAndExcute("classId", matching, article, page, pageSize);
            records = readAndExcuteCount("classId", matching, article, page, pageSize);
        } else {
            Article titleArticle = new Article();
            titleArticle.setTitle(article.getTitle());
            all = readAndExcute("title", matching, titleArticle, page, pageSize);
            records = readAndExcuteCount("classId", matching, article, page, pageSize);
            if (all.size() == 0) {
                Article summaryArticle = new Article();
                summaryArticle.setSummary(article.getSummary());
                all = readAndExcute("summary", matching, summaryArticle, page, pageSize);
                records = readAndExcuteCount("classId", matching, article, page, pageSize);
                if (all.size() == 0) {
                    Article contentArticle = new Article();
                    contentArticle.setContent(article.getSummary());
                    all = readAndExcute("content", matching, contentArticle, page, pageSize);
                    records = readAndExcuteCount("classId", matching, article, page, pageSize);
                    if (all.size() == 0)
                        return null;
                }
            }
        }

        List<ArticleVO> artileVOList = new ArrayList<>();

        // 时间处理、其他处理
        for (Article ac : all) {
            String createTimeAgo = TimeAgoUtils.format(ac.getCreateTime());
            String updateTimeAgo = TimeAgoUtils.format(ac.getUpdateTime());
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(ac, articleVO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String normalCreateTime = sdf.format(ac.getCreateTime());
            String normalUpdateTime = sdf.format(ac.getUpdateTime());
            articleVO.setNormalCreateTime(normalCreateTime);
            articleVO.setNormalUpdateTime(normalUpdateTime);
            articleVO.setCreateTimeAgoStr(createTimeAgo);
            articleVO.setUpdateTimeAgoStr(updateTimeAgo);

            String articleCoverId = ac.getArticleCoverId();
            if (!StringUtils.isBlank(articleCoverId)) {
                Picture queryPicture = new Picture();
                queryPicture.setId(articleCoverId);
                Picture pictureInfo = basicService.pictureMapper.selectOne(queryPicture);
                articleVO.setArticleCoverUrl(pictureInfo.getPicturePath());
            }
            artileVOList.add(articleVO);
        }

        // 降序排序，最近发表的文章 放到了 最前面
        Collections.sort(artileVOList);
        // 总页数
        int total = 1;
        // 每页 大小 小于 总记录数 时才需要分页
        if (pageSize < records) {
            // 最后一页 还有 数据
            if (records % pageSize != 0) {
                total += records / pageSize;
            } else {
                total = (int) (records / pageSize);
            }
        }

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(total);
        pagedResult.setRecords(records);
        pagedResult.setRows(artileVOList);
        // page 最后转换为 逻辑位置
        pagedResult.setPage(page+1);

        return pagedResult;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public ArticleVO queryArticleDetail(String articleId) {

        /**
         * 每次获取 文章，该 文章的 阅读数要 自增 1
         */

        Article article = new Article();
        article.setId(articleId);
        Example<Article> articleExample = Example.of(article);

        Optional<Article> one = basicService.articleRepository.findOne(articleExample);
        Article result = one.isPresent() ? one.get() : null;
        ArticleVO articleVO = new ArticleVO();
        if (result != null) {
            BeanUtils.copyProperties(result, articleVO);

            String createTimeAgo = TimeAgoUtils.format(result.getCreateTime());
            String updateTimeAgo = TimeAgoUtils.format(result.getUpdateTime());

            tk.mybatis.mapper.entity.Example userInfoExample = new tk.mybatis.mapper.entity.Example(UserInfo.class);
            tk.mybatis.mapper.entity.Example.Criteria criteria = userInfoExample.createCriteria();

            criteria.andEqualTo("userId", result.getUserId());

            UserInfo userInfo = basicService.userinfoMapper.selectOneByExample(userInfoExample);
            Classfication classfication = basicService.classficationMapper.selectByPrimaryKey(result.getClassId());

            if (userInfo != null) {
                articleVO.setAvatar(userInfo.getAvatar());
                articleVO.setNickName(userInfo.getNickname());
            }

            if (classfication != null) {
                articleVO.setClassficationName(classfication.getName());
            }


            // 标签
            String aid = result.getId();
            Articles2tags articles2tags = new Articles2tags();
            articles2tags.setArticleId(aid);
            articles2tags = queryArticleTag(articles2tags);
            if (articles2tags != null)  {
                Tag tag = basicService.tagMapper.selectByPrimaryKey(articles2tags.getTagId());
                articleVO.setTagId(tag.getId());
                articleVO.setTagName(tag.getName());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String normalCreateTime = sdf.format(result.getCreateTime());
            String normalUpdateTime = sdf.format(result.getUpdateTime());
            articleVO.setNormalCreateTime(normalCreateTime);
            articleVO.setNormalUpdateTime(normalUpdateTime);

            articleVO.setCreateTimeAgoStr(createTimeAgo);
            articleVO.setUpdateTimeAgoStr(updateTimeAgo);
            return articleVO;
        }

        return null;
    }

    private Articles2tags queryArticleTag(Articles2tags articles2tags) {

        List<Articles2tags> articles2tagsList = basicService.articles2tagsMapper.select(articles2tags);

        if (articles2tagsList.size() != 0) {

            return articles2tagsList.get(0);
        }
        return null;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public void multiUpdateArticleReadCounts(List<String> articleIdKeys, Map<String, String> articleMap) {
        for (String articleId : articleIdKeys) {

            Article article = new Article();
            article.setId(articleId);

            Example<Article> articleExample = Example.of(article);

            Optional<Article> one = basicService.articleRepository.findOne(articleExample);
            Article oldArticle = one.isPresent() ? one.get() : null;
            // 获取 articleId 对应的 readCounts
            String readCounts = articleMap.get(articleId);
            // 更新 readCounts
            if (oldArticle != null) {
                oldArticle.setReadCounts(Integer.parseInt(readCounts));

                basicService.articleRepository.save(oldArticle);
            }
        }
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeArticle(String articleId) {
        /**
         * 必须根据 id 删除

        Article article = new Article();
        article.setId(articleId);
        articleRepository.delete(article);
         */
        Query queryArticle = new Query();
        queryArticle.addCriteria(Criteria.where("id").is(articleId));
        basicService.mongoTemplate.remove(queryArticle, Article.class);

        // 删除评论
        Query queryComment = new Query();
        queryComment.addCriteria(Criteria.where("articleId").is(articleId));
        basicService.mongoTemplate.remove(queryComment, Comment.class);

        // 删除关联标签nav
        deleteTagAndArticleTagWithArticleId(articleId);
    }

    private boolean deleteTag(String tagId) {

        int i = basicService.tagMapper.deleteByPrimaryKey(tagId);

        return i > 0 ? true : false;
    }

    private void delArticleTag(String tagId) {

        tk.mybatis.mapper.entity.Example articles2tagsExample = new tk.mybatis.mapper.entity.Example(Articles2tags.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = articles2tagsExample.createCriteria();
        criteria.andEqualTo("tagId", tagId);

        int i = basicService.articles2tagsMapper.deleteByExample(articles2tagsExample);

    }

    private boolean deleteTagAndArticleTagWithArticleId(String articleId) {
        tk.mybatis.mapper.entity.Example example = new tk.mybatis.mapper.entity.Example(Articles2tags.class);

        tk.mybatis.mapper.entity.Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("articleId", articleId);

        int i = basicService.articles2tagsMapper.deleteByExample(example);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryArticleByTime(Long timeDifference, Integer page, Integer pageSize) {

        //分页查询对象
        if(page <= 0){
            page = 1;
        }
        // page 分页 在 mongodb 中是 从 0 开始的，转换为 物理位置
        page = page - 1;

        if(pageSize <= 0){
            pageSize = 10;
        }

        // 当前时间
        Long now = System.currentTimeMillis();
        Long time = now - timeDifference;
        // 一周前的 日期
        Date oneWekkAgodate = new Date(time);

        Query queryAll = new Query();
        Query queryCurrentPage = new Query();

        queryAll.addCriteria(Criteria.where("createTime").gt(oneWekkAgodate));
        queryCurrentPage.addCriteria(Criteria.where("createTime").gt(oneWekkAgodate));
        queryCurrentPage.fields().exclude("content");

        List<String> sortCondition = new ArrayList<>();
        sortCondition.add("createTime");

        // 倒序 时间
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        queryAll.with(sort);
        queryCurrentPage.with(sort);
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        queryCurrentPage.with(pageRequest);
        // 分页

        /**
         * PageHelper 不支持 mongodb 查询
         */
        //PageHelper.startPage(page, pageSize);
        long allArticleCount = basicService.mongoTemplate.count(queryAll, Article.class);
        List<Article> currentPageArticleList = basicService.mongoTemplate.find(queryCurrentPage, Article.class);


        List<ArticleVO> artileVOList = new ArrayList<>();

        // 时间处理
        for (Article ac : currentPageArticleList) {
            String createTimeAgo = TimeAgoUtils.format(ac.getCreateTime());
            String updateTimeAgo = TimeAgoUtils.format(ac.getUpdateTime());
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(ac, articleVO);
            articleVO.setCreateTimeAgoStr(createTimeAgo);
            articleVO.setUpdateTimeAgoStr(updateTimeAgo);

            String articleCoverId = ac.getArticleCoverId();
            if (!StringUtils.isBlank(articleCoverId)) {
                Picture queryPicture = new Picture();
                queryPicture.setId(articleCoverId);
                Picture pictureInfo = basicService.pictureMapper.selectOne(queryPicture);
                articleVO.setArticleCoverUrl(pictureInfo.getPicturePath());
            }
            artileVOList.add(articleVO);
        }

        long records = allArticleCount;
        int total = 1;
        // 每页 大小 小于 总记录数 时才需要分页
        if (pageSize < records) {
            // 最后一页 还有 数据
            if (records % pageSize != 0) {
                total += records / pageSize;
            } else {
                total = (int) (records / pageSize);
            }
        }

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(total);
        pagedResult.setRecords(records);
        pagedResult.setRows(artileVOList);
        // page 最后转换为 逻辑位置
        pagedResult.setPage(page+1);


        return pagedResult;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ArticleVO> queryArticleWithNoneTagByUser(String userId) {

        ExampleMatcher matching = ExampleMatcher.matching();
        ExampleMatcher articleExampleMatcher = matching.withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact());

        Article article = new Article();
        article.setUserId(userId);

        Example<Article> articleExample = Example.of(article, articleExampleMatcher);

        List<Article> all = basicService.articleRepository.findAll(articleExample);

        List<ArticleVO> artileVOList = new ArrayList<>();
        for (Article ac : all) {

            String articleId = ac.getId();
            Articles2tags articles2tags = new Articles2tags();
            articles2tags.setArticleId(articleId);
            // 过滤掉 有标签 标记的 用户文章
            boolean articleTagIsExist = queryArticleTagIsExist(articles2tags);
            if (articleTagIsExist) {
                continue;
            }

            String createTimeAgo = TimeAgoUtils.format(ac.getCreateTime());
            String updateTimeAgo = TimeAgoUtils.format(ac.getUpdateTime());
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(ac, articleVO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String normalCreateTime = sdf.format(ac.getCreateTime());
            String normalUpdateTime = sdf.format(ac.getUpdateTime());
            articleVO.setNormalCreateTime(normalCreateTime);
            articleVO.setNormalUpdateTime(normalUpdateTime);
            articleVO.setCreateTimeAgoStr(createTimeAgo);
            articleVO.setUpdateTimeAgoStr(updateTimeAgo);

            String articleCoverId = ac.getArticleCoverId();
            if (!StringUtils.isBlank(articleCoverId)) {
                Picture queryPicture = new Picture();
                queryPicture.setId(articleCoverId);
                Picture pictureInfo = basicService.pictureMapper.selectOne(queryPicture);
                if (pictureInfo != null)
                    articleVO.setArticleCoverUrl(pictureInfo.getPicturePath());
            }

            /**
             * 查询量 太大，流量大，而且 用户 只是在 查询而已，不需要评论 和 内容
             */
            articleVO.setContent(null);
            artileVOList.add(articleVO);
        }

        return artileVOList;
    }

    public boolean queryArticleTagIsExist(Articles2tags articles2tags) {
        List<Articles2tags> result = basicService.articles2tagsMapper.select(articles2tags);

        return result.size() == 0 ? false : true;
    }


    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean save(Article article) {

        String articleId = basicService.sid.nextShort();
        article.setId(articleId);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        article.setReadCounts(0);
        article.setCommentCounts(0);
        article.setReceiveLikeCounts(0);

        Article result = basicService.articleRepository.save(article);

        return result == null ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveWithIdAndUserId(Article article) {

        Article article1 = new Article();
        article1.setId(article.getId());

        Example<Article> articleExample = Example.of(article1);

        Optional<Article> one = basicService.articleRepository.findOne(articleExample);
        Article oldArticle = one.isPresent() ? one.get() : null;

        article.setCreateTime(oldArticle.getCreateTime());
        article.setReadCounts(oldArticle.getReadCounts());
        article.setReceiveLikeCounts(oldArticle.getReceiveLikeCounts());
        article.setCommentCounts(oldArticle.getCommentCounts());
        article.setUpdateTime(new Date());
        Article result = basicService.articleRepository.save(article);

        return result == null ? false : true;
    }

    private List<Article> readAndExcute(String property, ExampleMatcher matching, Article article, Integer page, Integer pageSize) {
        ExampleMatcher exampleMatcher = matching.withMatcher(property, ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Article> articleExample = Example.of(article, exampleMatcher);
        Pageable pageable = PageRequest.of(page, pageSize);

        Query query = new Query();
        query.addCriteria(Criteria.byExample(articleExample));
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        query.with(sort);
        query.fields().exclude("content");
        query.with(pageable);
        return basicService.mongoTemplate.find(query, Article.class);
    }

    private long readAndExcuteCount(String property, ExampleMatcher matching, Article article, Integer page, Integer pageSize) {
        ExampleMatcher exampleMatcher = matching.withMatcher(property, ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Article> articleExample = Example.of(article, exampleMatcher);
        Pageable pageable = PageRequest.of(page, pageSize);

        Query query = new Query();
        query.addCriteria(Criteria.byExample(articleExample));
        query.with(pageable);
        return basicService.mongoTemplate.count(query, Article.class);
    }


}
