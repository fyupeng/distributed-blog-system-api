package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "操作文章标签标记的model")
public class Articles2tags implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "关联id")
    private String id;

    /**
     * 文章id
     */
    @ApiModelProperty(value = "文章id", required = true)
    private String articleId;

    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id", required = true)
    private String tagId;

    public Articles2tags() {
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

    @Override
    public String toString() {
        return "Articles2tags{" +
                "id='" + id + '\'' +
                ", articleId='" + articleId + '\'' +
                ", tagId='" + tagId + '\'' +
                '}';
    }
}