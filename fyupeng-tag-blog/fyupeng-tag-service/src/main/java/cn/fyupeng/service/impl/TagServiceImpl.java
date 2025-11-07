package cn.fyupeng.service.impl;

import cn.fyupeng.annotation.DataSourceSwitcher;
import cn.fyupeng.enums.DataSourceEnum;
import cn.fyupeng.mapper.*;
import cn.fyupeng.pojo.*;
import cn.fyupeng.utils.TimeAgoUtils;
import cn.fyupeng.pojo.vo.Articles2tagsVO;
import cn.fyupeng.pojo.vo.TagVO;
import cn.fyupeng.service.TagService;
import cn.fyupeng.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: fyp
 * @Date: 2022/4/8
 * @Description:
 * @Package: com.crop.service.impl
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
@Service
public class TagServiceImpl implements TagService {

    private static TagServiceImpl basicService;

    @Lazy
    @Autowired
    private TagMapper tagMapper;

    @Lazy
    @Autowired
    private Articles2tagsMapper articles2tagsMapper;

    @Lazy
    @Autowired
    private ArticleRepository articleRepository;

    @Lazy
    @Autowired
    private ClassficationMapper classficationMapper;

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
    public boolean queryTagIsExist(Tag tag) {

        Tag result = basicService.tagMapper.selectOne(tag);

        return result == null ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    public boolean queryArticleTagIsExist(String id) {

        Articles2tags result = basicService.articles2tagsMapper.selectByPrimaryKey(id);

        return result == null ? false : true;
    }

    @Override
    public boolean queryArticleTagIsExist(Articles2tags articles2tags) {

        List<Articles2tags> result = basicService.articles2tagsMapper.select(articles2tags);

        return result.size() == 0 ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    public Tag queryTag(String tagId) {

        Tag tag = basicService.tagMapper.selectByPrimaryKey(tagId);

        return tag;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    public List<Articles2tagsVO> queryArticleTag(Articles2tags articles2tags) {

        List<Articles2tags> articles2tagsList = basicService.articles2tagsMapper.select(articles2tags);

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
                articles2tagsVO.setArticleCoverId(one.getArticleCoverId());

                String articleCoverId = one.getArticleCoverId();
                if (!StringUtils.isBlank(articleCoverId)) {
                    Picture queryPicture = new Picture();
                    queryPicture.setId(articleCoverId);
                    Picture pictureInfo = basicService.pictureMapper.selectOne(queryPicture);
                    articles2tagsVO.setArticleCoverUrl(pictureInfo.getPicturePath());
                }

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
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    public List<TagVO> queryAllTags(Tag tag) {

        List<Tag> select = basicService.tagMapper.select(tag);

        List<TagVO> tagVOList = new ArrayList<>();
        for (Tag one : select) {
            String tagId = one.getId();

            Articles2tags articles2tags = new Articles2tags();
            articles2tags.setTagId(tagId);
            List<Articles2tags> articles2tagsList = basicService.articles2tagsMapper.select(articles2tags);

            TagVO tagVO = new TagVO();

            BeanUtils.copyProperties(one, tagVO);

            tagVO.setArticleNum(articles2tagsList.size());

            tagVOList.add(tagVO);
        }

        return tagVOList;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTag(Tag tag) {

        String tagId = basicService.sid.nextShort();
        tag.setId(tagId);

        int i = basicService.tagMapper.insert(tag);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveArticleTag(Articles2tags articles2tags) {

        String articles2tagsId = basicService.sid.nextShort();
        articles2tags.setId(articles2tagsId);

        int i = basicService.articles2tagsMapper.insert(articles2tags);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleTag(Articles2tags articles2tags) {

        int i = basicService.articles2tagsMapper.updateByPrimaryKey(articles2tags);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTagAndArticleTagWithArticleId(String articleId) {
        Example example = new Example(Articles2tags.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("articleId", articleId);

        int i = basicService.articles2tagsMapper.deleteByExample(example);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTag(Tag tag) {

        int i = basicService.tagMapper.updateByPrimaryKeySelective(tag);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(String tagId) {

        int i = basicService.tagMapper.deleteByPrimaryKey(tagId);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public void delArticleTag(String tagId) {

        Example articles2tagsExample = new Example(Articles2tags.class);
        Example.Criteria criteria = articles2tagsExample.createCriteria();
        criteria.andEqualTo("tagId", tagId);

        int i = basicService.articles2tagsMapper.deleteByExample(articles2tagsExample);

    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTagAndArticleTagWithTagId(String tagId) {
        boolean result = deleteTag(tagId);
        if (result)
            delArticleTag(tagId);
        return result;
    }

}
