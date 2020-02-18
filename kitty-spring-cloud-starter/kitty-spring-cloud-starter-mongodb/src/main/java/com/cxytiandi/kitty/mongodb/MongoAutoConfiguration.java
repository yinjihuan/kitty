package com.cxytiandi.kitty.mongodb;

import com.cxytiandi.kitty.db.mongodb.spring.MongoTemplateAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mongo自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-17 19:45
 */
@Configuration
public class MongoAutoConfiguration {

    @Bean
    public MongoTemplateAspect mongoTemplateAspect() {
        return new MongoTemplateAspect();
    }

}