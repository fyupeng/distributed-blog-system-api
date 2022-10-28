package cn.fyupeng.service;

import cn.fyupeng.pojo.Classfication;

import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/3
 * @Description:
 * @Package: com.crop.service
 * @Version: 1.0
 */
public interface ClassficationService {
    boolean queryClassficationIdIsExist(String classId);

    boolean saveClassfication(Classfication classfication);

    Classfication queryClassfication(Classfication classfication);

    List<Classfication> queryAllClassfications();

    boolean deleteClassfication(String classficationId);

    boolean updateClassfication(Classfication clssfication);
}
