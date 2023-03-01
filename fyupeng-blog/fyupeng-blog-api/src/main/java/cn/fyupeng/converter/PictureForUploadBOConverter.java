package cn.fyupeng.converter;


import cn.fyupeng.pojo.vo.bo.PictureForUploadBO;
import cn.fyupeng.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/12/11
 * @Description: 图片上传业务对象转换器
 * @Package: cn.fyupeng.converter
 * @Version: 1.0
 */

@Slf4j
@Configuration
public class PictureForUploadBOConverter implements Converter<String, List<PictureForUploadBO>> {

    /**
     * [
     *   {
     *     "pictureDesc":"图片描述1",
     *     "pictureWidth":"100",
     *     "pictureHeight":"120"
     *   },
     *   {
     *     "pictureDesc":"图片描述2",
     *     "pictureWidth":"100",
     *     "pictureHeight":"120"
     *   }
     * ]
     * @param pictureBOs
     * @return
     */
    @Override
    public List<PictureForUploadBO> convert(String pictureBOs) {
        log.info("source string: {}", pictureBOs);
        return JsonUtils.jsonToList(pictureBOs, PictureForUploadBO.class);
    }
}
