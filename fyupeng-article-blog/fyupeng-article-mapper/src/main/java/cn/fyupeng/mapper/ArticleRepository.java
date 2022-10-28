package cn.fyupeng.mapper;

import cn.fyupeng.pojo.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Auther: fyp
 * @Date: 2022/4/3
 * @Description:
 * @Package: com.crop.mapper
 * @Version: 1.0
 */
public interface ArticleRepository extends MongoRepository<Article, String> {

}
