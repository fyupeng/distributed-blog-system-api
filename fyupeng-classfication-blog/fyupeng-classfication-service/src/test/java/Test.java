import cn.fyupeng.loadbalancer.RoundRobinLoadBalancer;
import cn.fyupeng.net.netty.client.NettyClient;
import cn.fyupeng.pojo.Classfication;
import cn.fyupeng.proxy.RpcClientProxy;
import cn.fyupeng.serializer.CommonSerializer;
import cn.fyupeng.service.ClassficationService;
import cn.fyupeng.utils.BlogJSONResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
   private static ClassficationService classficationServiceProxy = rpcClientProxy.getProxy(ClassficationService.class, Test.class);

   public static void main(String[] args) throws UnsupportedEncodingException {
      Classfication classfication = new Classfication();
      classfication.setName("算法");
      classfication.setId("");
      Classfication cf = classficationServiceProxy.queryClassfication(classfication);
      //List<Classfication> classfications = classficationServiceProxy.queryAllClassfications();
      System.out.println(cf);
   }

}
