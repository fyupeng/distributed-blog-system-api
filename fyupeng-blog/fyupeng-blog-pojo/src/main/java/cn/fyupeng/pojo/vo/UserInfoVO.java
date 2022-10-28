package cn.fyupeng.pojo.vo;


public class UserInfoVO {
    /**
     * 主键
     */
    private String id;

    private String userToken;

    /**
     * 用户id
     */
    private String userId;

    private Integer sex;

    /**
     * 我的头像，如果没有就默认给一张
     */
    private String avatar;

    /**
     * 名称
     */
    private String nickname;

    /**
     * 电话
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    private String description;

    private Integer permission;

    public UserInfoVO() {
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Integer getSex() {
        return sex;
    }

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

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "UserInfoVO{" +
                "id='" + id + '\'' +
                ", userToken='" + userToken + '\'' +
                ", userId='" + userId + '\'' +
                ", sex=" + sex +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                 ", permission=" + permission +
                '}';
    }
}