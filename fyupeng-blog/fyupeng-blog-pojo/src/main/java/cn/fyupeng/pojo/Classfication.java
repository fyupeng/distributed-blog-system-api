package cn.fyupeng.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "操作文章分类的model")
public class Classfication implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "分类id, 影响结果一致性的操作使用到该字段")
    private String id;

    /**
     * 分类名
     */
    @ApiModelProperty(value = "分类名", required = true, example = "算法/SQL")
    private String name;


    public Classfication() {
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
     * 获取分类名
     *
     * @return name - 分类名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置分类名
     *
     * @param name 分类名
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Classfication{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}