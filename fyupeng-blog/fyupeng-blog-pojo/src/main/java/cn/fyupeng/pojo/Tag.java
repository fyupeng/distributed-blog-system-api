package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


@ApiModel(description = "操作文章标签的model")
public class Tag implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "标签标识符[id], 影响结果一致性的操作使用到该字段")
    private String id;

    /**
     * 标签名
     */
    @ApiModelProperty(value = "文章标签名", required = true, example = "Java/Linux")
    private String name;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户id", example = "123456789")
    private String userId;

    /**
     * 标签封面 id
     */
    @ApiModelProperty(value = "标签封面id", example = "123456789")
    private String tag_cover_id;

    public Tag() {
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

    public String getTag_cover_id() {
        return tag_cover_id;
    }

    public void setTag_cover_id(String tag_cover_id) {
        this.tag_cover_id = tag_cover_id;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", tag_cover_id='" + tag_cover_id + '\'' +
                '}';
    }
}