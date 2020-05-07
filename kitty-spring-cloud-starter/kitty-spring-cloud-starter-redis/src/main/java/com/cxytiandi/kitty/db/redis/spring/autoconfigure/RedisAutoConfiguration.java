package com.cxytiandi.kitty.db.redis.spring.autoconfigure;

import com.cxytiandi.kitty.db.redis.spring.RedisAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-27 21:55
 */
@Configuration
@AutoConfigureAfter({org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class})
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisAutoConfiguration {

    @Bean
    public RedisAspect openTracingRedisAspect() {
        return new RedisAspect();
    }

}