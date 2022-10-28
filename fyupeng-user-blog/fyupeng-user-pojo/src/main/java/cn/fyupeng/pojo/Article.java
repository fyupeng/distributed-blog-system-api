package cn.fyupeng.pojo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Document(collection = "article")
public class Article {
    @Id
    private String id;

    private String title;

    @Indexed
    @Column(name = "user_id")
    private String userId;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private String summary;

    private String content;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "comment_counts")
    private Integer commentCounts;

    @Column(name = "read_counts")
    private Integer readCounts;

    @Column(name = "receive_like_counts")
    private Integer receiveLikeCounts;

    public Article() {
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return class_id
     */
    public String getClassId() {
        return classId;
    }

    /**
     * @param classId
     */
    public void setClassId(String classId) {
        this.classId = classId;
    }

    /**
     * @return comment_counts
     */
    public Integer getCommentCounts() {
        return commentCounts;
    }

    /**
     * @param commentCounts
     */
    public void setCommentCounts(Integer commentCounts) {
        this.commentCounts = commentCounts;
    }

    /**
     * @return read_counts
     */
    public Integer getReadCounts() {
        return readCounts;
    }

    /**
     * @param readCounts
     */
    public void setReadCounts(Integer readCounts) {
        this.readCounts = readCounts;
    }

    /**
     * @return receive_like_counts
     */
    public Integer getReceiveLikeCounts() {
        return receiveLikeCounts;
    }

    /**
     * @param receiveLikeCounts
     */
    public void setReceiveLikeCounts(Integer receiveLikeCounts) {
        this.receiveLikeCounts = receiveLikeCounts;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", classId='" + classId + '\'' +
                ", commentCounts=" + commentCounts +
                ", readCounts=" + readCounts +
                ", receiveLikeCounts=" + receiveLikeCounts +
                '}';
    }
}