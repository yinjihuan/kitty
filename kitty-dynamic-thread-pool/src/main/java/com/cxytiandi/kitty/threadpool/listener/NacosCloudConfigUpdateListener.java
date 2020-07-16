package com.cxytiandi.kitty.threadpool.listener;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cxytiandi.kitty.threadpool.DynamicThreadPoolManager;
import com.cxytiandi.kitty.threadpool.config.DynamicThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * Spring Cloud Alibaba Nacos配置修改监听
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-18 23:06
 */
@Slf4j
public class NacosCloudConfigUpdateListener {

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties poolProperties;


    @PostConstruct
    public void init() {
        initConfigUpdateListener();
    }

    public void initConfigUpdateListener() {
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        Assert.hasText(poolProperties.getNacosDataId(), "请配置kitty.threadpools.nacosDataId");
        Assert.hasText(poolProperties.getNacosGroup(), "请配置kitty.threadpools.nacosGroup");

        try {
            configService.addListener(poolProperties.getNacosDataId(), poolProperties.getNacosGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    new Thread(() -> dynamicThreadPoolManager.refreshThreadPoolExecutor(true)).start();
                    log.info("线程池配置有变化，刷新完成");
                }
            });
        } catch (NacosException e) {
            log.error("Nacos配置监听异常", e);
        }
    }

}
