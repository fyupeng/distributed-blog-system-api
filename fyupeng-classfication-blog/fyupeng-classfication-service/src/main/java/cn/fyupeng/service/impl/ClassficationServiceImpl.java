package cn.fyupeng.service.impl;

import cn.fyupeng.annotation.DataSourceSwitcher;
import cn.fyupeng.enums.DataSourceEnum;
import cn.fyupeng.mapper.ClassficationMapper;
import cn.fyupeng.pojo.Classfication;
import cn.fyupeng.service.ClassficationService;
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
import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/3
 * @Description:
 * @Package: com.crop.service.impl
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
@Service
public class ClassficationServiceImpl implements ClassficationService {

    private static ClassficationServiceImpl basicService;

    @Lazy
    @Autowired
    private ClassficationMapper classficationMapper;

    @Lazy
    @Autowired
    private Sid sid;

    @PostConstruct
    public void init() {
        this.basicService = this;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryClassficationIdIsExist(String classId) {

        Classfication classfication = basicService.classficationMapper.selectByPrimaryKey(classId);

        return classfication == null ? false : true;
    }

    /**
     * 当前没有事务，以 无事务方式 执行，有 事务，多个查询线程 使用同一个 事务
     * @param classfication
     * @return
     */
    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Classfication queryClassfication(Classfication classfication) {

        List<Classfication> result = basicService.classficationMapper.select(classfication);

        return (result == null || result.size() == 0) ? null : result.get(0);
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.SLAVE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Classfication> queryAllClassfications() {

        Example classficationExample = new Example(Classfication.class);
        Criteria criteria = classficationExample.createCriteria();

        List<Classfication> result = basicService.classficationMapper.selectAll();


        return result;
    }


    /**
     * 当前没有其他事务，新建事务，这样该方法 调用多次 实际上是 多个事务，体现原子性
     * @param classfication
     */
    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveClassfication(Classfication classfication) {

        String classficationId = basicService.sid.nextShort();

        classfication.setId(classficationId);

        int i = basicService.classficationMapper.insert(classfication);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateClassfication(Classfication clssfication) {

        int i = basicService.classficationMapper.updateByPrimaryKeySelective(clssfication);

        return i > 0 ? true : false;
    }

    @Override
    @DataSourceSwitcher(DataSourceEnum.MASTER)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteClassfication(String classficationId) {

        int i = basicService.classficationMapper.deleteByPrimaryKey(classficationId);

        return i > 0 ? true : false;
    }



}







