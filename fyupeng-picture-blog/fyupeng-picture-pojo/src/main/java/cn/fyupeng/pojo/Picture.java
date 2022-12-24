package cn.fyupeng.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.swing.text.html.HTMLDocument;

public class Picture implements Serializable {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 上传的图片用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 图片路径
     */
    @Column(name = "picture_path")
    private String picturePath;

    /**
     * 图片描述
     */
    @Column(name = "picture_desc")
    private String pictureDesc;

    /**
     * 图片宽度
     */
    @Column(name = "picture_width")
    private Double pictureWidth;

    /**
     * 图片高度
     */
    @Column(name = "picture_height")
    private Double pictureHeight;

    /**
     * 图片上传时间
     */
    @Column(name = "upload_time")
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