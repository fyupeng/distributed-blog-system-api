import cn.fyupeng.loadbalancer.RoundRobinLoadBalancer;
import cn.fyupeng.net.netty.client.NettyClient;
import cn.fyupeng.pojo.Article;
import cn.fyupeng.pojo.Articles2tags;
import cn.fyupeng.pojo.Classfication;
import cn.fyupeng.proxy.RpcClientProxy;
import cn.fyupeng.serializer.CommonSerializer;
import cn.fyupeng.service.ArticleService;
import cn.fyupeng.utils.PagedResult;

import java.io.UnsupportedEncodingException;

/**
 * @Auther: fyp
 * @Date: 2022/8/15
 * @Description:
 * @Package: PACKAGE_NAME
 * @Version: 1.0
 */
public class Test {

   private static final RoundRobinLoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer();
   private static final NettyClient nettyClient = new NettyClient(roundRobinLoadBalancer, CommonSerializer.KRYO_SERIALIZER);
   protected static RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
   private static ArticleService articleService = rpcClientProxy.getProxy(ArticleService.class, Test.class);

   public static void main(String[] args) throws UnsupportedEncodingException {
      Classfication classfication = new Classfication();
      Article article = new Article();
      article.setTitle("贪心");
      article.setSummary("贪心");
      article.setContent("贪心");
      PagedResult pagedResult = articleService.queryArticleSelective(article, null, null);
      //List<Classfication> classfications = classficationServiceProxy.queryAllClassfications();
      for (Object row : pagedResult.getRows()) {
         System.out.println(row);
      }


   }

}
