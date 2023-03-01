package cn.fyupeng.service.impl;

import cn.fyupeng.annotation.DataSourceSwitcher;
import cn.fyupeng.enums.DataSourceEnum;
import cn.fyupeng.mapper.UserInfoMapper;
import cn.fyupeng.mapper.UserMapper;
import cn.fyupeng.pojo.User;
import cn.fyupeng.pojo.UserInfo;
import cn.fyupeng.service.UserService;
import cn.fyupeng.annotation.Service;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.PostConstruct;


@SuppressWarnings("all")
@Service
@Component
public class UserServiceImpl implements UserService {

    private static UserServiceImpl basicService;

    @Lazy
    @Autowired
    private UserMapper userMapper;

    @Lazy
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Lazy
    @Autowired
    private Sid sid;

    @PostConstruct
    public void init() {
        basicService = this;
    }

    public UserServiceImpl() {
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUserIdIsExist(String userId) {

        User user = new User();
        user.setId(userId);
        User result = basicService.userMapper.selectOne(user);

        return result == null ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {

        User user = new User();
        user.setUsername(username);
        User result = basicService.userMapper.selectOne(user);

        return result == null ? false : true;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public User queryUser(String userId) {
        Example userExample = new Example(User.class);

        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        User user = basicService.userMapper.selectOneByExample(userExample);

        return user;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public UserInfo queryUserInfo(String userId) {
        Example userInfoExample = new Example(UserInfo.class);

        Criteria criteria = userInfoExample.createCriteria();
        criteria.andEqualTo("userId", userId);
        UserInfo userInfo = basicService.userInfoMapper.selectOneByExample(userInfoExample);
        return userInfo;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public User queryUserForLogin(String username, String password){
        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        User result = basicService.userMapper.selectOneByExample(userExample);

        return result;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {

        String userId = basicService.sid.nextShort();
        String userInfoId = basicService.sid.nextShort();
        UserInfo userInfo = new UserInfo();

        user.setId(userId);

        userInfo.setId(userInfoId);
        userInfo.setUserId(userId);

        basicService.userMapper.insert(user);
        basicService.userInfoMapper.insert(userInfo);
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateUser(User user){
        int i = basicService.userMapper.updateByPrimaryKey(user);

        return i > 0 ? true : false;
        /**
         * 防止 在 controller 忘记 传进 id 值，将会导致 所有用户 被更改
         *  fix : 弃用
        if (StringUtils.isBlank(user.getId())) {
            return;
        }
        Example userExample = new Example(User.class);

        // id 值为空时， 会 匹配所有用户，危险 !!!
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        userMapper.updateByExampleSelective(user, userExample);
         */
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(UserInfo userInfo){

        Example userInfoExample = new Example(UserInfo.class);

        Criteria criteria = userInfoExample.createCriteria();
        criteria.andEqualTo("id", userInfo.getId());
        criteria.andEqualTo("userId", userInfo.getUserId());

        basicService.userInfoMapper.updateByExampleSelective(userInfo, userInfoExample);
    }



}
