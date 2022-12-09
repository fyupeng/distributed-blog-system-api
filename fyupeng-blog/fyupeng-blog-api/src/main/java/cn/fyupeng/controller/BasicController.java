package cn.fyupeng.controller;

import cn.fyupeng.loadbalancer.RoundRobinLoadBalancer;
import cn.fyupeng.net.netty.client.NettyClient;
import cn.fyupeng.proxy.RpcClientProxy;
import cn.fyupeng.serializer.CommonSerializer;
import cn.fyupeng.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URL;


@RestController
public class BasicController {
    private static final RoundRobinLoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer();
    private static final NettyClient nettyClient = new NettyClient(roundRobinLoadBalancer, CommonSerializer.HESSIAN_SERIALIZER);

    protected RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);

    @Autowired
    public RedisOperator redis;

    public  static final String DATA_NAME = "distributed-blog-data";

    //文件保存的命名空间
    public static final String FILE_SPACE =
            System.getProperties().getProperty("user.home") + File.separator + "webapps" + File.separator + DATA_NAME + File.separator;

    public static final String URL_SPACE =
             "/" + DATA_NAME + "/" + "**";
    //ffmpeg所在目录
    public static final String FFMPEG_EXE = "ffmpeg";//需要预先设置好ffmpeg的环境变量
    //每页分页的记录数
    public static final Integer ARTICLE_PAGE_SIZE = 6;
    // 评论分页
    public static final Integer COMMENT_PAGE_SIZE = 10;
    // 图片分页
    public static final Integer PICTURE_PAGE_SIZE = 10;

    public static final Integer SEARCH_SIZE = 10;

    public static final Long ONE_WEEK = 604800000L;

    public static final Long TWO_WEEK = 1209600000L;

    public static final Long ONE_MONTH = 2592000000L; // 30天

    public static final Long ONE_YEAR = 31536000000L;

}
