package com.cxytiandi.kitty.sentinel.alarm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-31 21:17
 */
@Data
@ConfigurationProperties(prefix = "sentinel.block.alarm")
public class SentinelBlockAlarmProperties {

    /**
     * 异常告警API地址，如果配置了此值就将异常信息告诉配置的API,异常告警由外部系统处理
     * 默认会通过钉钉告警，需要配置钉钉信息
     */
    private String alarmApiUrl;

    /**
     * 钉钉机器人access_token
     */
    private String accessToken;

    /**
     * 钉钉机器人secret
     */
    private String secret;

    /**
     * 告警时间间隔，单位分钟
     */
    private int alarmTimeInterval = 1;

    /**
     * 负责人
     */
    private String owner;

}
