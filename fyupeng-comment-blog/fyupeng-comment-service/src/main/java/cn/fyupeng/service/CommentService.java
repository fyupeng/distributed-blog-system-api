package cn.fyupeng.service;

import cn.fyupeng.pojo.Comment;
import cn.fyupeng.pojo.vo.CommentVO;
import cn.fyupeng.enums.CommentStatus;
import cn.fyupeng.utils.PagedResult;

import java.util.Date;
import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/3
 * @Description:
 * @Package: com.crop.service
 * @Version: 1.0
 */
public interface CommentService {

    boolean saveComment(Comment comment);


    void removeCommentById(String commentId);

    Comment queryComment(String commentId);

    boolean queryCommentIsExist(String commentId);

    boolean updateComment(Comment comment);

    PagedResult queryAllComments(String articleId, Integer page, Integer pageSize, Integer sort);

    boolean queryCommentWithFatherCommentIsExist(String commentId);

    void removeCommentWithFatherCommentId(String fatherCommentId);

    List<CommentVO> queryAllComments(String aPattern, String cPattern, String userId, Date startTime, Date endTime);

    void setCommentStatusWithFatherId(Comment comment, CommentStatus commentStatus);
}
