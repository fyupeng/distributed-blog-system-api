package cn.fyupeng.mapper;


import cn.fyupeng.pojo.Picture;
import cn.fyupeng.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PictureMapper extends MyMapper<Picture> {

    public int insertListNoIncr(@Param("list") List<Picture> pictureList);

}