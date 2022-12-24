package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "操作图片上传和其他图片操作的model")
public class Picture implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "文章id, 影响结果一致性的操作使用到该字段")
    private String id;

    /**
     * 上传的图片用户id
     */
    @ApiModelProperty(value = "用户标识符[id]", required = true, example = "123456789")
    private String userId;

    /**
     * 图片路径
     */
    @ApiModelProperty(hidden = true)
    private String picturePath;

    /**
     * 图片描述
     */
    @ApiModelProperty(value = "图片描述", required = true, example = "这是图片描述....")
    private String pictureDesc;

    /**
     * 图片宽度
     */
    @ApiModelProperty(value = "图片宽度", example = "111.11")
    private Double pictureWidth;

    /**
     * 图片高度
     */
    @ApiModelProperty(value = "图片高度", example = "111.11")
    private Double pictureHeight;

    /**
     * 图片上传时间
     */
    @ApiModelProperty(hidden = true)
    private Date uploadTime;

    public Picture() {
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
     * 获取上传的图片用户id
     *
     * @return user_id - 上传的图片用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置上传的图片用户id
     *
     * @param userId 上传的图片用户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取图片路径
     *
     * @return picture_path - 图片路径
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * 设置图片路径
     *
     * @param picturePath 图片路径
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**
     * 获取图片描述
     *
     * @return picture_desc - 图片描述
     */
    public String getPictureDesc() {
        return pictureDesc;
    }

    /**
     * 设置图片描述
     *
     * @param pictureDesc 图片描述
     */
    public void setPictureDesc(String pictureDesc) {
        this.pictureDesc = pictureDesc;
    }

    /**
     * 获取图片宽度
     *
     * @return picture_width - 图片宽度
     */
    public Double getPictureWidth() {
        return pictureWidth;
    }

    /**
     * 设置图片宽度
     *
     * @param pictureWidth 图片宽度
     */
    public void setPictureWidth(Double pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    /**
     * 获取图片高度
     *
     * @return picture_height - 图片高度
     */
    public Double getPictureHeight() {
        return pictureHeight;
    }

    /**
     * 设置图片高度
     *
     * @param pictureHeight 图片高度
     */
    public void setPictureHeight(Double pictureHeight) {
        this.pictureHeight = pictureHeight;
    }

    /**
     * 获取图片上传时间
     *
     * @return upload_time - 图片上传时间
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置图片上传时间
     *
     * @param uploadTime 图片上传时间
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", pictureDesc='" + pictureDesc + '\'' +
                ", pictureWidth=" + pictureWidth +
                ", pictureHeight=" + pictureHeight +
                ", uploadTime=" + uploadTime +
                '}';
    }
}