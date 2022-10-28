package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "操作用户收藏文章的model")
public class UserLikeArticles {
    /**
     * 主键
     */
    @ApiModelProperty
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty
    private String userId;

    /**
     * 文章id
     */
    @ApiModelProperty
    private String articleId;

    public UserLikeArticles() {
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
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "UserLikeArticles{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", articleId='" + articleId + '\'' +
                '}';
    }
}