package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "评论id, 影响结果一致性的操作使用到该字段", example = "123456789")
    private String id;

    /**
     * 自关联，评论父id，如果为null，则是直接发言
     */
    @ApiModelProperty(value = "父评论标识符[id]", example = "123456789")
    private String fatherCommentId;

    /**
     * 被追问的用户id，如果为null,则是直接发言
     */
    @ApiModelProperty(value = "被回复用户标识符[id]", example = "123456789")
    private String toUserId;

    /**
     * 文章id
     */
    @ApiModelProperty(value = "评论所属文章标识符[id]", required = true, example = "123456789")
    private String articleId;

    /**
     * 留言者，评论的用户id
     */
    @ApiModelProperty(value = "留言者，评论用户标识符[id]", required = true, example = "123456789")
    private String fromUserId;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true, example = "2018-12-04T13:46:56.711Z")
    private Date createTime;

    /**
     * 评论内容
     */
    @ApiModelProperty(value = "评论内容", required = true, example = "这是评论内容....")
    private String comment;

    /**
     * 评论状态
     *  0 - 正常状态
     *  1 - 屏蔽
     */
    @ApiModelProperty(value = "状态", hidden = true, example = "0[表示评论处于正常状态]/1[表示评论被屏蔽]")
    private Integer status;


    public Comment() {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", fatherCommentId='" + fatherCommentId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", articleId='" + articleId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", createTime=" + createTime +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                '}';
    }
}