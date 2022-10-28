package cn.fyupeng.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "操作用户上传头像和其他详细操作的model")
public class UserInfo {
    /**
     * 主键
     */
    @ApiModelProperty(hidden = true)
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户标识符[id]", required = true)
    private String userId;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    /**
     * 我的头像，如果没有就默认给一张
     */
    @ApiModelProperty(hidden = true)
    private String avatar;

    /**
     * 名称
     */
    @ApiModelProperty(value = "别名")
    private String nickname;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话号码")
    private String tel;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 个人描述
     */
    @ApiModelProperty(value = "个人描述")
    private String description;

    public UserInfo() {
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
     * 获取性别
     * @return
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别
     * @param sex
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取我的头像，如果没有就默认给一张
     *
     * @return avatar - 我的头像，如果没有就默认给一张
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置我的头像，如果没有就默认给一张
     *
     * @param avatar 我的头像，如果没有就默认给一张
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取名称
     *
     * @return nickname - 名称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置名称
     *
     * @param nickname 名称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取电话
     *
     * @return tel - 电话
     */
    public String getTel() {
        return tel;
    }

    /**
     * 设置电话
     *
     * @param tel 电话
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * 获取邮箱
     *
     * @return email - 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", sex=" + sex +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}