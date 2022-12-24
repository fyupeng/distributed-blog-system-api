package cn.fyupeng.service.impl;

import cn.fyupeng.mapper.*;
import cn.fyupeng.pojo.*;
import cn.fyupeng.pojo.vo.Articles2tagsVO;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.utils.TimeAgoUtils;
import cn.fyupeng.pojo.vo.ArticleVO;
import cn.fyupeng.service.ArticleService;
import cn.fyupeng.anotion.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
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

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserInfoMapper userinfoMapper;

    @Autowired
    private ClassficationMapper classficationMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private Articles2tagsMapper articles2tagsMapper;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private Sid sid;

    @PostConstruct
    public void init() {
        this.basicService = this;
    }

    @Override
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
            List<Articles2tagsVO> select = queryArticleTag(articles2tags);
            if (select.size() != 0) {
                Tag tag = basicService.tagMapper.selectByPrimaryKey(select.get(0).getTagId());
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

    private List<Articles2tagsVO> queryArticleTag(Articles2tags articles2tags) {

        List<Articles2tags> articles2tagsList = basicService.articles2tagsMapper.select(articles2tags);

        for (Articles2tags tags : articles2tagsList) {
            System.out.println(tags);
        }

        List<Articles2tagsVO> articles2tagsVOList = new ArrayList<>();
        for (Articles2tags articleTags : articles2tagsList) {

            String articleId = articleTags.getArticleId();
            String tagId = articleTags.getTagId();

            Tag tag = basicService.tagMapper.selectByPrimaryKey(tagId);

            Article article = new Article();
            article.setId(articleId);

            org.springframework.data.domain.Example<Article> articleExample = org.springframework.data.domain.Example.of(article);


            Optional<Article> articleOptional = basicService.articleRepository.findOne(articleExample);
            Article one = articleOptional.isPresent() ? articleOptional.get() : null;

            if (one != null) {

                Classfication classfication = basicService.classficationMapper.selectByPrimaryKey(one.getClassId());

                Articles2tagsVO articles2tagsVO = new Articles2tagsVO();
                BeanUtils.copyProperties(articleTags, articles2tagsVO);

                articles2tagsVO.setTagName(tag.getName());
                articles2tagsVO.setTitle(one.getTitle());
                articles2tagsVO.setClassficationName(classfication.getName());
                articles2tagsVO.setCommentCounts(one.getCommentCounts());
                articles2tagsVO.setReadCounts(one.getReadCounts());
                articles2tagsVO.setReceiveLikeCounts(one.getReceiveLikeCounts());

                String createTimeAgo = TimeAgoUtils.format(one.getCreateTime());
                String updateTimaAgo = TimeAgoUtils.format(one.getUpdateTime());

                articles2tagsVO.setCreateTimeAgoStr(createTimeAgo);
                articles2tagsVO.setUpdateTimeAgoStr(updateTimaAgo);

                articles2tagsVOList.add(articles2tagsVO);
            }
        }

        return articles2tagsVOList;
    }

    @Override
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
