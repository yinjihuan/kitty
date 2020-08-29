package com.cxytiandi.kitty.sentinel.autoconfigure;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.cxytiandi.kitty.sentinel.ApplicationContextHelper;
import com.cxytiandi.kitty.sentinel.alarm.SentinelBlockAlarm;
import com.cxytiandi.kitty.sentinel.properties.EarlyWarningProperties;
import com.cxytiandi.kitty.sentinel.properties.OriginParserProperties;
import com.cxytiandi.kitty.sentinel.properties.PathProperties;
import com.cxytiandi.kitty.sentinel.RestfulUrlCleaner;
import com.cxytiandi.kitty.sentinel.SentinelRequestOriginParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:07
 */
@Configuration
@EnableConfigurationProperties({PathProperties.class, OriginParserProperties.class, EarlyWarningProperties.class})
public class KittySentinelAutoConfiguration {

    @Autowired
    private PathProperties pathProperties;

    @Autowired
    private OriginParserProperties originParserProperties;

    @Bean
    public RestfulUrlCleaner restfulUrlCleaner() {
        return new RestfulUrlCleaner(pathProperties);
    }

    @PostConstruct
    public void init() {
        WebCallbackManager.setRequestOriginParser(new SentinelRequestOriginParser(originParserProperties));
    }

    @Bean("kittyApplicationContextHelper")
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    public SentinelBlockAlarm sentinelBlockAlarm() {
        return new SentinelBlockAlarm();
    }
}
