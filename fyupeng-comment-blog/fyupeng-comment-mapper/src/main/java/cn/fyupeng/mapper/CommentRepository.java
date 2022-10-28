package cn.fyupeng.mapper;

import cn.fyupeng.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Auther: fyp
 * @Date: 2022/4/3
 * @Description:
 * @Package: com.crop.mapper
 * @Version: 1.0
 */
public interface CommentRepository extends MongoRepository<Comment, String> {

}
