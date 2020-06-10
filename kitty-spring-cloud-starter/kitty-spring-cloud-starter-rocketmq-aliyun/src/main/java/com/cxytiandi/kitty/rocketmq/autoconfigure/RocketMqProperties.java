package com.cxytiandi.kitty.rocketmq.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ 配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-07 15:50
 */
@Data
@ConfigurationProperties(prefix = "kitty.rocketmq.aliyun")
public class RocketMqProperties {

    private String accessKey;

    private String secretKey;

    private String nameServiceAddress;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 集群订阅方式: CLUSTERING 广播订阅方式: BROADCASTING
     */
    private String messageModel = "CLUSTERING";

    /**
     * 设置 Consumer 实例的消费线程数，默认值：20。
     */
    private Integer consumeThreadNums = 20;

    /**
     * 设置消息消费失败的最大重试次数，默认值：16。
     */
    private Integer maxReconsumeTimes = 16;

    /**
     * 设置每条消息消费的最大超时时间，超过设置时间则被视为消费失败，等下次重新投递再次消费。每个业务需要设置一个合理的值，默认值：15，单位：分钟 。
     */
    private Integer consumeTimeout = 15;

    /**
     * 只适用于顺序消息，设置消息消费失败的重试间隔时间。
     */
    private Long suspendTimeMillis;

    /**
     * 客户端本地的最大缓存消息数据，默认值：1000，单位：条。
     */
    private Integer maxCachedMessageAmount = 1000;



}
