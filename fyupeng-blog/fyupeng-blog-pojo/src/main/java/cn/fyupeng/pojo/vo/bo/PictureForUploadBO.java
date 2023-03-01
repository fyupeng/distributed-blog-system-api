package cn.fyupeng.pojo.vo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.awt.print.PrinterAbortException;
import java.io.Serializable;

/**
 * @Auther: fyp
 * @Date: 2022/12/11
 * @Description:
 * @Package: cn.fyupeng.pojo.bo
 * @Version: 1.0
 */
@ApiModel(value = "操作图片上传的Model")
public class PictureForUploadBO implements Serializable {

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
     * 图片路径
     */
    @ApiModelProperty(hidden = true)
    private String picturePath;

    public PictureForUploadBO() {
    }

    public String getPictureDesc() {
        return pictureDesc;
    }

    public void setPictureDesc(String pictureDesc) {
        this.pictureDesc = pictureDesc;
    }

    public Double getPictureWidth() {
        return pictureWidth;
    }

    public void setPictureWidth(Double pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    public Double getPictureHeight() {
        return pictureHeight;
    }

    public void setPictureHeight(Double pictureHeight) {
        this.pictureHeight = pictureHeight;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
