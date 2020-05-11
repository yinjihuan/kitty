package com.cxytiandi.kitty.cat.autoconfigure;

import com.cxytiandi.kitty.cat.CatClientInit;
import com.cxytiandi.kitty.cat.aspect.CatTransactionAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cat Client自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 19:31
 */
@Configuration
public class CatClientAutoConfiguration {

    @Value("${spring.application.name:unknown}")
    private String domain;

    @Value("${cat.servers:}")
    private String servers;

    @Bean
    public CatClientInit catClientInit() {
        return new CatClientInit(domain, servers);
    }

    @Bean
    public CatTransactionAspect catTransactionAspect() {
        return new CatTransactionAspect();
    }

}