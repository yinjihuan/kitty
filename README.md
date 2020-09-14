Spring Cloud &amp; Spring Cloud Alibaba 基础框架，内置了 Cat 监控，互联网公司落地 Spring Cloud 架构必备。

## 组件列表

**kitty-spring-cloud-starter-cat**：Cat 监控组件。

**kitty-spring-cloud-starter-web**：spring-boot-starter-web的封装，会对请求的Url进行Cat埋点，会对一些通用信息进行接收透传，会对RestTemplate的调用进行Cat埋点。

**kitty-spring-cloud-starter-dubbo**：Dubbo组件，调用监控，Cat监控信息传递。

**kitty-spring-cloud-starter-dynamic-thread-pool**：动态线程池组件，支持动态修改线程池的参数和Cat监控告警。

**kitty-spring-cloud-starter-elasticsearch**：Elasticsearch封装。

**kitty-spring-cloud-starter-feign**：Feign组件, 内置Cat调用监控，Cat监控信息传递。

**kitty-spring-cloud-starter-gateway-zuul**：Zuul组件，内置Cat调用监控，Cat监控信息传递。

**kitty-spring-cloud-starter-id**：ID发射器客户端，基于美团Leaf。

**kitty-spring-cloud-starter-jetcache**：缓存框架JetCache组件，内置Cat调用监控。

**kitty-spring-cloud-starter-lock**：分布式锁组件（内置幂等注解），基于Redisson的Redis锁和Mysql的锁。

**kitty-spring-cloud-starter-mongodb**：MongoDB客户端的封装，内置Cat调用监控。

**kitty-spring-cloud-starter-mybatis**：基于Mybatis-Plus，内置Cat调用监控。

**kitty-spring-cloud-starter-nacos**：Nacos组件，内置Cat调用监控。

**kitty-spring-cloud-starter-redis**：Redis客户端，内置Cat调用监控。

**kitty-spring-cloud-starter-rocketmq-aliyun**：阿里云RocketMQ封装，内置Cat调用监控，本地事务消息。

**kitty-spring-cloud-starter-sentinel**：熔断限流。

**kitty-spring-cloud-starter-sleuth**：链路跟踪。

**kitty-spring-cloud-starter-xxljob**：分布式任务调度。


## 使用文档

- [Cat集成指南](https://github.com/yinjihuan/kitty/wiki/Cat%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)

- [Spring-Boot-Web-集成指南](https://github.com/yinjihuan/kitty/wiki/Spring-Boot-Web-%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)

- [Mybatis集成指南](https://github.com/yinjihuan/kitty/wiki/Mybatis-%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)
  
- [MongoDB集成指南](https://github.com/yinjihuan/kitty/wiki/MongoDB%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)

- [Redis集成指南](https://github.com/yinjihuan/kitty/wiki/Spring-Redis%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)
  
- [JetCache集成指南](https://github.com/yinjihuan/kitty/wiki/JetCache%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97)

## 源码分析
  
- [动态线程池原理分析](https://mp.weixin.qq.com/s/JM9idgFPZGkRAdCpw0NaKw)
  
- [动态线程池对接Nacos,Apollo多配置](https://mp.weixin.qq.com/s/xoN_eUyL4Dy1-UvLn3grqg)


## 使用案例

组件使用案例：https://github.com/yinjihuan/kittysamples

完整的使用案例可以参考Kitty Cloud这个项目，地址：https://github.com/yinjihuan/kitty-cloud

## 支持

有任何问题可以直接提issues或者直接加我个人微信 jihuan900。

# 公众号

公众号 ***猿天地*** 会持续更新Kitty Cloud 和 微服务相关技术文章，请关注。技术交流群请加我微信jihuan900

![](https://github.com/yinjihuan/kitty-cloud/blob/master/doc/images/2685774-17a60e1ead7fd232.png)
