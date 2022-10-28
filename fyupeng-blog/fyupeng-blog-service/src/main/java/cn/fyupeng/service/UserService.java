package cn.fyupeng.service;

import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.UserInfo;

/**
 * @Auther: fyp
 * @Date: 2022/8/17
 * @Description: 用户业务服务接口
 * @Package: cn.fyupeng.service
 * @Version: 1.0
 */
public interface UserService {

    /**
     * @Description: 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * @Description: 用户登录，根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    public User queryUserForLogin(String username, String password);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    User queryUser(String userId);

    /**
     * 查询用户详细信息
     * @param userId
     * @return
     */
    public UserInfo queryUserInfo(String userId);

    /**
     * 查询用户是否存在
     * @param userId
     * @return
     */
    boolean queryUserIdIsExist(String userId);
    /**
     * @Description: 用户修改信息
     * @param user
     */
    public boolean updateUser(User user);

    /**
     * @Description: 用户修改详细信息
     * @param user
     */

    /**
     * @Description: 保存用户（注册用户）
     * @param user
     */
    public void saveUser(User user);

    public void updateUserInfo(UserInfo user);

}
