package cn.fyupeng.service.impl;

import cn.fyupeng.anotion.Service;
import cn.fyupeng.mapper.PictureMapper;
import cn.fyupeng.pojo.Picture;
import cn.fyupeng.pojo.vo.PictureVO;
import cn.fyupeng.service.PictureService;
import cn.fyupeng.utils.PagedResult;
import cn.fyupeng.utils.TimeAgoUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/4/1
 * @Description:
 * @Package: com.crop.service.impl
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Service
@Component
public class PictureServiceImpl implements PictureService {

    private static PictureServiceImpl basicService;

    @Autowired
    private Sid sid;

    @Autowired
    private PictureMapper pictureMapper;

    @PostConstruct
    public void init() {
        basicService = this;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryPictureIsExist(Picture picture) {
        List<Picture> result = basicService.pictureMapper.select(picture);

        return (result == null || result.size() == 0) ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Picture queryPicture(Picture picture) {

        List<Picture> result = basicService.pictureMapper.select(picture);

        return (result == null || result.size() == 0) ? null : result.get(0);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult getAllPictures(Picture picture, Integer page, Integer pageSize) {

        //分页查询对象
        if(page <= 0){
            page = 1;
        }

        page = page - 1;

        if(pageSize <= 0){
            pageSize = 10;
        }

        PageHelper pageHelper = new PageHelper();
        pageHelper.startPage(page, pageSize);

        List<Picture> pictureList = basicService.pictureMapper.select(picture);


        List<PictureVO> pictureVOList = new ArrayList<>();
        for (Picture pt : pictureList) {

            PictureVO pictureVO = new PictureVO();
            BeanUtils.copyProperties(pt, pictureVO);

            String uploadTimeAgoStr = TimeAgoUtils.format(pt.getUploadTime());
            pictureVO.setUploadTimeAgoStr(uploadTimeAgoStr);

            pictureVOList.add(pictureVO);
        }

        PageInfo<PictureVO> pageInfo = new PageInfo<>(pictureVOList);

        PagedResult pagedResult = new PagedResult();

        pagedResult.setRows(pageInfo.getList());
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setPage(page);

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updatePicture(Picture picture) {

        int i = basicService.pictureMapper.updateByPrimaryKeySelective(picture);

        return i > 0 ? true : false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void upload(Picture picture) {
        String pictureId = basicService.sid.nextShort();

        picture.setId(pictureId);
        picture.setUploadTime(new Date());

        basicService.pictureMapper.insert(picture);

    }

    @Override
    public void upload(List<Picture> pictures) {
        for (Picture picture : pictures) {
            String pictureId = basicService.sid.nextShort();
            System.out.println(pictureId);
            picture.setId(pictureId);
            picture.setUploadTime(new Date());
        }
        //basicService.pictureMapper.insertList(pictures);
        basicService.pictureMapper.insertListNoIncr(pictures);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deletePicture(Picture picture) {

        int i = basicService.pictureMapper.delete(picture);

        return i > 0 ? true : false;
    }


}
