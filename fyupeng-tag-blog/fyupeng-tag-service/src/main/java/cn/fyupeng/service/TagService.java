package cn.fyupeng.service;

import cn.fyupeng.pojo.Articles2tags;
import cn.fyupeng.pojo.Tag;
import cn.fyupeng.pojo.vo.Articles2tagsVO;
import cn.fyupeng.pojo.vo.TagVO;

import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/8
 * @Description:
 * @Package: com.crop.service
 * @Version: 1.0
 */
public interface TagService {

    boolean queryTagIsExist(Tag tag);

    boolean queryArticleTagIsExist(String id);

    boolean queryArticleTagIsExist(Articles2tags articles2tags);

    List<TagVO> queryAllTags(Tag tag);

    boolean saveTag(Tag tag);

    boolean updateTag(Tag tag);

    boolean deleteTag(String tagId);

    void delArticleTag(String tagId);

    boolean deleteTagAndArticleTagWithTagId(String tagId);

    Tag queryTag(String tagId);

    List<Articles2tagsVO> queryArticleTag(Articles2tags articles2tags);

    boolean saveArticleTag(Articles2tags articles2tags);

    boolean updateArticleTag(Articles2tags articles2tags);

    boolean deleteTagAndArticleTagWithArticleId(String articleId);
}
