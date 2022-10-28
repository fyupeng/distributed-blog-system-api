package cn.fyupeng.pojo.vo;

public class TagVO {
    /**
     * 主键
     */
    private String id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 用户id
     */
    private String userId;

    private Integer articleNum;

    public TagVO() {
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
     * 获取标签名
     *
     * @return name - 标签名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置标签名
     *
     * @param name 标签名
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    @Override
    public String toString() {
        return "TagVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", articleNum=" + articleNum +
                '}';
    }
}