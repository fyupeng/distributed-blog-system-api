package cn.fyupeng.pojo.vo;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class CommentVO implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 自关联，评论父id，如果为null，则是直接发言
     */
    private String fatherCommentId;

    /**
     * 被追问的用户id，如果为null,则是直接发言
     */
    private String toUserId;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 留言者，评论的用户id
     */
    private String fromUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 评论内容
     */
    private String comment;

    private String normalCreateTime;

    private String fromUserAvatar;
    private String fromUserNickName;
    private String toUserNickName;
    private String createTimeAgo;

    private String fatherCommentContent;

    /**
     * 状态位
     * 1 - 正常
     * 2- 屏蔽
     */
    private Integer status;

    public CommentVO() {
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取自关联，评论父id，如果为null，则是直接发言
     *
     * @return father_comment_id - 自关联，评论父id，如果为null，则是直接发言
     */
    public String getFatherCommentId() {
        return fatherCommentId;
    }

    /**
     * 设置自关联，评论父id，如果为null，则是直接发言
     *
     * @param fatherCommentId 自关联，评论父id，如果为null，则是直接发言
     */
    public void setFatherCommentId(String fatherCommentId) {
        this.fatherCommentId = fatherCommentId;
    }

    /**
     * 获取被追问的用户id，如果为null,则是直接发言
     *
     * @return to_user_id - 被追问的用户id，如果为null,则是直接发言
     */
    public String getToUserId() {
        return toUserId;
    }

    /**
     * 设置被追问的用户id，如果为null,则是直接发言
     *
     * @param toUserId 被追问的用户id，如果为null,则是直接发言
     */
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    /**
     * 获取文章id
     *
     * @return article_id - 文章id
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * 设置文章id
     *
     * @param articleId 文章id
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    /**
     * 获取留言者，评论的用户id
     *
     * @return from_user_id - 留言者，评论的用户id
     */
    public String getFromUserId() {
        return fromUserId;
    }

    /**
     * 设置留言者，评论的用户id
     *
     * @param fromUserId 留言者，评论的用户id
     */
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取评论内容
     *
     * @return comment - 评论内容
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置评论内容
     *
     * @param comment 评论内容
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public String getFromUserNickName() {
        return fromUserNickName;
    }

    public void setFromUserNickName(String fromUserNickName) {
        this.fromUserNickName = fromUserNickName;
    }

    public String getToUserNickName() {
        return toUserNickName;
    }

    public void setToUserNickName(String toUserNickName) {
        this.toUserNickName = toUserNickName;
    }

    public String getCreateTimeAgo() {
        return createTimeAgo;
    }

    public void setCreateTimeAgo(String createTimeAgo) {
        this.createTimeAgo = createTimeAgo;
    }

    public String getNormalCreateTime() {
        return normalCreateTime;
    }

    public void setNormalCreateTime(String normalCreateTime) {
        this.normalCreateTime = normalCreateTime;
    }

    public String getFatherCommentContent() {
        return fatherCommentContent;
    }

    public void setFatherCommentContent(String fatherCommentContent) {
        this.fatherCommentContent = fatherCommentContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CommentVO{" +
                "id='" + id + '\'' +
                ", fatherCommentId='" + fatherCommentId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", articleId='" + articleId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", createTime=" + createTime +
                ", comment='" + comment + '\'' +
                ", normalCreateTime='" + normalCreateTime + '\'' +
                ", fromUserAvatar='" + fromUserAvatar + '\'' +
                ", fromUserNickName='" + fromUserNickName + '\'' +
                ", toUserNickName='" + toUserNickName + '\'' +
                ", createTimeAgo='" + createTimeAgo + '\'' +
                ", fatherCommentContent='" + fatherCommentContent + '\'' +
                ", status=" + status +
                '}';
    }
}