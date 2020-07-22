package com.cxytiandi.kitty.sentinel.autoconfigure;

import com.cxytiandi.kitty.sentinel.PathConfig;
import com.cxytiandi.kitty.sentinel.RestfulUrlCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:07
 */
@Configuration
@EnableConfigurationProperties(PathConfig.class)
public class KittySentinelAutoConfiguration {

    @Autowired
    private PathConfig pathConfig;

    @Bean
    public RestfulUrlCleaner restfulUrlCleaner() {
        return new RestfulUrlCleaner(pathConfig);
    }

}
