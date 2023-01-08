package cn.fyupeng.pojo.vo;

import java.io.Serializable;

public class Articles2tagsVO implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 标签id
     */
    private String tagId;

    private String title;
    private String tagName;
    private Integer commentCounts;
    private Integer readCounts;
    private Integer receiveLikeCounts;
    private String createTimeAgoStr;
    private String updateTimeAgoStr;
    private String classficationName;

    private String articleCoverId;
    private String articleCoverUrl;

    public Articles2tagsVO() {
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
     * 获取标签id
     *
     * @return tag_id - 标签id
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * 设置标签id
     *
     * @param tagId 标签id
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public String toString() {
        return "Articles2tagsVO{" +
                "id='" + id + '\'' +
                ", articleId='" + articleId + '\'' +
                ", tagId='" + tagId + '\'' +
                ", title='" + title + '\'' +
                ", tagName='" + tagName + '\'' +
                ", commentCounts=" + commentCounts +
                ", readCounts=" + readCounts +
                ", receiveLikeCounts=" + receiveLikeCounts +
                ", createTimeAgoStr='" + createTimeAgoStr + '\'' +
                ", updateTimeAgoStr='" + updateTimeAgoStr + '\'' +
                ", classficationName='" + classficationName + '\'' +
                ", articleCoverId='" + articleCoverId + '\'' +
                ", articleCoverUrl='" + articleCoverUrl + '\'' +
                '}';
    }
}
