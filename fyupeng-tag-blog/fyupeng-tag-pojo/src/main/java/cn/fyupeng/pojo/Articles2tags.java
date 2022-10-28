package cn.fyupeng.pojo;

import javax.persistence.Column;
import javax.persistence.Id;

public class Articles2tags {
    @Id
    private String id;

    @Column(name = "article_id")
    private String articleId;

    @Column(name = "tag_id")
    private String tagId;

    public Articles2tags() {
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
     * @return article_id
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * @param articleId
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    /**
     * @return tag_id
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * @param tagId
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "Articles2tags{" +
                "id='" + id + '\'' +
                ", articleId='" + articleId + '\'' +
                ", tagId='" + tagId + '\'' +
                '}';
    }
}