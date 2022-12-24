package cn.fyupeng.pojo.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: fyp
 * @Date: 2022/4/4
 * @Description:
 * @Package: com.crop.pojo.vo
 * @Version: 1.0
 */
public class ArticleVO implements Comparable, Serializable {

    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容，4000以内建议使用varchar，超过建议clob
     */
    private String content;

    /**
     * 分类id
     */
    private String classId;

    /**
     * 评论数
     */
    private Integer commentCounts;

    /**
     * 阅读量
     */
    private Integer readCounts;

    /**
     * 收藏量，用户喜欢、收藏
     */
    private Integer receiveLikeCounts;

    private String normalCreateTime;
    private String normalUpdateTime;

    private String createTimeAgoStr;
    private String updateTimeAgoStr;
    private String classficationName;
    private String nickName;
    private String avatar;

    // 目前只做 一篇 文章 对于 一个 标签
    private String tagId;
    private String tagName;
    private String articleCoverId;
    private String articleCoverUrl;

    public ArticleVO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Integer getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(Integer commentCounts) {
        this.commentCounts = commentCounts;
    }

    public Integer getReadCounts() {
        return readCounts;
    }

    public void setReadCounts(Integer readCounts) {
        this.readCounts = readCounts;
    }

    public Integer getReceiveLikeCounts() {
        return receiveLikeCounts;
    }

    public void setReceiveLikeCounts(Integer receiveLikeCounts) {
        this.receiveLikeCounts = receiveLikeCounts;
    }

    public String getCreateTimeAgoStr() {
        return createTimeAgoStr;
    }

    public void setCreateTimeAgoStr(String createTimeAgoStr) {
        this.createTimeAgoStr = createTimeAgoStr;
    }

    public String getUpdateTimeAgoStr() {
        return updateTimeAgoStr;
    }

    public void setUpdateTimeAgoStr(String updateTimeAgoStr) {
        this.updateTimeAgoStr = updateTimeAgoStr;
    }

    public String getClassficationName() {
        return classficationName;
    }

    public void setClassficationName(String classficationName) {
        this.classficationName = classficationName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNormalCreateTime() {
        return normalCreateTime;
    }

    public void setNormalCreateTime(String normalCreateTime) {
        this.normalCreateTime = normalCreateTime;
    }

    public String getNormalUpdateTime() {
        return normalUpdateTime;
    }

    public void setNormalUpdateTime(String normalUpdateTime) {
        this.normalUpdateTime = normalUpdateTime;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getArticleCoverId() {
        return articleCoverId;
    }

    public void setArticleCoverId(String articleCoverId) {
        this.articleCoverId = articleCoverId;
    }

    public String getArticleCoverUrl() {
        return articleCoverUrl;
    }

    public void setArticleCoverUrl(String articleCoverUrl) {
        this.articleCoverUrl = articleCoverUrl;
    }

    @Override
    public int compareTo(Object o) {
        return this.getCreateTime().getTime() > ((ArticleVO) o).getCreateTime().getTime() ? -1 : 1;
    }

    @Override
    public String toString() {
        return "ArticleVO{" +
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
                ", normalCreateTime='" + normalCreateTime + '\'' +
                ", normalUpdateTime='" + normalUpdateTime + '\'' +
                ", createTimeAgoStr='" + createTimeAgoStr + '\'' +
                ", updateTimeAgoStr='" + updateTimeAgoStr + '\'' +
                ", classficationName='" + classficationName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", tagId='" + tagId + '\'' +
                ", tagName='" + tagName + '\'' +
                ", articleCoverId='" + articleCoverId + '\'' +
                ", articleCoverUrl='" + articleCoverUrl + '\'' +
                '}';
    }
}
