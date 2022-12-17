## 分布式博客管理后端接口api

### 介绍
提供多机器横向扩展服务、可靠性和可用性的 `api` 后端博客分布式管理项目

服务节点：
- 统一暴露接口服务：`8001`
- 用户服务：`8082`
- 分类服务：`8083`
- 文章服务：`8084`
- 评论服务：`8085`
- 标签服务：`8086`

### 软件架构
软件架构说明

- 项目分布式前后端代理架构设计：

![分布式前后端分离架构](https://yupeng-tuchuang.oss-cn-shenzhen.aliyuncs.com/分布式前后分离架构.png)

- 项目RPC架构设计：

![分布式博客微服务架构](https://yupeng-tuchuang.oss-cn-shenzhen.aliyuncs.com/分布式博客微服务架构.png)

提示：接口文档应用了`Nginx`代理技术，使用swagger-ui技术提供文档阅读，服务接口会代理不到真实服务。

接口文档：[http://localhost:9001/swagger-ui.html](http://localhost:8083/swagger-ui.html)


在架构设计中，一台服务器对外暴露接口，另一台作为存储信息服务，只对内提供服务接口，提高安全性。

数据库支持：`MySQL`、`Redis`、`MongoDB`

### 数据处理

- `MySQL` 处理关系型数据

`MySQL`对于处理关系型数据是最合适的，它在`POJO`与`PO`之间数据的映射是一对一的，所以在开发的时候可以使用逆向工具快速生成对应的`POJO`类，缩短开发时间和开发成本。

- MongoDB 处理非关系型数据

对于非关系型数据存储，采用`MongoDB`是比较合适的，比如本项目的评论和文章都采用这种方式存储，它们不仅支持`k-v`形式、文档形式、图片形式，还容易扩展，数据没有耦合性。

- `Redis` 处理缓存数据

对于可以使用弱一致性的，比如`CSDN`它的文章阅读量是每天中午会更新，不要求即时更新，就可以使用`Redis`来缓存数据，而且我在`Redis`数据库前面做了一层类似于布隆过滤器，先是过滤了敏感和没有营养的词汇，然后才可能会去查询数据库，如果关键字没有被`Redis`采录，它会写入到`Redis`中，以维护热度搜索，然后依次按照文章类名、标题、简述、内容去匹配，前面匹配到了，就停止去匹配后面的字段，就直接将匹配的文章查询出来。


### 架构交互

- `RPC`请求交互

为了实现远程调用服务和服务的横向扩展，服务端和客户端不采用直连方式，而采用注册中心注册和获取服务的主机名和端口，通过该端口来进行`netty`数据通信。

项目使用了自己开源的微服务框架`rpc-netty-framework`，可移步[mvnrepository.com](https://mvnrepository.com)、[search.maven.org](https://search.maven.org/) 获取。

- 高可用服务

为了达到可用性，博客管理有多种服务提供，将多种服务由不同的接口来提供，各个服务独立，之间不产生依赖性，这样一旦一台服务宕机，其他服务依旧可以为用户提供服务。

在使用云服务器搭建时，由于无法预测服务宕机的时机，最简易的监控行为就是利用`linux`的任务计划`contab`制定定时计划，按时间频率定时检测开启的服务是否还在运行，来自动开启服务。

使用`nginx`搭建反向代理和负载均衡：

```cnf
http {
        upstream blogapi {
            server localhost:8081;
            ## 可扩展代理服务，通过负载均衡实现可靠性和可用性
            #server localhost:8082;
            #server localhost:8083;
        }
        # 反向代理，对 swagger-ui 不友好
        server {
            listen    9001;
            server_name localhost;
            # Reverse proxy backend server
            location / {
                root html;
                index index.html idex.htm;
                proxy_pass http://blogapi;
            }
        }
}
```

### 项目亮点

- 阅读定时同步

阅读量不会即时更新到 `mongodb`,但会及时更新到 `redis` 缓存起来
开启定时任务，使用合适的策略将数据同步到 `mongodb` 中。

**实现**

用户阅读一篇文章时，在所调用的 `api`接口获取该请求所在的`ip`地址和 文章`id`,这样下一步就可以做到将该`ip`和文章`id`保存到`Redis`中，能够避免短时间内重复阅读同一篇文章而额外去执行一次 数据库`IO`操作，可以在`Redis`设置该`key`存活时间为 1 小时。

这样，我们就实现了即时将数据暂存到了`Redis`中，接着我们只需开启一个子线程获取`Redis`中每篇文章的`id`值和阅读数，将其同步到`MongoDB`就可以了，实现可以参考`ScheduledExecutorService`的`scheduleAtFixedRate()`方法。


- 热度搜索

热度搜索利用的是 `Redis`的两大数据结构`zSet`和`Hash`来实现存储近期和搜索量的，从而实现热度搜索，当然搜索前必须得先对敏感关键字过滤，切忌让敏感关键字作为了热搜词汇，导致出现敏感文章被顶推上热搜。

**实现**

热度搜索需要在前端做一个搜索，接着调用`api`将用户的搜索词汇放到`Redis`中，可以使用`zSet`，把时间戳当成`zSet`的权值，所以是有序的，可以获取近期的搜索，实现近期搜索（这里还可以将该关键字连同`userId`放到`Redis`做用户搜索记录），接着再使用`Redis`的`Hash`数据结构，将关键字作为`key`，`value`作为关键字搜索量，就能够做到近期热度搜索了。

- 支持多表单多文件与`json`数据上传

传统表单无法同时支持请求头`Content-Type`中`application/json`与`multipart/form-data`请求，不用去解决类似`WebKitFormBoundary`、Convert转换器不支持复杂问题。

**实现**

使用`multipart/form-data`，在需要同时上传`json`数据，先避开`boundary`问题，对于转化器转换失败问题，自定义转换器实现`Converter<入参类型, 出参类型>`接口，重写其方法`<出参类型> convert(<入参类型>)`

直接传`String`类型，在后端自己格式转换，将`String`转成自己需要的任意类型，前端只需按照`json`形式传递即可。

利用`SpringBoot`面向切面编程，入参直接写上转换后的对象。

> 例如：

转换器
```java
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
```
控制器
```java
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public BlogJSONResult upload(@RequestParam String userId, @RequestParam(value = "pictureBOs") List<PictureForUploadBO> pictureBOs,
            @RequestPart(value = "file")  /**下面注解不推荐使用，会导致 swagger2 文件上传按钮失效*/
            /**@RequestParam(value = "file")  
                                 @ApiParam(name = "file", value = "图片", allowMultiple = true) 支持 swagger2 接口文档测试**/ 
            MultipartFile[] file) throws Exception{
    }
```

这样便能实现多文件上传并且每个文件对号配置一些信息参数。

### 问题修复

1. 项目部署自定义注解失效；
2. 反射代理创建对象导致`springboot`依赖注入失败；
3. 真实分布式场景下注册中心服务发现不可用；
4. 日志集成兼容问题；
5. `RPC`整合`springboot`端口双重绑定；
6. 客户端反射代理服务结果一致性问题（RPC如何判定数据完整性）；
7. 解决大数据网络传输失败的问题（fastjson、kryo无法解决，推荐采用hessian作为序列化方式）；
8. 解决重试超时抛出异常导致客户端处于结果不一致性的问题（采用advice监听所有异常，并以正常结果和异常信息返回，保证各种结果一致性）；
9. 添加令牌访问，使得各种操作更加合理和高效（解决恶意的接口攻击）