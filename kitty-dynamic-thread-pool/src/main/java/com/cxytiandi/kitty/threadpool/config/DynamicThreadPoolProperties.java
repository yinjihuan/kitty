package com.cxytiandi.kitty.threadpool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态线程池配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-24 20:36
 */
@Data
@ConfigurationProperties(prefix = "kitty.threadpools")
public class DynamicThreadPoolProperties {

    /**
     * Nacos DataId, 监听配置修改用
     */
    private String nacosDataId;

    /**
     * Nacos Group, 监听配置修改用
     */
    private String nacosGroup;

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

    /**
     * 线程池配置
     */
    private List<ThreadPoolProperties> executors = new ArrayList<>();

}
