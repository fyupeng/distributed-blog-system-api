package cn.fyupeng.service;

import cn.fyupeng.pojo.Article;
import cn.fyupeng.pojo.vo.ArticleVO;
import cn.fyupeng.utils.PagedResult;

import java.util.List;
import java.util.Map;

/**
 * @Auther: fyp
 * @Date: 2022/4/2
 * @Description:
 * @Package: com.crop.service
 * @Version: 1.0
 */
public interface ArticleService {

    boolean queryArticleIsExist(String articleId);

    boolean queryArticleIsUser(Article article);

    boolean save(Article article);

    PagedResult queryArticleSelective(Article article, Integer page, Integer pageSize);

    ArticleVO queryArticleDetail(String articleId);

    boolean saveWithIdAndUserId(Article article);

    void multiUpdateArticleReadCounts(List<String> articleIdKeys, Map<String, String> articleMap);

    void removeArticle(String articleId);

    PagedResult queryArticleByTime(Long timeDifference, Integer page, Integer pageSize);

    List<ArticleVO> queryArticleWithNoneTagByUser(String userId);
}
