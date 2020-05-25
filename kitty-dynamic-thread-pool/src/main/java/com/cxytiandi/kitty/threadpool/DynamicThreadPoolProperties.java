package com.cxytiandi.kitty.threadpool;

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
     * 线程池配置
     */
    private List<ThreadPoolProperties> executors = new ArrayList<>();

}
